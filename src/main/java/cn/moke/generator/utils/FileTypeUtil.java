package cn.moke.generator.utils;


import cn.moke.generator.enums.FileType;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author moke
 */
public class FileTypeUtil {

    private final static int BYTE_LENGTH = 28;

    /**
     * 获取文件类型（根据文件头判断）
     * @param is 输入流
     * @return 文件类型
     * @throws IOException
     */
    public static FileType getFileType(InputStream is) throws IOException {
        byte[] bytes = new byte[BYTE_LENGTH];
        is.read(bytes, 0, bytes.length);
        return getFileType(bytes);
    }

    /**
     * 获取文件类型（根据文件头判断）
     * @param bytes 字节数组
     * @return 文件类型
     */
    public static FileType getFileType(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < BYTE_LENGTH; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }

        FileType[] fileTypes = FileType.values();
        for (FileType fileType : fileTypes) {
            if (stringBuilder.toString().startsWith(fileType.getValue())) {
                return fileType;
            }
        }
        return null;
    }

    /**
     * 判断是否是图片（支持JPEG、PNG、GIF、BMP）
     * @param fileType 文件类型
     * @return 是否
     */
    public static boolean isPicture(FileType fileType) {
        if(fileType == null){
            return false;
        }
        switch (fileType){
            case JPEG:
            case PNG:
            case GIF:
            case BMP: return true;
            default: return false;
        }
    }

    /**
     * 判断是否是图片（支持JPEG、PNG、GIF、BMP）
     * @param bytes 字节数组
     * @return 是否
     */
    public static boolean isPicture(byte[] bytes) {
        FileType fileType = getFileType(bytes);
        if(fileType == null){
            return false;
        }
        switch (fileType){
            case JPEG:
            case PNG:
            case GIF:
            case BMP: return true;
            default: return false;
        }
    }

    /**
     * 判断是否是图片（支持JPEG、PNG、GIF、BMP）
     * @param is 输入流
     * @return 是否
     */
    public static boolean isPicture(InputStream is) throws IOException {
        FileType fileType = getFileType(is);
        if(fileType == null){
            return false;
        }
        switch (fileType){
            case JPEG:
            case PNG:
            case GIF:
            case BMP: return true;
            default: return false;
        }
    }
}
