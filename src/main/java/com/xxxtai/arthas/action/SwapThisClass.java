package com.xxxtai.arthas.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.xxxtai.arthas.constants.ClassIdentity;
import com.xxxtai.arthas.domain.ClassInfo;
import com.xxxtai.arthas.domain.EncryptInfo;
import com.xxxtai.arthas.domain.Result;
import com.xxxtai.arthas.facade.OssFacade;
import com.xxxtai.arthas.facade.impl.OssFacadeImpl;
import com.xxxtai.arthas.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

import static com.xxxtai.arthas.constants.CommonConstants.PATH_SEPARATOR;

public class SwapThisClass extends AnAction {

    private OssFacade ossFacade = new OssFacadeImpl();

    @Override
    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        Project project = dataContext.getData(CommonDataKeys.PROJECT);
        try {
            ClassInfo currentClassInfo = parseClassInfoFromDataContext(dataContext);
            byte[] currentClassBytes = findTheSwapClass(currentClassInfo);
            if (currentClassBytes == null) {
                NotifyUtil.error(project, currentClassInfo.toString() + "\n" + "the class of " + currentClassInfo.getClassPath() + " can not be found");
                return;
            }

            EncryptInfo encryptInfo = encryptTheSwapClass(currentClassBytes);

            Result<String> uploadCurrentClassResult = ossFacade.uploadString(currentClassInfo.getSimpleName(), encryptInfo.getEncryptContent());
            if (!uploadCurrentClassResult.isSuccess()) {
                NotifyUtil.error(project, uploadCurrentClassResult.getErrorMsg());
                return;
            }
            currentClassInfo.setCurrentClassOssUrl(uploadCurrentClassResult.getValue());

            String hotSwapScript = renderHotSwapScriptWithTemplate(getClass().getClassLoader(), currentClassInfo);
            Result<String> uploadHotSwapScriptResult = ossFacade.uploadString("hotSwapScript", hotSwapScript);
            if (!uploadHotSwapScriptResult.isSuccess()) {
                NotifyUtil.error(project, uploadHotSwapScriptResult.getErrorMsg());
                return;
            }

            String command = String.format("curl -L %s | sh -s %s %s", uploadHotSwapScriptResult.getValue(), encryptInfo.getKey(), encryptInfo.getIv());
            ClipboardUtils.setClipboardString(command);
            NotifyUtil.error(project,
                    "command:" + command + "\n" +
                            currentClassInfo.toString());
            NotifyUtil.notifyMessage(project, "the command of hot swap has been copied to the clipboard, go to the host to execute the command");
        } catch (Exception t) {
            NotifyUtil.error(project, StringUtils.isNotBlank(t.getMessage()) ? t.getMessage() : "Internal exception");
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
        } else if (classInfo.getClassPath().endsWith(ClassIdentity.Suffix.SOURCE)) {
            classInfo.setClassType(ClassIdentity.Type.SOURCE);
        } else {
            throw new RuntimeException("this file is neither java nor class");
        }
        if (ClassIdentity.Type.CLASS.equals(classInfo.getClassType())) {
            String[] pathNodes = classInfo.getClassPath().split(PATH_SEPARATOR);
            String fileName = pathNodes[pathNodes.length - 1];
            classInfo.setSimpleName(fileName.substring(0, fileName.length() - ClassIdentity.Suffix.CLASS.length()));
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

    private byte[] findTheSwapClass(ClassInfo classInfo) throws Exception {
        String filePath;
        if (ClassIdentity.Type.SOURCE.equals(classInfo.getClassType())) {
            String projectBasePath = classInfo.getProjectBasePath();
            filePath = projectBasePath + PATH_SEPARATOR + classInfo.getBelongedModuleName() + "/target/classes/" +
                    classInfo.getQualifiedName().replaceAll("\\.", PATH_SEPARATOR) +
                    ClassIdentity.Suffix.CLASS;
        } else {
            filePath = classInfo.getClassPath();
        }
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

    private static String renderHotSwapScriptWithTemplate(ClassLoader classLoader, ClassInfo currentClassInfo) throws Exception {
        String hotSwapScript4OneClass = IoUtil.getResourceFile(classLoader, "/scripts/template/HotSwapScript4OneClass.sh");
        Map<String, Object> params = new HashMap<>();
        params.put("className", currentClassInfo.getSimpleName());
        params.put("currentClassOssUrl", currentClassInfo.getCurrentClassOssUrl());
        StringSubstitutor s = new StringSubstitutor(params);
        return s.replace(hotSwapScript4OneClass);
    }
}
