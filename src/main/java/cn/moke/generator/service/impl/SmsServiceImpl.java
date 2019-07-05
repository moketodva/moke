package cn.moke.generator.service.impl;

import cn.moke.generator.service.SmsService;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author moke
 */
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    /**
     * 短信API产品名称（短信产品名固定，无需修改）
     */
    private String product = "Dysmsapi";
    /**
     * 短信API产品域名（接口地址固定，无需修改）
     */
    private String domain = "dysmsapi.aliyuncs.com";
    /**
     *
     */
    private String regionId = "cn-hangzhou";
    /**
     *
     */
    private String endpointName = "cn-hangzhou";
    /**
     * 成功状态码
     */
    private String successCode = "OK";
    /**
     * accessKeyId
     */
    @Value("${moke.sms.access-key-id}")
    private String accessKeyId;
    /**
     * accessKeySecret
     */
    @Value("${moke.sms.access-key-secret}")
    private String accessKeySecret;
    /**
     * 短信签名
     */
    @Value("${moke.sms.sign-name}")
    private String signName;
    /**
     * 短信模板
     */
    @Value("${moke.sms.template-code}")
    private String templateCode;
    /**
     * 连接超时
     */
    @Value("${moke.sms.connection-timeout}")
    private String connectTimeout;
    /**
     * 读取超时
     */
    @Value("${moke.sms.read-timeout}")
    private String readTimeout;


    private IAcsClient getIAcsClient() {
        try {
            //设置超时时间-可自行调整
            System.setProperty("sun.net.client.defaultConnectTimeout", connectTimeout);
            System.setProperty("sun.net.client.defaultReadTimeout", readTimeout);
            //初始化ascClient,暂时不支持多region（请勿修改）
            IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint(endpointName, regionId, product, domain);
            return new DefaultAcsClient(profile);
        } catch (ClientException e) {
            log.error("{}", e);
            return null;
        }
    }

    @Override
    public boolean sendSmsCode(String phone, String smsCode) {
        IAcsClient acsClient = getIAcsClient();
        if(acsClient == null){
            return false;
        }
        // 组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        // 使用post提交
        request.setMethod(MethodType.POST);
        // 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为00+国际区号+号码，如“0085200000000”
        request.setPhoneNumbers(phone);
        // 必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        // 必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);
        // 可选:模板中的变量替换JSON串,如模板内容为"亲爱的\${name},您的验证码为\${code}"时,此处的值为
        // 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam("{\"code\":\""+smsCode+"\"}");

        SendSmsResponse sendSmsResponse;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            log.error("{}", e);
            return false;
        }
        return sendSmsResponse.getCode() != null && successCode.equals(sendSmsResponse.getCode());
    }
}
