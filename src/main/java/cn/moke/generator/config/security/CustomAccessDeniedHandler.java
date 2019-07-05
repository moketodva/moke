package cn.moke.generator.config.security;

import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.utils.ResponseUtil;
import cn.moke.generator.utils.WrapperUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author moke
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        ResponseUtil.writeJson(response, HttpStatus.FORBIDDEN, WrapperUtil.error(WrapperCode.FORBIDDEN));
    }
}
