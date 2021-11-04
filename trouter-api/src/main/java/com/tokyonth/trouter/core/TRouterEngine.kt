package com.tokyonth.trouter.core

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat
import com.tokyonth.trouter.core.data.ClassHolder

class TRouterEngine {

    companion object {

        private lateinit var application: Application

        private var instance: TRouterEngine? = null

        private var holder = ClassHolder()

        fun initEngine(application: Application) {
            this.application = application
        }

        fun getInstance(): TRouterEngine {
            synchronized(TRouterEngine::class.java) {
                if (instance == null) {
                    instance = TRouterEngine()
                }
                return instance!!
            }
        }

    }

    fun setPath(path: String): TRouterEngine {
        holder.setPath(path)
        return this
    }

    fun addFiled(vararg any: Any): TRouterEngine {

        return this
    }

    fun navigation() {
        if (holder.getPath().isNotEmpty()) {
            val findClassArr = TRouterParser.getFindClassHolder()
            for (classKey in findClassArr.keys) {
                if (holder.getPath() == classKey) {
                    val intent = Intent().apply {
                        setClassName(application.packageName, findClassArr[classKey]!!.className)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    ActivityCompat.startActivity(application, intent, null)
                }
            }
        }
    }

    fun build(): ClassHolder {
        return holder
    }

}
