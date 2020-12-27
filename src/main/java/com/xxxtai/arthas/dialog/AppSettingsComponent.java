package com.xxxtai.arthas.dialog;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Supports creating and managing a JPanel for the Settings Dialog.
 */
public class AppSettingsComponent {
    private final JPanel myMainPanel;
    private final JBTextField ossEndpointText = new JBTextField();
    private final JBTextField ossAccessKeyIdText = new JBTextField();
    private final JBTextField ossAccessKeySecretText = new JBTextField();
    private final JBTextField bucketNameText = new JBTextField();
    private final JBTextField selectJavaProcessText = new JBTextField();


    public AppSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Enter OSS Endpoint: "), ossEndpointText, 1, false)
                .addLabeledComponent(new JBLabel("Enter OSS AccessKeyId: "), ossAccessKeyIdText, 1, false)
                .addLabeledComponent(new JBLabel("Enter OSS AccessKeySecret: "), ossAccessKeySecretText, 1, false)
                .addLabeledComponent(new JBLabel("Enter OSS BucketName: "), bucketNameText, 1, false)
                .addLabeledComponent(new JBLabel("Enter JAVA PROCESS: "), selectJavaProcessText, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return ossEndpointText;
    }

    @NotNull
    public String getOssEndpointText() {
        return ossEndpointText.getText();
    }

    public void setOssEndpointText(@NotNull String newText) {
        ossEndpointText.setText(newText);
    }

    @NotNull
    public String getOssAccessKeyIdText() {
        return ossAccessKeyIdText.getText();
    }

    public void setOssAccessKeyIdText(String newText) {
        ossAccessKeyIdText.setText(newText);
    }

    public void setOssAccessKeySecretText(String newText) {
        ossAccessKeySecretText.setText(newText);
    }

    @NotNull
    public String getOssAccessKeySecretText() {
        return ossAccessKeySecretText.getText();
    }

    @NotNull
    public String getBucketNameText() {
        return bucketNameText.getText();
    }

    public void setBucketNameText(String newText) {
        bucketNameText.setText(newText);
    }

    public String getSelectJavaProcessText() {
        return selectJavaProcessText.getText();
    }

    public void setSelectJavaProcessText(String newText) {
        selectJavaProcessText.setText(newText);
    }

}
