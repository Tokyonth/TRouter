package com.tokyonth.trouter.core

import android.app.Application
import java.lang.RuntimeException

class TRouter {

    companion object {

        private var isInit = false

        private var instance: TRouter? = null

        fun init(application: Application) {
            if (!isInit) {
                isInit = true
                TRouterParser.initParser(application)
                TRouterEngine.initEngine(application)
            }
        }

        fun getInstance(): TRouter {
            if (!isInit) {
                throw RuntimeException("TRouter not init!")
            } else {
                synchronized(TRouter::class.java) {
                    if (instance == null) {
                        instance = TRouter()
                    }
                    return instance!!
                }
            }
        }

    }

    fun setPath(path: String): TRouter {
        TRouterEngine.getInstance().setPath(path).build()
        return this
    }

    fun addStringField(key: String, value: String): TRouter {
        TRouterEngine.getInstance().addStringField(key, value).build()
        return this
    }

    fun navigation() {
        TRouterEngine.getInstance().navigation()
    }

}
