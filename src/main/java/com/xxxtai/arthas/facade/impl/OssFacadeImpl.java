package com.xxxtai.arthas.facade.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.StorageClass;
import com.intellij.openapi.project.Project;
import com.xxxtai.arthas.constants.CommonConstants;
import com.xxxtai.arthas.dialog.MyToolWindow;
import com.xxxtai.arthas.domain.AppSettingsState;
import com.xxxtai.arthas.domain.Result;
import com.xxxtai.arthas.facade.OssFacade;
import com.xxxtai.arthas.utils.IoUtil;

import java.io.ByteArrayInputStream;

public class OssFacadeImpl implements OssFacade {
    private static String DIRECTORY = "public/";

    @Override
    public Result<String> uploadString(Project project, String key, String content) {
        Result<OssInfo> parseResult = parseOssInfo(project);
        if (!parseResult.isSuccess()) {
            return Result.buildErrorResult(parseResult.getErrorMsg());
        }
        OssInfo ossInfo = parseResult.getValue();
        try {
            OSS ossClient = new OSSClientBuilder().build(ossInfo.endpoint, ossInfo.accessKeyId, ossInfo.accessKeySecret);
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossInfo.bucketName, DIRECTORY + key, new ByteArrayInputStream(content.getBytes()));

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            metadata.setObjectAcl(CannedAccessControlList.PublicRead);
            putObjectRequest.setMetadata(metadata);

            ossClient.putObject(putObjectRequest);
            ossClient.shutdown();

            return Result.buildSuccessResult(ossInfo.objectAccessUrlPrefix + CommonConstants.PATH_SEPARATOR + DIRECTORY + key);
        } catch (ClientException e) {
            MyToolWindow.consoleLog(IoUtil.printStackTrace(e));
            return Result.buildErrorResult("Please check your network");
        }
    }

    private Result<OssInfo> parseOssInfo(Project project) {
        OssInfo ossInfo = new OssInfo();
        AppSettingsState settings = AppSettingsState.getInstance(project);
        if (CommonConstants.DEFAULT.equals(settings.endpoint)
            || CommonConstants.DEFAULT.equals(settings.accessKeyId)
            || CommonConstants.DEFAULT.equals(settings.accessKeySecret)
            || CommonConstants.DEFAULT.equals(settings.bucketName)) {
            ossInfo.endpoint = CommonConstants.DEFAULT_ENDPOINT;
            ossInfo.accessKeyId = CommonConstants.DEFAULT_ACCESS_KEY_ID;
            ossInfo.accessKeySecret = CommonConstants.DEFAULT_ACCESS_KEY_SECRET;
            ossInfo.bucketName = CommonConstants.DEFAULT_BUCKET_NAME;
        } else {
            if (!settings.endpoint.contains(CommonConstants.URL_SEPARATOR)) {
                return Result.buildErrorResult("Please check your setting of endpoint");
            }
            ossInfo.endpoint = settings.endpoint;
            ossInfo.accessKeyId = settings.accessKeyId;
            ossInfo.accessKeySecret = settings.accessKeySecret;
            ossInfo.bucketName = settings.bucketName;
        }

        String[] urlParts = ossInfo.endpoint.split(CommonConstants.URL_SEPARATOR);
        ossInfo.objectAccessUrlPrefix = urlParts[0] + CommonConstants.URL_SEPARATOR + ossInfo.bucketName + "." + urlParts[1];
        return Result.buildSuccessResult(ossInfo);
    }

    private class OssInfo {
        String endpoint;
        String accessKeyId;
        String accessKeySecret;
        String objectAccessUrlPrefix;
        String bucketName;

        @Override
        public String toString() {
            return "OssInfo:{" +
                "endpoint:" + endpoint + "\n" +
                "accessKeyId:" + accessKeyId + "\n" +
                "accessKeySecret:" + accessKeySecret + "\n" +
                "objectAccessUrlPrefix:" + objectAccessUrlPrefix + "\n" +
                "bucketName:" + bucketName + "\n" +
                "}";
        }
    }
}
