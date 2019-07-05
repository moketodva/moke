package cn.moke.generator.utils;

/**
 * @author moke
 */
public class StringUtil {
    public static boolean isNull(String str){
        return str == null;
    }

    public static boolean isEmpty(String str){
        return str == null || "".equals(str);
    }

    /**
     * 获取文件后缀名
     * @param str 文件名
     * @return
     */
    public static String getFileSuffix(String str){
        if(str == null){
            return null;
        }
        String[] result = str.split(".");
        if(result.length <= 0){
            return null;
        }
        return result[result.length-1];
    }
}
