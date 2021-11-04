package com.tokyonth.trouter.annotation;

public class TRouteMeta {

    private AssemblyType assemblyType;
    private String path;
    private String className;

    public TRouteMeta(AssemblyType assemblyType, String path, String className) {
        this.assemblyType = assemblyType;
        this.path = path;
        this.className = className;
    }

    public AssemblyType getAssemblyType() {
        return assemblyType;
    }

    public void setAssemblyType(AssemblyType assemblyType) {
        this.assemblyType = assemblyType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
