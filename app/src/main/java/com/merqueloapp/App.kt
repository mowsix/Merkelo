package com.merqueloapp

import android.app.Application
import org.osmdroid.config.Configuration

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // User-Agent recomendado por OSM (con el package name de tu app)
        Configuration.getInstance().userAgentValue = applicationContext.packageName
    }
}
