package com.sample.huutho.utils

import android.app.Application

class App : Application() {


    companion object {
        lateinit var app: App
        fun getInstance() = app
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}