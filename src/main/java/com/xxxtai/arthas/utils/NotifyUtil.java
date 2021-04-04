package com.xxxtai.arthas.utils;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

/**
 * Copyright (c) 2020, 2021, xxxtai. All rights reserved.
 *
 * @author xxxtai
 */
public class NotifyUtil {

    public static void notifyMessage(Project project, String message) {
        NotificationGroupManager.getInstance().getNotificationGroup("ArthasHotSwap")
            .createNotification(message, NotificationType.INFORMATION)
            .notify(project);
    }

    public static void error(Project project, String errorMsg) {
        Messages.showMessageDialog(project, errorMsg,
            "ArthasHotSwap", Messages.getErrorIcon());
    }
}
