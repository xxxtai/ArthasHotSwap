package com.xxxtai.arthas.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.Messages;
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
            ClassInfo currentClassInfo = new ClassInfo(dataContext);
            byte[] currentClassBytes = findTheSwapClass(currentClassInfo);
            if (currentClassBytes == null) {
                NotifyUtil.error(dataContext, "the class of " + currentClassInfo.getName() + " can not be found");
                return;
            }

            EncryptInfo encryptInfo = encryptTheSwapClass(currentClassBytes);
            String currentClassOssUrl = uploadEncryptContent2Oss(encryptInfo.getEncryptContent());
            currentClassInfo.setCurrentClassOssUrl(currentClassOssUrl);

            String hotSwapScript4OneClass = IoUtil.getFile(getClass().getClassLoader(), "/scripts/template/HotSwapScript4OneClass");
            String script = renderTemplate(hotSwapScript4OneClass, currentClassInfo);
            Messages.showMessageDialog(dataContext.getData(CommonDataKeys.PROJECT),  JSON.toJSONString() + "\n" + script,
                    "测试", Messages.getInformationIcon());
        } catch (Exception t) {
            NotifyUtil.error(dataContext, StringUtils.isNotBlank(t.getMessage()) ? t.getMessage() : "Internal exception");
            t.printStackTrace();
        }
    }

    private byte[] findTheSwapClass(ClassInfo classInfo) {
        return null;
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
        params.put("className", currentClassInfo.getName());
        StringSubstitutor s = new StringSubstitutor(params);
        return s.replace(content);
    }
}
