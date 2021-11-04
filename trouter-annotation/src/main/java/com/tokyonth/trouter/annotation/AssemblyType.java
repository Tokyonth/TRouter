package com.tokyonth.trouter.annotation;

public enum AssemblyType {

    ACTIVITY(0, "android.app.Activity"),
    FRAGMENT(1, "android.app.Fragment");

    int id;
    String className;

    public String getClassName() {
        return className;
    }

    AssemblyType(int id, String className) {
        this.id = id;
        this.className = className;
    }

}
