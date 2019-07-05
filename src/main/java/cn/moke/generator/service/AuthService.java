package cn.moke.generator.service;


import cn.moke.generator.entity.dto.PwdAlterDto;
import cn.moke.generator.entity.dto.RegisterDto;
import cn.moke.generator.entity.dto.UserDto;
import cn.moke.generator.entity.dto.UserInfoDto;

/**
 * @author moke
 */
public interface AuthService {

    /**
     * 注册
     * @param registerDto 用户信息
     */
    void register(RegisterDto registerDto);

    /**
     * 检测用户名是否已经被注册
     * @param username 用户名
     */
    void checkUsername(String username);

    /**
     * 修改密码
     * @param pwdAlterDto 密码修改VO
     */
    void alterPassword(PwdAlterDto pwdAlterDto);

    /**
     * 修改个人信息
     * @param userInfoDto 个人信息
     */
    void modifyUserInfo(UserInfoDto userInfoDto);
}
