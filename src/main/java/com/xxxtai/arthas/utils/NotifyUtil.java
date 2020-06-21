package com.xxxtai.arthas.utils;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.ui.Messages;

public class NotifyUtil {

    public static void error(DataContext dataContext, String errorMsg) {
        Messages.showMessageDialog(dataContext.getData(CommonDataKeys.PROJECT), errorMsg,
                "Arthas Hot Swap", Messages.getInformationIcon());
    }
}
