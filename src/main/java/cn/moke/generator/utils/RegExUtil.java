package cn.moke.generator.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author moke
 */
public class RegExUtil {

    /**
     * 返回正则匹配字符串
     * @param regExStr 正则匹配规则
     * @param targetStr 目标字符串
     * @return
     */
    public static String find(String regExStr, String targetStr){
        Pattern pattern = Pattern.compile(regExStr);
        Matcher matcher = pattern.matcher(targetStr);
        return matcher.group();
    }
}
