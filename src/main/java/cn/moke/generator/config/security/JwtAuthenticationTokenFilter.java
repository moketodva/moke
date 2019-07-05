package cn.moke.generator.config.security;

import cn.moke.generator.constant.SecurityConstant;
import cn.moke.generator.entity.vo.TokenVo;
import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.utils.JwtTokenUtil;
import cn.moke.generator.utils.ResponseUtil;
import cn.moke.generator.utils.WrapperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author moke
 */
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Value("${moke.authority.login-uri:/auth/login}")
    private String loginUri;
    @Value("${moke.authority.logout-uri:/auth/logout}")
    private String logoutUri;
    @Value("${moke.authority.token-expire-time-second:3000}")
    private Integer tokenExpireTimeSecond;
    @Value("${moke.authority.token-fresh-time-second:1000}")
    private Integer tokenFreshTimeSecond;
    @Value("${moke.authority.is-auto-fresh:true}")
    private Boolean isAutoFresh;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String tokenPrefix = SecurityConstant.TOKEN_PREFIX;
        String authHeader = request.getHeader(SecurityConstant.TOKEN_REQUEST_HEADER);
        // 判断uri是否匹配登录路径,且带有Token相应的请求头及前缀,最后还没被认证过
        if (authHeader != null
                && authHeader.startsWith(tokenPrefix)
                && !loginUri.equals(request.getRequestURI())
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 截取出JwtToken
            String authToken = authHeader.substring(tokenPrefix.length());
            String tokenVOJsonStr;
            boolean isFresh = false;
            String newToken = null;
            try {
                Claims claims = JwtTokenUtil.claims(authToken);
                tokenVOJsonStr = claims.getSubject();
                // 生成新Token
                if(isAutoFresh){
                    long time = (claims.getExpiration().getTime() - System.currentTimeMillis())/1000;
                    if(time < tokenFreshTimeSecond){
                        isFresh = true;
                        newToken = JwtTokenUtil.generateToken(tokenVOJsonStr, tokenExpireTimeSecond);
                    }
                }
            } catch (ExpiredJwtException e){
                ResponseUtil.writeJson(response, HttpStatus.UNAUTHORIZED, WrapperUtil.error(WrapperCode.UNAUTHORIZED));
                return;
            } catch (SignatureException e){
                ResponseUtil.writeJson(response, HttpStatus.UNAUTHORIZED, WrapperUtil.error(WrapperCode.UNAUTHORIZED));
                return;
            } catch (MalformedJwtException e){
                ResponseUtil.writeJson(response, HttpStatus.UNAUTHORIZED, WrapperUtil.error(WrapperCode.UNAUTHORIZED));
                return;
            } catch (Exception e){
                logger.error("{}", e);
                ResponseUtil.writeJson(response, HttpStatus.INTERNAL_SERVER_ERROR, WrapperUtil.error(WrapperCode.ERROR));
                return;
            }
            // json转换
            TokenVo tokenVo;
            try {
                tokenVo = objectMapper.readValue(tokenVOJsonStr, TokenVo.class);
            } catch (IOException e) {
                log.error("{}",e);
                return;
            }

            if(tokenVo != null){
                // 根据username从Redis取JwtToken
                String username = tokenVo.getUsername();
                String jwtToken = redisTemplate.opsForValue().get(username);
                // 比对JwtToken（
                //      有这步是为了让JwtToken能"失效"
                //      当用户修改密码, 可以直接将redis里的相应key-value删除.
                //      当用户注销的时候, 可以直接将redis里的相应key-value删除.
                // ）
                if(authToken.equals(jwtToken)){
                    // 自动刷新令牌操作
                    if(isFresh){
                        response.setHeader(SecurityConstant.TOKEN_RESPONSE_HEADER, newToken);
                        response.setHeader(HttpHeaders.CACHE_CONTROL,"no-store");
                        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,"Authorization");
                        try{
                            redisTemplate.opsForValue().set(username, newToken, tokenExpireTimeSecond,  TimeUnit.SECONDS);
                        }catch(Exception e){
                            log.error("{}", e);
                            ResponseUtil.writeJson(response, HttpStatus.INTERNAL_SERVER_ERROR, WrapperUtil.error(WrapperCode.ERROR));
                            return;
                        }
                    }
                    // 设置Authentication
                    CustomUserDetail customUserDetail = new CustomUserDetail();
                    BeanUtils.copyProperties(tokenVo, customUserDetail);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(customUserDetail, null, customUserDetail.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    chain.doFilter(request, response);
                    return;
                }else{
                    ResponseUtil.writeJson(response, HttpStatus.UNAUTHORIZED, WrapperUtil.error(WrapperCode.UNAUTHORIZED));
                    return;
                }
            }else{
                ResponseUtil.writeJson(response, HttpStatus.BAD_REQUEST, WrapperUtil.error(WrapperCode.EXCEPTION_JSON_PARSER));
                return;
            }
        }
        chain.doFilter(request, response);
    }
}