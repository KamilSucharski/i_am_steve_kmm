package com.iamsteve

import android.app.Application
import util.Initializer

class AndroidApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Initializer.initialize()
    }

}