package cn.moke.generator.config.security;

import cn.moke.generator.constant.SecurityConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author moke
 */

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (request != null
                && request.getContentType() != null
                && (request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)
                || request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE))) {

            UsernamePasswordAuthenticationToken authRequest = null;
            try {
                ServletInputStream inputStream = request.getInputStream();
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String,String> authenticationMap = objectMapper.readValue(inputStream, Map.class);
                authRequest = new UsernamePasswordAuthenticationToken(
                        authenticationMap.get(SecurityConstant.USERNAME_FIELD_NAME), authenticationMap.get(SecurityConstant.PASSWORD_FIELD_NAME));
            } catch (IOException e) {
                e.printStackTrace();
                authRequest = new UsernamePasswordAuthenticationToken("", "");
            }finally {
                setDetails(request, authRequest);
                return this.getAuthenticationManager().authenticate(authRequest);
            }
        } else {
            return super.attemptAuthentication(request, response);
        }
    }
}
