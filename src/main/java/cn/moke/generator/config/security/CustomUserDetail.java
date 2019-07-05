package cn.moke.generator.config.security;

import cn.moke.generator.entity.po.Permission;
import cn.moke.generator.entity.po.Role;
import cn.moke.generator.entity.po.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author moke
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetail implements UserDetails {

    private String id;

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private String gender;

    private LocalDate birthday;

    private Integer age;

    private String phonePrefix;

    private String phone;

    private String email;

    private String publicId;

    private String publicType;

    private Boolean isLock;

    private List<Role> roles;

    private List<Permission> permissions;

    public CustomUserDetail(User user){
        BeanUtils.copyProperties(user, this);
//        this.id = user.getId();
//        this.username = user.getUsername();
//        this.password = user.getPassword();
//        this.isLock = user.getIsLock() == null ? false : user.getIsLock();
//        this.roles = user.getRoles();
//        this.permissions = user.getPermissions();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if(this.getRoles() == null){
            return grantedAuthorities;
        }else{
            List<Role> roles = this.getRoles();
            if(roles != null){
                for (Role role : roles) {
                    SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.getName());
                    grantedAuthorities.add(simpleGrantedAuthority);
                }
            }

            if(permissions != null){
                for (Permission permission : permissions) {
                    SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(permission.getName());
                    grantedAuthorities.add(simpleGrantedAuthority);
                }
            }

            return grantedAuthorities;
        }
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isLock == null || !isLock;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
