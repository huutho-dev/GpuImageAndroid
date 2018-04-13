package com.sample.huutho.utils

import android.app.Application
import android.content.SharedPreferences

class App : Application() {


    companion object {
        lateinit var app: App
        fun getInstance() = app

        lateinit var prefHelper: SharedPreferences
            private set
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        prefHelper = PreferenceHelper.defaultPrefs(this)
    }


    private fun fuckingShit(stickers: List<String>) {

        val listSticker = prefHelper.getStringSet("123", mutableSetOf())
        listSticker.forEachIndexed({ _, s ->
            if (listSticker.contains(s)) {
                listSticker.remove(s)
                listSticker.add(s)
            } else {
                listSticker.add(s)
            }
        })
    }

}