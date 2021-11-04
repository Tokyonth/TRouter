package com.tokyonth.trouter.core.data

class ClassHolder {

    private var path: String = ""
    private var classSource: String = ""

    fun getPath(): String {
        return path
    }

    fun getClassSource(): String {
        return classSource
    }

    fun setPath(path: String) {
        this.path = path
    }

    fun setClassSource(classSource: String) {
        this.classSource = classSource
    }

}
