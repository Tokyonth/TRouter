package com.tokyonth.trouter.core

import android.app.Application
import com.tokyonth.trouter.core.data.ClassHolder
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

    fun addFiled(vararg any: Any): TRouter {
        TRouterEngine.getInstance().addFiled(any).build()
        return this
    }

    fun navigation() {
        TRouterEngine.getInstance().navigation()
    }

}
