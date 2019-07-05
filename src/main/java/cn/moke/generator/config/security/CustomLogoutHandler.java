package cn.moke.generator.config.security;

import cn.moke.generator.entity.po.User;
import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.utils.ResponseUtil;
import cn.moke.generator.utils.StringUtil;
import cn.moke.generator.utils.WrapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author moke
 */
@Component
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if(authentication == null){
            ResponseUtil.writeJson(response, HttpStatus.UNAUTHORIZED, WrapperUtil.error(WrapperCode.UNAUTHORIZED));
            return;
        }
        CustomUserDetail user = (CustomUserDetail)authentication.getPrincipal();
        if(user == null){
            ResponseUtil.writeJson(response, HttpStatus.INTERNAL_SERVER_ERROR, WrapperUtil.error(WrapperCode.ERROR));
            return;
        }
        try{
            Boolean isDelete = redisTemplate.delete(user.getUsername());
            if(!isDelete){
                String tokenStr = redisTemplate.opsForValue().get(user.getUsername());
                if(!StringUtil.isEmpty(tokenStr)){
                    ResponseUtil.writeJson(response, null, WrapperUtil.error(WrapperCode.AUTH_LOGOUT_FAIL));
                }
            }
        }catch(Exception e){
            log.error("{}", e);
            ResponseUtil.writeJson(response, HttpStatus.INTERNAL_SERVER_ERROR, WrapperUtil.error(WrapperCode.ERROR));
        }
    }
}
