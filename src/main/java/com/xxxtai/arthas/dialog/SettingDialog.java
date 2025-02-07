package com.xxxtai.arthas.dialog;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.xxxtai.arthas.constants.CommonConstants;
import com.xxxtai.arthas.domain.AppSettingsState;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Copyright (c) 2020, 2021, xxxtai. All rights reserved.
 *
 * @author xxxtai
 */
public class SettingDialog implements Configurable {
    private AppSettingsComponent mySettingsComponent;
    private Project project;
    /**
     * 设置信息
     */
    private AppSettingsState settings;

    public SettingDialog(Project project) {
        this.project = project;
        settings = AppSettingsState.getInstance(this.project);
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "ArthasHotSwap";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        return !mySettingsComponent.getOssEndpointText().equals(settings.endpoint)
            || !mySettingsComponent.getOssAccessKeyIdText().equals(settings.accessKeyId)
            || !mySettingsComponent.getOssAccessKeySecretText().equals(settings.accessKeySecret)
            || !mySettingsComponent.getBucketNameText().equals(settings.bucketName)
            || !mySettingsComponent.getSelectJavaProcessText().equals(settings.selectJavaProcessName)
            || !mySettingsComponent.getSpecifyJavaHomeText().equals(settings.specifyJavaHome);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (!mySettingsComponent.getOssEndpointText().isBlank() && !mySettingsComponent.getOssEndpointText().contains(CommonConstants.URL_SEPARATOR)) {
            throw new ConfigurationException("endpoint should start with http:// or https://");
        }
        settings.endpoint = mySettingsComponent.getOssEndpointText();
        settings.accessKeyId = mySettingsComponent.getOssAccessKeyIdText();
        settings.accessKeySecret = mySettingsComponent.getOssAccessKeySecretText();
        settings.bucketName = mySettingsComponent.getBucketNameText();
        settings.selectJavaProcessName = mySettingsComponent.getSelectJavaProcessText();
        settings.specifyJavaHome = mySettingsComponent.getSpecifyJavaHomeText();
    }

    @Override
    public void reset() {
        mySettingsComponent.setOssEndpointText(settings.endpoint);
        mySettingsComponent.setOssAccessKeyIdText(settings.accessKeyId);
        mySettingsComponent.setOssAccessKeySecretText(settings.accessKeySecret);
        mySettingsComponent.setBucketNameText(settings.bucketName);
        mySettingsComponent.setSelectJavaProcessText(settings.selectJavaProcessName);
        mySettingsComponent.setSpecifyJavaHomeText(settings.specifyJavaHome);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}
