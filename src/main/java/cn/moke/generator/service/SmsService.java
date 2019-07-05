package cn.moke.generator.service;


/**
 * @author moke
 */
public interface SmsService {

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @param smsCode 短信验证码
     * @return 是否成功
     */
    boolean sendSmsCode(String phone, String smsCode);

}
