package cn.moke.generator.config.security;

import cn.moke.generator.constant.SecurityConstant;
import cn.moke.generator.entity.po.Permission;
import cn.moke.generator.entity.po.Path;
import cn.moke.generator.entity.po.Role;
import cn.moke.generator.mapper.PathMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author moke
 */
@Component
public class CustomFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private Map<Path, Collection<ConfigAttribute>> map = new ConcurrentHashMap<>();;

    @Value("${moke.authority.mode:0}")
    private Integer authorityMode;
    @Value("${moke.authority.unit:0}")
    private Integer authorityUnit;
    @Resource
    private PathMapper pathMapper;

    /**
     * 加载所有匹配规则
     */
    public void loadPath(){
        map.clear();
        if(authorityUnit.equals(SecurityConstant.DEFAULT_AUTHORITY_UNIT_ROLE)){
            List<Path> paths = pathMapper.selectPathAndRole();
            for (Path path : paths) {
                List<Role> roles = path.getRoles();
                List<ConfigAttribute> securityConfigs = new ArrayList<>();
                for (Role role : roles) {
                    ConfigAttribute securityConfig = new SecurityConfig(role.getName());
                    securityConfigs.add(securityConfig);
                }
                map.put(path, securityConfigs);
            }
        }else{
            List<Path> paths = pathMapper.selectPathAndPermission();
            for (Path path : paths) {
                Permission permission = path.getPermission();
                List<ConfigAttribute> securityConfigs = new ArrayList<>();
                ConfigAttribute securityConfig = new SecurityConfig(permission.getName());
                securityConfigs.add(securityConfig);
                map.put(path, securityConfigs);
            }
        }
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if(authorityMode == 0){
            return null;
        }

        if(map.size() == 0){
            synchronized (this){
                if(map.size() == 0) {
                    loadPath();
                }
            }
        }

        FilterInvocation fi = (FilterInvocation) object;
        String url = fi.getRequestUrl();
        String httpMethod = fi.getRequest().getMethod();

        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for(Map.Entry<Path, Collection<ConfigAttribute>> entry : map.entrySet()){
            if(antPathMatcher.match(entry.getKey().getAntUri(), url) &&
                    (httpMethod.equalsIgnoreCase(entry.getKey().getMethod()) || entry.getKey().getMethod() == null) ){
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
