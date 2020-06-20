package com.xxxtai.arthas.domain;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public class ClassInfo {

    private String name;

    private String qualifiedName;

    private String classPath;

    private String currentClassOssUrl;

    public ClassInfo(DataContext context) {
        Project project = context.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return;
        }

        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(context);
        if (psiElement instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) psiElement;
            this.name = psiClass.getName();
            this.qualifiedName = psiClass.getQualifiedName();
        }

        PsiFile psiFile = context.getData(CommonDataKeys.PSI_FILE);
        if (psiFile != null) {
            this.classPath = psiFile.getVirtualFile().getPath();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
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
}
