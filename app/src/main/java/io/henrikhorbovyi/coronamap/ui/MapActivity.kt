package io.henrikhorbovyi.coronamap.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.henrikhorbovyi.coronamap.R
import io.henrikhorbovyi.coronamap.remote.GlobalInfo
import io.henrikhorbovyi.coronamap.remote.Historical
import io.henrikhorbovyi.coronamap.remote.asLatLng
import io.henrikhorbovyi.coronamap.util.*
import io.henrikhorbovyi.coronamap.viewmodel.FetchHistoricalViewModel
import io.henrikhorbovyi.coronamap.viewmodel.GlobalInfoViewModel
import io.henrikhorbovyi.coronamap.viewmodel.GlobalStatusViewModel
import io.henrikhorbovyi.coronamap.viewmodel.MapCameraUpdateViewModel
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*


class MapActivity : AppCompatActivity(), OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private val globalStatusViewModel: GlobalStatusViewModel by viewModel()
    private val globalInfoViewModel: GlobalInfoViewModel by viewModel()
    private val historicalViewModel: FetchHistoricalViewModel by viewModel()
    private val mapCameraUpdateViewModel: MapCameraUpdateViewModel by viewModel()
    private val progressDialog: ProgressDialogFragment by lazy { ProgressDialogFragment() }
    private var globalMap: GoogleMap? = null
    private var resultHasArrived: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setObservers()
        bottomSheetBehavior()
        setInfoCardClickListeners()
        obtainUserCurrentLocation()
        showProgress()
        globalStatusViewModel.fetchStatus()
    }

    override fun onResume() {
        super.onResume()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        if (resultHasArrived)
            hideProgress()

    }

    override fun onMapReady(map: GoogleMap?) {
        globalMap = map
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            obtainUserCurrentLocation()
        } else {
            AlertDialog.Builder(this)
                .setMessage("Your permission is really important to show COVID-19 cases near to you. Would you like to allow the access to your location?")
                .setPositiveButton(R.string.allow) { _, _ -> requestLocationPermission() }
                .setNegativeButton(R.string.no) { _, _ -> globalInfoViewModel.fetchInfo() }
                .show()
        }
    }

    private fun requestLocationPermission() {
        ifHigherThanVersion(sure = {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                FINE_LOCATION_PERMISSION_CODE
            )
        })
    }

    private fun obtainUserCurrentLocation() {
        ifHigherThanVersion(sure = {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestLocationPermission()
                return@ifHigherThanVersion
            }
        })


        try {

            LocationServices.getFusedLocationProviderClient(this@MapActivity)
                .lastLocation
                .addOnSuccessListener {
                    Timber.i("LOCATION (success) $it")
                    mapCameraUpdateViewModel.updateCamera(it)
                    globalInfoViewModel.fetchInfo()
                }
        } catch (e: java.lang.Exception) {
            Timber.i("LOCATION (error) $e")
            globalInfoViewModel.fetchInfo()
        }
    }

    private fun bottomSheetBehavior() {
        val sheetBehavior: BottomSheetBehavior<ConstraintLayout> =
            BottomSheetBehavior.from(globalInfoBottomSheet)
        sheetBehavior.behaviorListener(onStateChange = { _, newState ->
            when (newState) {
                BottomSheetBehavior.STATE_HIDDEN -> sheetBehavior.state =
                    BottomSheetBehavior.STATE_COLLAPSED
            }
        })
    }

    private fun setObservers() {
        observe(globalStatusViewModel.globalStatusObservable) { result ->
            result.onSuccess { globalInfo ->
                globalInfo.apply {
                    globalInfected.text = cases.formattedNumber()
                    globalDeaths.text = deaths.formattedNumber()
                    globalRecovered.text = recovered.formattedNumber()
                }
            }.onFailure {}
        }
        observe(globalInfoViewModel.globalInfoObservable) { result ->
            resultHasArrived = true
            result
                .onSuccess { markAllLocations(it) }
                .onFailure { hideProgress() }
        }

        observe(historicalViewModel.historicalObservable) { result ->
            result.onSuccess {
                plotDataOnGraphs(it)
            }.onFailure {
                println("HISTORICAL: error $it")
            }
        }

        observe(mapCameraUpdateViewModel.cameraUpdateObservable) { cameraUpdate ->
            globalMap?.animateCamera(cameraUpdate)
        }
    }

    private fun setInfoCardClickListeners() {
        globalInfectedCard.setOnClickListener {
            onInfoCardClicked(MapMarkerBuilder.Strategy.Infected)
        }
        globalDeathsCard.setOnClickListener {
            onInfoCardClicked(MapMarkerBuilder.Strategy.Deaths)
        }
        globalRecoveredCard.setOnClickListener {
            onInfoCardClicked(MapMarkerBuilder.Strategy.Recovered)
        }
    }

    private fun onInfoCardClicked(markerStrategy: MapMarkerBuilder.Strategy) {
        resultHasArrived = false
        showProgress()
        try {
            val result: Result<List<GlobalInfo>>? = globalInfoViewModel.globalInfoObservable.value
            requireNotNull(result)
            markAllLocations(result.getOrDefault(listOf()), markerStrategy)
        } catch (e: Exception) {
        }
    }

    private fun markAllLocations(
        globalInfo: List<GlobalInfo>,
        markerStrategy: MapMarkerBuilder.Strategy = MapMarkerBuilder.Strategy.Infected
    ) {
        lifecycleScope.launch {
            val mappedMarkers = withContext(Dispatchers.IO) {
                mapGlobalInfoToMarker(globalInfo, markerStrategy)
            }
            globalMap?.clear()
            globalMap?.let { map ->
                mappedMarkers.forEach { marker -> marker?.let { map.addMarker(it) } }
            }
            hideProgress()
        }
    }

    private fun mapGlobalInfoToMarker(
        globalInfo: List<GlobalInfo>,
        markerStrategy: MapMarkerBuilder.Strategy
    ): List<MarkerOptions?> {
        return globalInfo.map { info ->
            val markerBuilder = MapMarkerBuilder(context = this, markerStrategy = markerStrategy())

            info.coordinates.asLatLng()?.let { latLng ->
                markerBuilder.build(
                    latLng = latLng,
                    status = info.stats,
                    title = (info.province
                        ?: info.country).plus(" (${info.updatedAt.asHumanDate()})")
                )
            }
        }
    }

    private fun showProgress() {
        if (!progressDialog.isResumed)
            progressDialog.show(supportFragmentManager, ProgressDialogFragment.TAG)
    }

    private fun hideProgress() {
        if (progressDialog.isAdded)
            progressDialog.dismissAllowingStateLoss()
    }


    private fun plotDataOnGraphs(historical: Historical) {

        val dateDistinction: (String) -> Int = {
            val calendar = Calendar.getInstance().apply { time = it.asDateTime() }
            calendar.get(Calendar.MONTH).plus(1)
        }
        val caseDates = historical
            .cases
            .distinctBy { dateDistinction(it.date) }
            .map { it.date.asDateTime().asHumanDate() }


        val cases = extractHistorical(historical, dateDistinction, 1)
        val deaths = extractHistorical(historical, dateDistinction, 2)
        val recovered = extractHistorical(historical, dateDistinction, 3)

        val casesSet = LineDataSet(
            cases,
            getString(R.string.global_info_card_infected_label)
        ).apply {
            color = findColor(R.color.blue)
            setCircleColor(findColor(R.color.blue))
            lineWidth = 4f
        }

        val deathsSet = LineDataSet(
            deaths,
            getString(R.string.global_info_card_deaths_label)
        ).apply {
            color = findColor(R.color.red)
            setCircleColor(findColor(R.color.red))
            lineWidth = 4f
        }


        val recoveredSet = LineDataSet(
            recovered,
            getString(R.string.global_info_card_recovered_label)
        ).apply {
            color = findColor(R.color.green)
            setCircleColor(findColor(R.color.green))
            lineWidth = 4f
        }

        globalCasesInfoChart.apply {
            data = LineData(casesSet, deathsSet, recoveredSet)
            axisRight.isEnabled = false
            description = Description().apply { text = "" }
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(caseDates)
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
            }
            invalidate()
        }
    }

    private fun extractHistorical(
        historical: Historical,
        dateDistinction: (String) -> Int,
        typeOfInfo: Int
    ): List<Entry> {

        return when (typeOfInfo) {
            1 -> {
                historical
                    .cases
                    .distinctBy { dateDistinction(it.date) }
                    .mapIndexed { index, cases -> Entry(index.toFloat(), cases.amount.toFloat()) }
            }
            2 -> {
                historical
                    .deaths
                    .distinctBy { dateDistinction(it.date) }
                    .mapIndexed { index, cases -> Entry(index.toFloat(), cases.amount.toFloat()) }
            }
            else -> {
                historical
                    .recovered
                    .distinctBy { dateDistinction(it.date) }
                    .mapIndexed { index, cases -> Entry(index.toFloat(), cases.amount.toFloat()) }
            }
        }
    }


    companion object {
        const val FINE_LOCATION_PERMISSION_CODE = 1000
    }
}
