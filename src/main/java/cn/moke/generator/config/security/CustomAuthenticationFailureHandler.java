package cn.moke.generator.config.security;

import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.utils.ResponseUtil;
import cn.moke.generator.utils.WrapperUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author moke
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        ResponseUtil.writeJson(response, null, WrapperUtil.error(WrapperCode.AUTH_USERNAME_PASSWORD_INCORRECT_AUTH));
    }
}
