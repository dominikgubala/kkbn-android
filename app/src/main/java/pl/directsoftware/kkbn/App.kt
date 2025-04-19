package pl.directsoftware.kkbn

import android.app.Application
import android.content.Context

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    val context: Context
        get() = applicationContext

    companion object {
        lateinit var instance: App
    }
}