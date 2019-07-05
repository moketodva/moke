package cn.moke.generator.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author moke
 */
public class IpUtil {

    private final static String LOCALHOST_IPV6 =  "0:0:0:0:0:0:0:1";
    private final static String LOCALHOST_IPV4 =  "127.0.0.1";
    private final static String IP_UNKNOWN =  "unknown";
    private final static String MULTIPLE_IP_SPLIT_SIGN =  ",";
    private final static int IPV4_STRING_LENGTH =  15;

    public static String getIp(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if(ip.equals(LOCALHOST_IPV4) || ip.equals(LOCALHOST_IPV6)){
                // 从网卡获取IP
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip = inetAddress.getHostAddress();
            }
        }
        // 经过多次代理会有多个ip, 以","分割;第一个ip为最开始发起请求的ip
        if(ip != null && ip.length() > IPV4_STRING_LENGTH){
            if(ip.indexOf(MULTIPLE_IP_SPLIT_SIGN) > 0){
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }
}
