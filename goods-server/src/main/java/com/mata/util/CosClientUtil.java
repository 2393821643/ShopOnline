package com.mata.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CosClientUtil {
    @Autowired
    private COSClient cosClient;

    @Value("${tx-client.bucketName}")
    private String bucketName;

    @Value("${tx-client.fileMkdir}")
    private String fileMkdir;

    @Value("${tx-client.regionName}")
    private String regionName;


    /**
     * 上传文件到cos中
     * @param file 要上传的文件
     * @return 文件的url
     */
    public String sendFile(File file){
        String key= fileMkdir+"/"+file.getName();
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        return "https://"+bucketName+".cos."+regionName+".myqcloud.com/"+fileMkdir+"/"+file.getName();
    }
}
