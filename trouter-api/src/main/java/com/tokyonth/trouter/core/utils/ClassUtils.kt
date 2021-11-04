package com.tokyonth.trouter.core.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import kotlin.Throws
import android.content.pm.PackageManager
import dalvik.system.DexFile
import android.util.Log
import com.tokyonth.trouter.core.Constants
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.NumberFormatException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.regex.Pattern

object ClassUtils {

    private val SECONDARY_FOLDER_NAME = "code_cache" + File.separator + "secondary-dexes"

    @Throws(
        PackageManager.NameNotFoundException::class,
        IOException::class,
        InterruptedException::class
    )
    fun getRouterClass(context: Context): Set<String> {
        val classNames: MutableSet<String> = HashSet()
        val paths = getSourcePaths(context)
        val parserCtl = CountDownLatch(paths.size)
        for (path in paths) {
            var dexfile: DexFile? = null
            try {
                dexfile = if (path.endsWith(Constants.EXTRACTED_SUFFIX)) {
                    //NOT use new DexFile(path), because it will throw "permission error in /data/dalvik-cache"
                    DexFile.loadDex(path, "$path.tmp", 0)
                } else {
                    DexFile(path)
                }
                val dexEntries = dexfile!!.entries()
                while (dexEntries.hasMoreElements()) {
                    val className = dexEntries.nextElement()
                    if (className.startsWith(Constants.ROUTER_ROOT_PACKAGE_NAME)) {
                        classNames.add(className)
                    }
                }
            } catch (ignore: Throwable) {
                Log.e("ARouter", "Scan map file in dex files made error.", ignore)
            } finally {
                if (null != dexfile) {
                    try {
                        dexfile.close()
                    } catch (ignore: Throwable) {
                    }
                }
                parserCtl.countDown()
            }
        }
        parserCtl.await()

        Log.d(
            "TRouter-->",
            "Filter " + classNames.size + " classes by packageName <" + Constants.ROUTER_ROOT_PACKAGE_NAME + ">"
        )
        return classNames
    }

    private fun getMultiDexPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            Constants.PREFS_FILE,
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) Context.MODE_PRIVATE else Context.MODE_PRIVATE or Context.MODE_MULTI_PROCESS
        )
    }

    @Throws(PackageManager.NameNotFoundException::class, IOException::class)
    private fun getSourcePaths(context: Context): List<String> {
        val applicationInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        val sourceApk = File(applicationInfo.sourceDir)
        val sourcePaths: MutableList<String> = ArrayList()
        sourcePaths.add(applicationInfo.sourceDir) //add the default apk path

        //the prefix of extracted file, ie: test.classes
        val extractedFilePrefix = sourceApk.name + Constants.EXTRACTED_NAME_EXT

//        如果VM已经支持了MultiDex，就不要去Secondary Folder加载 Classesx.zip了，那里已经么有了
//        通过是否存在sp中的multidex.version是不准确的，因为从低版本升级上来的用户，是包含这个sp配置的
        if (!isVMMultidexCapable) {
            //the total dex numbers
            val totalDexNumber = getMultiDexPreferences(context).getInt(Constants.KEY_DEX_NUMBER, 1)
            val dexDir = File(applicationInfo.dataDir, SECONDARY_FOLDER_NAME)
            for (secondaryNumber in 2..totalDexNumber) {
                //for each dex file, ie: test.classes2.zip, test.classes3.zip...
                val fileName = extractedFilePrefix + secondaryNumber + Constants.EXTRACTED_SUFFIX
                val extractedFile = File(dexDir, fileName)
                if (extractedFile.isFile) {
                    sourcePaths.add(extractedFile.absolutePath)
                    //we ignore the verify zip part
                } else {
                    throw IOException("Missing extracted secondary dex file '" + extractedFile.path + "'")
                }
            }
        }

        /* if (ARouter.debuggable()) { // Search instant run support only debuggable
            sourcePaths.addAll(tryLoadInstantRunDexFile(applicationInfo));
        }*/return sourcePaths
    }// let isMultidexCapable be false// 非YunOS原生Android

    // YunOS需要特殊判断
    private val isVMMultidexCapable: Boolean
        get() {
            var isMultidexCapable = false
            var vmName: String? = null
            try {
                if (isYunOS) {    // YunOS需要特殊判断
                    vmName = "'YunOS'"
                    isMultidexCapable =
                        Integer.valueOf(System.getProperty("ro.build.version.sdk")) >= 21
                } else {    // 非YunOS原生Android
                    vmName = "'Android'"
                    val versionString = System.getProperty("java.vm.version")
                    if (versionString != null) {
                        val matcher =
                            Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(versionString)
                        if (matcher.matches()) {
                            try {
                                val major = matcher.group(1).toInt()
                                val minor = matcher.group(2).toInt()
                                isMultidexCapable =
                                    (major > Constants.VM_WITH_MULTIDEX_VERSION_MAJOR
                                            || (major == Constants.VM_WITH_MULTIDEX_VERSION_MAJOR
                                            && minor >= Constants.VM_WITH_MULTIDEX_VERSION_MINOR))
                            } catch (ignore: NumberFormatException) {
                                // let isMultidexCapable be false
                            }
                        }
                    }
                }
            } catch (ignore: Exception) {
            }
            Log.i(
                "打印-->",
                "VM with name " + vmName + if (isMultidexCapable) " has multidex support" else " does not have multidex support"
            )
            return isMultidexCapable
        }

    /**
     * 判断系统是否为YunOS系统
     */
    private val isYunOS: Boolean
        get() = try {
            val version = System.getProperty("ro.yunos.version")
            val vmName = System.getProperty("java.vm.name")
            (vmName != null && vmName.lowercase(Locale.getDefault()).contains("lemur")
                    || version != null && version.trim { it <= ' ' }.isNotEmpty())
        } catch (ignore: Exception) {
            false
        }

}
