package com.merqueloapp

import android.app.Application
import org.osmdroid.config.Configuration
import java.io.File

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // User-Agent recomendado (simple y sin BuildConfig para evitar imports raros)
        Configuration.getInstance().userAgentValue = applicationContext.packageName

        // Caché en almacenamiento interno (rápido y sin permisos extra)
        val base = File(cacheDir, "osmdroid")
        val tiles = File(base, "tiles")
        base.mkdirs()
        tiles.mkdirs()
        Configuration.getInstance().osmdroidBasePath = base
        Configuration.getInstance().osmdroidTileCache = tiles
    }
}
