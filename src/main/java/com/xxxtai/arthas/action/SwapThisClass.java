package com.xxxtai.arthas.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.xxxtai.arthas.constants.ClassIdentity;
import com.xxxtai.arthas.dialog.MyToolWindow;
import com.xxxtai.arthas.domain.AppSettingsState;
import com.xxxtai.arthas.domain.ClassInfo;
import com.xxxtai.arthas.domain.EncryptInfo;
import com.xxxtai.arthas.domain.Result;
import com.xxxtai.arthas.facade.OssFacade;
import com.xxxtai.arthas.facade.impl.OssFacadeImpl;
import com.xxxtai.arthas.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.xxxtai.arthas.constants.CommonConstants.CLASS_SEPARATOR;
import static com.xxxtai.arthas.constants.CommonConstants.JAVA_PATH_TOKEN;
import static com.xxxtai.arthas.constants.CommonConstants.PATH_SEPARATOR;
import static com.xxxtai.arthas.constants.CommonConstants.SRC_PATH_TOKEN;
import static com.xxxtai.arthas.constants.CommonConstants.TARGET_CLASS_PATH_TOKEN;

public class SwapThisClass extends AnAction {
    private int times = 0;
    private OssFacade ossFacade = new OssFacadeImpl();

    @Override
    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        Project project = dataContext.getData(CommonDataKeys.PROJECT);
        forceNotify(project);
        try {
            ClassInfo currentClassInfo = parseClassInfoFromDataContext(dataContext);
            byte[] currentClassBytes = findTheSwapClass(currentClassInfo);
            if (currentClassBytes == null) {
                NotifyUtil.error(project,
                    "Please compile the file first ! "
                        + "\nCan't find the class of the source file : " + currentClassInfo.getClassPath());
                return;
            }

            EncryptInfo encryptInfo = encryptTheSwapClass(currentClassBytes);

            Result<String> uploadCurrentClassResult = ossFacade.uploadString(
                project,
                generateEncryptKey(project, currentClassInfo.getSimpleName()),
                encryptInfo.getEncryptContent());
            if (!uploadCurrentClassResult.isSuccess()) {
                NotifyUtil.error(project, uploadCurrentClassResult.getErrorMsg());
                return;
            }
            currentClassInfo.setCurrentClassOssUrl(uploadCurrentClassResult.getValue());

            String hotSwapScript = renderHotSwapScriptWithTemplate(project, getClass().getClassLoader(), currentClassInfo);
            Result<String> uploadHotSwapScriptResult = ossFacade.uploadString(
                project,
                generateEncryptKey(project,"hotSwapScript"),
                hotSwapScript);
            if (!uploadHotSwapScriptResult.isSuccess()) {
                NotifyUtil.error(project, uploadHotSwapScriptResult.getErrorMsg());
                return;
            }

            String command = String.format("sudo curl -L %s  > HotSwapScript4OneClass.sh ; "
                    + "chmod +x HotSwapScript4OneClass.sh; "
                    + "yes | ./HotSwapScript4OneClass.sh  %s %s", uploadHotSwapScriptResult.getValue(), encryptInfo.getKey(), encryptInfo.getIv());
            ClipboardUtils.setClipboardString(command);

            notifyResult(project, currentClassInfo, command);
        } catch (Throwable t) {
            MyToolWindow.consoleLog(IoUtil.printStackTrace(t));
            try {
                String tip = getTipFromUrl("https://xxxtai-arthas-hot-swap.oss-cn-beijing.aliyuncs.com/tips/errorTip");
                if (StringUtils.isNotBlank(tip)) {
                    NotifyUtil.error(project, tip);
                } else {
                    NotifyUtil.error(project, "Internal exception : " + t.getMessage());
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void forceNotify(Project project) {
        try {
            if (times > 0) {
                return;
            }
            times++;
            String tip = getTipFromUrl("https://xxxtai-arthas-hot-swap.oss-cn-beijing.aliyuncs.com/tips/forceTip");
            if (StringUtils.isNotBlank(tip)) {
                NotifyUtil.error(project, tip);
            }
        } catch (Throwable t) {
        }
    }

    private void notifyResult(Project project, ClassInfo currentClassInfo, String command) throws Exception {

        MyToolWindow.consoleLog( currentClassInfo.toString());
        MyToolWindow.consoleLog("************************************************ The following string is the command of hot swap ************************************************" +
                "\n" +
                "\n" + command +
                "\n" +
                "\n********************************* Copy this command, and then go to the host to execute the command ***********************************************\n\n");

        String tip = getTipFromUrl("https://xxxtai-arthas-hot-swap.oss-cn-beijing.aliyuncs.com/tips/successTip");
        if (StringUtils.isNotBlank(tip)) {
            NotifyUtil.notifyMessage(project, tip);
            return;
        }

        NotifyUtil.notifyMessage(project,
            "Arthas Hot Swap Tip : the command of hot swap has been copied to the clipboard, go to the host to execute the command");
    }

    private String generateEncryptKey(Project project, String keyWord) {
        byte[] keyBytes = AesCryptoUtil.generalRandomBytes(16);
        byte[] ivBytes = AesCryptoUtil.generalRandomBytes(16);
        String key = (project == null ? "" : project.getName()) + keyWord;
        String encryptStr = AesCryptoUtil.encrypt(key.getBytes(), keyBytes, ivBytes);
        return encryptStr.replaceAll("[\n/+]", "x");
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
        String substring = classInfo.getClassPath().substring(0, classInfo.getClassPath().length() - 5);
        String[] arr = substring.split(PATH_SEPARATOR);
        classInfo.setSimpleName(arr[arr.length - 1]);
        String[] qualifiedNameArray = substring.split(JAVA_PATH_TOKEN);
        classInfo.setQualifiedName(qualifiedNameArray[qualifiedNameArray.length - 1].replaceAll(PATH_SEPARATOR, CLASS_SEPARATOR));
        return classInfo;
    }

    private byte[] findTheSwapClass(ClassInfo classInfo) throws Exception {
        String filePath;
        if (ClassIdentity.Type.SOURCE.equals(classInfo.getClassType())) {
            String[] paths = classInfo.getClassPath().split(SRC_PATH_TOKEN);
            filePath = paths[0] +
                TARGET_CLASS_PATH_TOKEN +
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

    private static String renderHotSwapScriptWithTemplate(Project project, ClassLoader classLoader, ClassInfo currentClassInfo) throws Exception {
        AppSettingsState settings = AppSettingsState.getInstance(project);

        String filePath = StringUtils.isBlank(settings.selectJavaProcessName) ?
            "/scripts/template/HotSwapScript4OneClass.sh" : "/scripts/template/HotSwapScript4OneClassWithJavaProcess.sh";

        String hotSwapScript4OneClass = IoUtil.getResourceFile(classLoader, filePath);
        Map<String, Object> params = new HashMap<>(3);
        params.put("className", currentClassInfo.getSimpleName());
        params.put("currentClassOssUrl", currentClassInfo.getCurrentClassOssUrl());
        if (StringUtils.isNotBlank(settings.selectJavaProcessName)) {
            params.put("selectJavaProcessName", settings.selectJavaProcessName);
        }

        StringSubstitutor s = new StringSubstitutor(params);
        return s.replace(hotSwapScript4OneClass);
    }

    private static String getTipFromUrl(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        String s;
        while ((s = reader.readLine()) != null) {
            builder.append(s);
        }
        reader.close();
        return builder.toString();
    }
}
