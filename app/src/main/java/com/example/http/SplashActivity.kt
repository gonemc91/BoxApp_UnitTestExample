package com.example.http

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nav_components_2_tabs_exercise.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * Entry point of the app.
 *
 * Splash activity contains only window background, all other initialization logic is placed to
 * [SplashFrgment] and [SplashViewModel].
 *
 */
@AndroidEntryPoint
class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //Singletons.init(applicationContext)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}