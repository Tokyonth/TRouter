package com.tokyonth.trouter.core

import android.app.Application
import com.tokyonth.trouter.annotation.TRouteMeta
import com.tokyonth.trouter.core.utils.ClassUtils
import com.tokyonth.trouter.core.utils.doAsync

class TRouterParser {

    companion object {

        private var findClassMap = HashMap<String, TRouteMeta>()

        fun initParser(application: Application) {
            doAsync {
                val findClass = ClassUtils.getRouterClass(application)
                for (classStr in findClass) {
                    val iClass = Class.forName(classStr)
                    val find = iClass.newInstance() as TRouterInject
                    find.onLoader(findClassMap)
                }
            }
        }

        fun getFindClassHolder(): HashMap<String, TRouteMeta> {
            return findClassMap
        }

    }

}
