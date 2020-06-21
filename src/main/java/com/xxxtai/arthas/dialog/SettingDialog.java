package com.xxxtai.arthas.dialog;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.xxxtai.arthas.constants.CommonConstants;
import com.xxxtai.arthas.domain.AppSettingsState;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SettingDialog implements Configurable {
    private AppSettingsComponent mySettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "ArthasHotSwap";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        return !mySettingsComponent.getOssEndpointText().equals(settings.endpoint)
                || !mySettingsComponent.getOssAccessKeyIdText().equals(settings.accessKeyId)
                || !mySettingsComponent.getOssAccessKeySecretText().equals(settings.accessKeySecret);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (StringUtils.isBlank(mySettingsComponent.getOssEndpointText())) {
            throw new ConfigurationException("endpoint can not be blank");
        }
        if (!mySettingsComponent.getOssEndpointText().contains(CommonConstants.URL_SEPARATOR)) {
            throw new ConfigurationException("endpoint should start with http:// or https://");
        }
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.endpoint = mySettingsComponent.getOssEndpointText();
        settings.accessKeyId = mySettingsComponent.getOssAccessKeyIdText();
        settings.accessKeySecret = mySettingsComponent.getOssAccessKeySecretText();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setOssEndpointText(settings.endpoint);
        mySettingsComponent.setOssAccessKeyIdText(settings.accessKeyId);
        mySettingsComponent.setOssAccessKeySecretText(settings.accessKeySecret);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}
