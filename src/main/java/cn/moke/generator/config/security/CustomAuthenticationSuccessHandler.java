package cn.moke.generator.config.security;

import cn.moke.generator.entity.po.User;
import cn.moke.generator.entity.vo.TokenVo;
import cn.moke.generator.entity.vo.TokenWrapperVo;
import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.utils.JwtTokenUtil;
import cn.moke.generator.utils.ResponseUtil;
import cn.moke.generator.utils.StringUtil;
import cn.moke.generator.utils.WrapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author moke
 */
@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${moke.authority.token-expire-time-second:3000}")
    private Integer tokenExpireTimeSecond;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        // 获取用户信息
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        TokenVo tokenVo = new TokenVo();
        User user = new User();
        BeanUtils.copyProperties(customUserDetail, tokenVo);
        BeanUtils.copyProperties(customUserDetail, user);
        String json = redisTemplate.opsForValue().get(customUserDetail.getUsername());
        if(!StringUtil.isEmpty(json)){
            TokenWrapperVo tokenWrapperVo = new TokenWrapperVo(json, user);
            ResponseUtil.writeJson(response, null, WrapperUtil.ok(tokenWrapperVo));
            return;
        }

        String tokenVoJsonStr;
        try {
            tokenVoJsonStr = objectMapper.writeValueAsString(tokenVo);
        } catch (JsonProcessingException e) {
            log.error("{}",e);
            return;
        }
        // token存入redis
        if(tokenVoJsonStr != null){
            String jwtToken = JwtTokenUtil.generateToken(tokenVoJsonStr, tokenExpireTimeSecond);
            if(jwtToken != null){
                try {
                    redisTemplate.opsForValue().set(customUserDetail.getUsername(), jwtToken, tokenExpireTimeSecond, TimeUnit.SECONDS);
                }catch(Exception e){
                    log.error("{}", e);
                    ResponseUtil.writeJson(response, HttpStatus.INTERNAL_SERVER_ERROR, WrapperUtil.error(WrapperCode.ERROR));
                    return;
                }
                TokenWrapperVo tokenWrapperVo = new TokenWrapperVo(jwtToken, user);
                ResponseUtil.writeJson(response, null, WrapperUtil.ok(tokenWrapperVo));
                return;
            }
        }
        ResponseUtil.writeJson(response, null, WrapperUtil.error(WrapperCode.AUTH_TOKEN_OBTAIN_FAIL));
    }
}
