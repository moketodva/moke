package cn.moke.generator.service.impl;

import cn.moke.generator.service.UploadService;
import cn.moke.generator.utils.Base64FileUtil;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Region;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

/**
 * @author moke
 */
@Service
public class UploadServiceImpl implements UploadService {

    @Value("${moke.upload.access-key}")
    private String accessKey;

    @Value("${moke.upload.secret-key}")
    private String secretKey;

    @Value("${moke.upload.bucket}")
    private String bucket;

    @Value("${moke.upload.domain}")
    private String domain;

    @Override
    public String upload(String data64, String fileName) throws IOException {
        // BASE64解码
        Base64 base64 = new Base64();
        byte[] byteData = base64.decode(Base64FileUtil.getData(data64));
        // 上传
        return this.upload(byteData, fileName);
    }

    @Override
    public String upload(byte[] byteData, String fileName) throws IOException {
        // 覆盖上传令牌
        String token = generateToken(fileName);
        // 上传
        Configuration configuration = new Configuration(Region.region2());
        UploadManager uploadManager = new UploadManager(configuration);
        Response response = uploadManager.put(byteData, fileName, token);
        DefaultPutRet defaultPutRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        // 组拼url
        return accessUrl(defaultPutRet.key);
    }

    @Override
    public String upload(InputStream inputStream, String fileName) throws QiniuException {
        // 覆盖上传令牌
        String token = generateToken(fileName);
        // 上传
        Configuration configuration = new Configuration(Region.region2());
        UploadManager uploadManager = new UploadManager(configuration);
        Response response = uploadManager.put(inputStream, fileName, token, null, null);
        DefaultPutRet defaultPutRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        // 组拼url
        return accessUrl(defaultPutRet.key);
    }

    @Override
    public String fileRename(String fileName) {
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        return UUID.randomUUID().toString().replace("-", "") + suffixName;
    }

    /**
     * 生成token
     * @param key
     * @return
     */
    private String generateToken(String key) {
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket, key);
        return upToken;
    }

    /**
     * 组拼 文件访问url
     * @param key
     * @return
     */
    private String accessUrl(String key){
        return domain + "/" + key + "?v=" + Instant.now().toEpochMilli();
    }
}
