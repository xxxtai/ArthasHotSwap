package com.xxxtai.arthas.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.xxxtai.arthas.constants.ClassIdentity;
import com.xxxtai.arthas.domain.ClassInfo;
import com.xxxtai.arthas.domain.EncryptInfo;
import com.xxxtai.arthas.utils.AesCryptoUtil;
import com.xxxtai.arthas.utils.IoUtil;
import com.xxxtai.arthas.utils.NotifyUtil;
import com.xxxtai.arthas.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

public class SwapThisClass extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        try {
            ClassInfo currentClassInfo = parseClassInfoFromDataContext(dataContext);
            byte[] currentClassBytes = findTheSwapClass(dataContext, currentClassInfo);
            if (currentClassBytes == null) {
                NotifyUtil.error(dataContext, currentClassInfo.toString() + "\n" + "the class of " + currentClassInfo.getClassPath() + " can not be found");
                return;
            }

            EncryptInfo encryptInfo = encryptTheSwapClass(currentClassBytes);
            String currentClassOssUrl = uploadEncryptContent2Oss(encryptInfo.getEncryptContent());
            currentClassInfo.setCurrentClassOssUrl(currentClassOssUrl);

            String hotSwapScript4OneClass = IoUtil.getResourceFile(getClass().getClassLoader(), "/scripts/template/HotSwapScript4OneClass");
            String script = renderTemplate(hotSwapScript4OneClass, currentClassInfo);
            Messages.showMessageDialog(dataContext.getData(CommonDataKeys.PROJECT), currentClassInfo.toString() +
                    "\n script:" + script,
                    "error", Messages.getInformationIcon());
        } catch (Exception t) {
            NotifyUtil.error(dataContext, StringUtils.isNotBlank(t.getMessage()) ? t.getMessage() : "Internal exception");
            t.printStackTrace();
        }
    }

    private ClassInfo parseClassInfoFromDataContext(DataContext context) {
        ClassInfo classInfo = new ClassInfo();
        Project project = context.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            throw new RuntimeException("is not a project");
        }
        classInfo.setProjectBasePath(project.getBasePath());

        PsiFile psiFile = context.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            throw new RuntimeException("please choose a file");
        }

        classInfo.setClassPath(psiFile.getVirtualFile().getPath());
        if (classInfo.getClassPath().endsWith(ClassIdentity.Suffix.CLASS)) {
            classInfo.setClassType(ClassIdentity.Type.CLASS);
        } else if (classInfo.getClassPath().endsWith(ClassIdentity.Suffix.SOURCE)){
            classInfo.setClassType(ClassIdentity.Type.SOURCE);
        } else {
            throw new RuntimeException("this file is neither java nor class");
        }
        if (ClassIdentity.Type.CLASS.equals(classInfo.getClassType())) {
            return classInfo;
        }

        Module module = ModuleUtil.findModuleForFile(psiFile.getVirtualFile(), project);
        classInfo.setBelongedModuleName(module == null ? "" : module.getName());

        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(context);
        if (!(psiElement instanceof PsiClass)) {
            throw new RuntimeException("Please put the mouse cursor on the class name");
        }

        PsiClass psiClass = (PsiClass) psiElement;
        classInfo.setSimpleName(psiClass.getName());
        classInfo.setQualifiedName(psiClass.getQualifiedName());
        return classInfo;
    }

    private byte[] findTheSwapClass(DataContext dataContext, ClassInfo classInfo) throws Exception {
        String filePath;
        if (ClassIdentity.Type.SOURCE.equals(classInfo.getClassType())) {
            String projectBasePath = classInfo.getProjectBasePath();
            filePath = projectBasePath + "/" + classInfo.getBelongedModuleName() + "/target/classes/" +
                    classInfo.getQualifiedName().replaceAll("\\.", "/") +
                    ".class";
        } else {
            filePath = classInfo.getClassPath();
        }
        NotifyUtil.error(dataContext, filePath);

        return IoUtil.getTargetClass(filePath);
    }

    private EncryptInfo encryptTheSwapClass(byte[] classBytes) {
        byte[] keyBytes = AesCryptoUtil.generalRandomBytes(16);
        byte[] ivBytes = AesCryptoUtil.generalRandomBytes(16);
        EncryptInfo encryptInfo = new EncryptInfo();
        encryptInfo.setOriginBytes(classBytes);
        encryptInfo.setKey(StringUtil.bytes2Hex(keyBytes));
        encryptInfo.setIv(StringUtil.bytes2Hex(ivBytes));
        encryptInfo.setEncryptContent(AesCryptoUtil.encrypt(classBytes, keyBytes, ivBytes));
        return encryptInfo;
    }

    private String uploadEncryptContent2Oss(String encryptContent) {
        return "url";
    }

    private static String renderTemplate(String content, ClassInfo currentClassInfo) {
        Map<String, Object> params = new HashMap<>();
        params.put("className", currentClassInfo.getSimpleName());
        StringSubstitutor s = new StringSubstitutor(params);
        return s.replace(content);
    }
}
