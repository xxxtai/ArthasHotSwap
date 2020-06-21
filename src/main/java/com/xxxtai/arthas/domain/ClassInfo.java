package com.xxxtai.arthas.domain;

import com.xxxtai.arthas.constants.ClassIdentity;

public class ClassInfo {

    /**
     * @see ClassIdentity.Type
     */
    private String classType;

    private String projectBasePath;

    private String simpleName;

    private String qualifiedName;

    private String belongedModuleName;

    private String classPath;

    private String currentClassOssUrl;

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String getBelongedModuleName() {
        return belongedModuleName;
    }

    public void setBelongedModuleName(String belongedModuleName) {
        this.belongedModuleName = belongedModuleName;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getCurrentClassOssUrl() {
        return currentClassOssUrl;
    }

    public void setCurrentClassOssUrl(String currentClassOssUrl) {
        this.currentClassOssUrl = currentClassOssUrl;
    }

    public String getProjectBasePath() {
        return projectBasePath;
    }

    public void setProjectBasePath(String projectBasePath) {
        this.projectBasePath = projectBasePath;
    }

    @Override
    public String toString() {
        return "classInfo:{" +
                "classType: " + classType + ",\n" +
                "projectBasePath: " + projectBasePath + ",\n" +
                "simpleName: " + simpleName + ",\n" +
                "qualifiedName: " + qualifiedName + ",\n" +
                "belongedModuleName: " + belongedModuleName + ",\n" +
                "classPath: " + classPath + ",\n" +
                "currentClassOssUrl: " + currentClassOssUrl + ",\n" +
                "}";
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }
}
