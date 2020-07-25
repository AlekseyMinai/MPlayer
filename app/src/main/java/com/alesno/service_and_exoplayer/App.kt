package com.alesno.service_and_exoplayer

import android.app.Application
import com.alesno.service_and_exoplayer.di.ApplicationComponent


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ApplicationComponent.init(this)
    }
}