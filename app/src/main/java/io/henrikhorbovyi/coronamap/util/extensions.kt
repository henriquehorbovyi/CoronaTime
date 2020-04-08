package io.henrikhorbovyi.coronamap.util

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.lang.Exception
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * .:.:.:. Created by @henrikhorbovyi on 3/31/20 .:.:.:.
 */

fun LifecycleOwner.ifHigherThanVersion(sure: () -> Unit, no: () -> Unit = {}) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        sure()
    else
        no()
}


/* VIEW */
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.findColor(colorResource: Int) =
    ContextCompat.getColor(context, colorResource)

fun Activity.findColor(colorResource: Int) =
    ContextCompat.getColor(this, colorResource)


fun <T : ViewGroup> BottomSheetBehavior<T>.behaviorListener(
    onSlide: (bottomSheet: View, slideOffset: Float) -> Unit = { _, _ -> },
    onStateChange: (bottomSheet: View, newState: Int) -> Unit = { _, _ -> }
) {

    val callback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            onSlide(bottomSheet, slideOffset)
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            onStateChange(bottomSheet, newState)
        }
    }
    this.addBottomSheetCallback(callback)
}

/* Lifecycle & LiveData */
fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, onChange: (T) -> Unit) {
    liveData.observe(this, Observer(onChange))
}


/* Masks & Formaters */
fun Int.formattedNumber(): String {
    return NumberFormat.getInstance(Locale.US).format(this)
}

fun String.asHumanDate(): String {
    return try {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss", Locale.US)
        val date: Date? = dateFormatter.parse(this)
        date?.run {
            SimpleDateFormat.getDateInstance().format(date)
        }.toString()
    } catch (e: Exception) {
        try {
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val date: Date? = dateFormatter.parse(this)
            date?.run {
                SimpleDateFormat.getDateInstance().format(date)
            }.toString()
        } catch (e: Exception) {
            "-"
        }
    }
}

fun Date.asHumanDate(): String {
    return try {
        val dateFormatter = SimpleDateFormat.getDateInstance()
        dateFormatter.format(this)
    } catch (e: Exception) {
        "-"
    }
}

fun String.asDateTime(): Date {
    val dateFormatter = SimpleDateFormat("MM/dd/yy", Locale.US)
    val date: Date = dateFormatter.parse(this) ?: Date()
    return date
}
