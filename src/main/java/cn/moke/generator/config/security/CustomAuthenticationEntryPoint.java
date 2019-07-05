package cn.moke.generator.config.security;

import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.utils.ResponseUtil;
import cn.moke.generator.utils.WrapperUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author moke
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        ResponseUtil.writeJson(response, HttpStatus.UNAUTHORIZED, WrapperUtil.error(WrapperCode.UNAUTHORIZED));
    }
}