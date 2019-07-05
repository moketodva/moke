package cn.moke.generator.config.security;

import cn.moke.generator.constant.SecurityConstant;
import cn.moke.generator.mapper.RolePermissionMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author moke
 */
@Component
public class CustomAccessDecisionManager implements AccessDecisionManager {

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> permits = new ArrayList<>();
        // 角色转换成权限
        for (GrantedAuthority grantedAuthority : authorities) {
            permits.add(grantedAuthority.getAuthority());
            if(grantedAuthority.getAuthority().startsWith(SecurityConstant.ROLE_NAME_PREFIX)){
                System.out.println(grantedAuthority.getAuthority());
                List<String> list = rolePermissionMapper.selectPermissionNameByRoleName(grantedAuthority.getAuthority());
                permits.addAll(list);
            }
        }

        for (ConfigAttribute configAttribute : configAttributes) {
            for (String permit : permits) {
                if(permit.equalsIgnoreCase(configAttribute.getAttribute())){
                    return;
                }
            }
        }
        throw new AccessDeniedException("not allow");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
