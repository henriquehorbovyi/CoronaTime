package io.henrikhorbovyi.coronamap.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.henrikhorbovyi.coronamap.R
import io.henrikhorbovyi.coronamap.remote.Status

/**
 * .:.:.:. Created by @henrikhorbovyi on 4/1/20 .:.:.:.
 */

class MapMarkerBuilder(
    private val context: Context,
    private val markerStrategy: (Status) -> Pair<String, Int> = { "" to 0 }
) {

    sealed class Strategy(private val value: (Status) -> Pair<String, Int> = { "" to 0 }) {

        operator fun invoke() = value

        object Infected : Strategy({ it.confirmed.toString() to R.color.blue })
        object Deaths : Strategy({ it.deaths.toString() to R.color.red })
        object Recovered : Strategy({ it.recovered.toString() to R.color.green })
    }

    fun build(
        latLng: LatLng,
        status: Status,
        title: String = ""
    ): MarkerOptions? {
        val markerView: View =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.custom_marker, null)

        val (amount: String, markerColor: Int) = markerStrategy(status)

        val numberTextView = markerView.findViewById<TextView>(R.id.markerText)
        val markerCard = markerView.findViewById<CardView>(R.id.customMarkerCard)
        numberTextView.text = amount
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            markerCard.setCardBackgroundColor(markerCard.findColor(markerColor))
        }

        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        markerView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        markerView.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        markerView.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        markerView.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(
            markerView.measuredWidth,
            markerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        markerView.draw(canvas)

        return MarkerOptions()
            .position(latLng)
            .title(title)
            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
    }

}
