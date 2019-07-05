package cn.moke.generator.utils;

import cn.moke.generator.constant.SecurityConstant;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author moke
 */
@Slf4j
public class JwtTokenUtil {

    private final static String SIGNING_KEY = "xboot";
    private final static SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

    /**
     * 生成token
     * @param subject 主体
     * @param expirationSeconds 过期时长
     * @return
     */
    public static String generateToken(String subject, int expirationSeconds) {
        String jwtToken = null;
        try {
            jwtToken = Jwts.builder()
                    .setClaims(null)
                    .setSubject(subject)
                    .setExpiration(new Date(System.currentTimeMillis() + expirationSeconds * 1000))
                    .signWith(signatureAlgorithm, SIGNING_KEY)
                    .compact();
        } catch (Exception e){
            log.error("{}", e);
        }
        return jwtToken;
    }

    /**
     * 从token从取出主体信息
     * @param token
     * @return
     */
    public static String parseToken(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        Claims claims = Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public static Claims claims(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token).getBody();
        return claims;
    }

}