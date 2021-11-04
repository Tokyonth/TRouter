package com.tokyonth.trouter.core

import android.app.Application
import android.util.Log
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
                    Log.e("找到类--->", classStr)
                    val iClass = Class.forName(classStr)
                    val find = iClass.newInstance() as TRouterInject
                    find.onLoader(findClassMap)

                    for (key in findClassMap.keys) {
                        val vv = findClassMap[key]
                        Log.e(
                            "规则-->",
                            "大小:${findClassMap.size}  路径:${vv?.path}  类:${vv?.className}"
                        )
                    }
                }
            }
        }

        fun getFindClassHolder(): HashMap<String, TRouteMeta> {
            return findClassMap
        }

    }

}
