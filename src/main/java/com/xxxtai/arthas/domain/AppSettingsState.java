package com.xxxtai.arthas.domain;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Copyright (c) 2020, 2021, xxxtai. All rights reserved.
 *
 * Supports storing the application settings in a persistent way.
 * The State and Storage annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 *
 * @author xxxtai
 */
@State(
    name = "com.xxxtai.arthas.domain.AppSettingsState",
    storages = {@Storage("setting.xml")}
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    public String endpoint = "";
    public String accessKeyId = "";
    public String accessKeySecret = "";
    public String bucketName = "";
    public String selectJavaProcessName = "";
    public String specifyJavaHome = "";

    public static AppSettingsState getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, AppSettingsState.class);
    }

    @Nullable
    @Override
    public AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
