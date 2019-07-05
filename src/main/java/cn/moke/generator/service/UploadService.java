package cn.moke.generator.service;

import com.qiniu.common.QiniuException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author moke
 */
public interface UploadService {

    /**
     * 上传
     * @param data64 base64数据
     * @param fileName 文件名
     * @return
     * @throws QiniuException
     */
    String upload(String data64, String fileName) throws IOException;

    /**
     * 上传
     * @param byteData 字节数组
     * @param fileName 文件名
     * @return
     * @throws IOException
     */
    String upload(byte[] byteData, String fileName) throws IOException;

    /**
     * 上传
     * @param inputStream 字节流
     * @param fileName 文件名
     * @return
     */
    String upload(InputStream inputStream, String fileName) throws QiniuException;

    /**
     * 使用UUID命名
     * @param fileName 文件名
     * @return UUID文件名
     */
    String fileRename(String fileName);
}
