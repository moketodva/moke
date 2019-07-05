package cn.moke.generator.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.moke.generator.constant.CommonConstant;
import cn.moke.generator.entity.vo.Wrapper;
import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.exception.BusinessException;
import cn.moke.generator.service.SmsService;
import cn.moke.generator.utils.StringUtil;
import cn.moke.generator.utils.WrapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ResponseHeader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * @author moke
 */
@Api(tags = "验证码接口")
@RestController
@RequestMapping("/captcha")
@Validated
public class CaptchaController {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private SmsService smsService;

    @ApiOperation("获取验证码id")
    @RequestMapping(value = "/id", method = RequestMethod.GET)
    public Wrapper getCaptchaId(){
        // 生成验证码id和验证码code并作为key、value存入redis（有效2分钟）
        String captchaId = IdUtil.simpleUUID();
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100, 4, 15);
        String code = lineCaptcha.getCode();
        redisTemplate.opsForValue().set(CommonConstant.CAPTCHA_ID_REDIS_KEY_PREFIX + captchaId, code, 2L, TimeUnit.MINUTES);
        return WrapperUtil.ok(captchaId);
    }

    @ApiOperation("获取验证码图片")
    @RequestMapping(value = "/pic/{captchaId}", method = RequestMethod.GET)
    public void getCaptchaPic(@ApiParam("验证码id") @PathVariable String captchaId,
                              HttpServletResponse response) throws IOException {
        // 判断验证码id是否失效
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100, 4, 15);
        String code = redisTemplate.opsForValue().get(CommonConstant.CAPTCHA_ID_REDIS_KEY_PREFIX + captchaId);
        if(StringUtil.isEmpty(code)){
            throw new BusinessException(WrapperCode.CAPTCHA_CAPTCHA_EXPIRE);
        }
        // 生成验证码图片
        Image image = lineCaptcha.createImage(code);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImgUtil.writePng(image, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        // 响应
        OutputStream out = null;
        try {
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            out = response.getOutputStream();
            out.write(imageBytes);
        } finally {
            if(out != null){
                out.close();
            }
        }
    }

    @ApiOperation("发送短信验证码")
    @RequestMapping(value = "/sms", method = RequestMethod.GET)
    public Wrapper sendSmsCode(@ApiParam("手机区号") @NotEmpty(message = "手机区号不能为空")  @RequestParam(required = false) String phonePrefix,
                               @ApiParam("手机号码") @NotEmpty(message = "手机号码不能为空") @RequestParam(required = false) String phone,
                               HttpServletRequest request) {
        // 判断是否重复发送
        String sessionId = request.getSession().getId();
        String result = redisTemplate.opsForValue().get(CommonConstant.SMS_CODE_REDIS_KEY_PREFIX + sessionId);
        if(!StringUtil.isEmpty(result)){
            return WrapperUtil.error(WrapperCode.CAPTCHA_SMS_CODE_REPEAT);
        }
        // 短信验证码存入redis（有效1分钟）
        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().set(CommonConstant.SMS_CODE_REDIS_KEY_PREFIX + sessionId, code, 1L, TimeUnit.MINUTES);
        // 发送短信验证码
        boolean isSuccess = smsService.sendSmsCode(phone, code);
        // 发送失败
        if(!isSuccess){
            redisTemplate.delete(CommonConstant.SMS_CODE_REDIS_KEY_PREFIX + sessionId);
            return WrapperUtil.error(WrapperCode.CAPTCHA_SMS_CODE_SEND_FAIL);
        }
        // 发送成功
        return WrapperUtil.ok();
    }
}
