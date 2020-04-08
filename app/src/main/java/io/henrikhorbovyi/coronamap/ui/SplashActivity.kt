package io.henrikhorbovyi.coronamap.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.henrikhorbovyi.coronamap.R

class SplashActivity : AppCompatActivity(R.layout.activity_splash) {

    override fun onResume() {
        super.onResume()
        startActivity(Intent(this, MapActivity::class.java))
        finish()
    }
}