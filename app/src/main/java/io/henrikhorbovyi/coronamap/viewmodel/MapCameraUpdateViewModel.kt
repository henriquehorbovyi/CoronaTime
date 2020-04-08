package io.henrikhorbovyi.coronamap.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng

/**
 * .:.:.:. Created by @henrikhorbovyi on 4/1/20 .:.:.:.
 */
class MapCameraUpdateViewModel : ViewModel() {

    private val cameraUpdate: MutableLiveData<CameraUpdate> = MutableLiveData()
    val cameraUpdateObservable: LiveData<CameraUpdate>
        get() = cameraUpdate

    fun updateCamera(location: Location?, zoom: Float = 4.5f) {
        location?.let {
            val latLng = LatLng(location.latitude, location.longitude)
            cameraUpdate.postValue(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        }
    }
}