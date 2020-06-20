package com.xxxtai.arthas.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;

public class SwapThisClass extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        DataContext dataContext = e.getDataContext();
        Editor editor = dataContext.getData(CommonDataKeys.EDITOR);
        Project project = dataContext.getData(CommonDataKeys.PROJECT);
        if (editor == null || project == null) {
            return;
        }
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        String className = "";
        String methodName = "";
        if (psiElement instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) psiElement;
            className = psiMethod.getContainingClass().getQualifiedName();
            methodName = psiMethod.getNameIdentifier().getText();
        }
        if (psiElement instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) psiElement;
            className = psiClass.getQualifiedName();
            methodName = "*";
        }

        if (psiElement instanceof PsiField) {
            PsiField psiField = (PsiField) psiElement;
            className = psiField.getContainingClass().getQualifiedName();
            methodName = "*";
        }


        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        String classPath = psiFile.getVirtualFile().getPath();
        String title = "Hello World!";

        StringBuilder builder = new StringBuilder();
        builder.append("className:").append(className).append("\n")
                .append("methodName:").append(methodName).append("\n")
                .append("classPath:").append(classPath).append("\n");

        Messages.showMessageDialog(project, builder.toString(), title, Messages.getInformationIcon());
    }
}
