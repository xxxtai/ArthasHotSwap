package com.xxxtai.arthas.facade.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.StorageClass;
import com.xxxtai.arthas.constants.CommonConstants;
import com.xxxtai.arthas.domain.AppSettingsState;
import com.xxxtai.arthas.domain.Result;
import com.xxxtai.arthas.facade.OssFacade;

import java.io.ByteArrayInputStream;

public class OssFacadeImpl implements OssFacade {
    private static final String BUCKET_NAME = "arthas-hot-swap";

    @Override
    public Result<String> uploadString(String key, String content) {
        Result<OssInfo> parseResult = parseOssInfo();
        if (!parseResult.isSuccess()) {
            return Result.buildErrorResult(parseResult.getErrorMsg());
        }
        OssInfo ossInfo = parseResult.getValue();
        try {
            OSS ossClient = new OSSClientBuilder().build(ossInfo.endpoint, ossInfo.accessKeyId, ossInfo.accessKeySecret);
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, key, new ByteArrayInputStream(content.getBytes()));

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            metadata.setObjectAcl(CannedAccessControlList.PublicRead);
            putObjectRequest.setMetadata(metadata);

            ossClient.putObject(putObjectRequest);
            ossClient.shutdown();

            return Result.buildSuccessResult(ossInfo.objectAccessUrlPrefix + CommonConstants.PATH_SEPARATOR + key);
        } catch (ClientException e) {
            e.printStackTrace();
            return Result.buildErrorResult("Please check your network");
        }
    }

    private Result<OssInfo> parseOssInfo() {
        OssInfo ossInfo = new OssInfo();
        AppSettingsState settings = AppSettingsState.getInstance();
        if (!settings.endpoint.contains(CommonConstants.URL_SEPARATOR)) {
            return Result.buildErrorResult("Please check your setting of endpoint");
        }
        ossInfo.endpoint = settings.endpoint;
        ossInfo.accessKeyId = settings.accessKeyId;
        ossInfo.accessKeySecret = settings.accessKeySecret;
        String[] urlParts = settings.endpoint.split(CommonConstants.URL_SEPARATOR);
        ossInfo.objectAccessUrlPrefix = urlParts[0] + CommonConstants.URL_SEPARATOR + BUCKET_NAME + "." + urlParts[1];
        return Result.buildSuccessResult(ossInfo);
    }

    private class OssInfo {
        String endpoint;
        String accessKeyId;
        String accessKeySecret;
        String objectAccessUrlPrefix;

        @Override
        public String toString() {
            return "OssInfo:{" +
                    "endpoint:" + endpoint + "\n" +
                    "accessKeyId:" + accessKeyId + "\n" +
                    "accessKeySecret:" + accessKeySecret + "\n" +
                    "objectAccessUrlPrefix:" + objectAccessUrlPrefix + "\n" +
                    "}";
        }
    }
}
