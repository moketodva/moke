package cn.moke.generator.config.security;

import cn.moke.generator.entity.po.User;
import cn.moke.generator.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author moke
 */
@Component
public class CustomUserDetailService implements UserDetailsService {

    @Resource
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = Optional.ofNullable(userService.findByUsername(username)).orElse(new User());
        return new CustomUserDetail(user);
    }
}
