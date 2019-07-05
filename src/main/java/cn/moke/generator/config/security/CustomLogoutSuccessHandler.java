package cn.moke.generator.config.security;

import cn.moke.generator.utils.ResponseUtil;
import cn.moke.generator.utils.WrapperUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author moke
 */
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        ResponseUtil.writeJson(response, null, WrapperUtil.ok());
    }
}
