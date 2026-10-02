package com.example

import android.app.Application
import com.example.data.local.AppDatabase

class ApexAiApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: ApexAiApp
            private set
    }
}
