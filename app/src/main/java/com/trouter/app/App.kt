package com.trouter.app

import android.app.Application
import com.tokyonth.trouter.core.TRouter

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        TRouter.init(this)
    }

}
