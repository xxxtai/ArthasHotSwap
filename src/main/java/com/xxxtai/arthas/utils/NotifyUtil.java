package com.xxxtai.arthas.utils;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class NotifyUtil {
    private static final NotificationGroup NOTIFICATION = new NotificationGroup("arthas", NotificationDisplayType.BALLOON, false);

    public static void notifyMessage(Project project, String message) {
        try {
            Notification currentNotify = NOTIFICATION.createNotification(message, NotificationType.INFORMATION);
            Notifications.Bus.notify(currentNotify, project);
        } catch (Exception e) {
            //
        }
    }

    public static void notifyMessage(Project project, String message, NotificationType type) {
        try {
            Notification currentNotify = NOTIFICATION.createNotification(message, type);
            Notifications.Bus.notify(currentNotify, project);
        } catch (Exception e) {
            //
        }
    }

    public static void error(Project project, String errorMsg) {
        Messages.showMessageDialog(project, errorMsg,
                "Arthas Hot Swap", Messages.getInformationIcon());
    }
}
