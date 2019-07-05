package cn.moke.generator.service.impl;

import cn.moke.generator.constant.CommonConstant;
import cn.moke.generator.entity.dto.PwdAlterDto;
import cn.moke.generator.entity.dto.RegisterDto;
import cn.moke.generator.entity.dto.UserDto;
import cn.moke.generator.entity.dto.UserInfoDto;
import cn.moke.generator.entity.po.Role;
import cn.moke.generator.entity.po.User;
import cn.moke.generator.entity.po.UserRole;
import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.exception.BusinessException;
import cn.moke.generator.mapper.UserMapper;
import cn.moke.generator.mapper.UserRoleMapper;
import cn.moke.generator.service.AuthService;
import cn.moke.generator.service.RoleService;
import cn.moke.generator.utils.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author moke
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RoleService roleService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private HttpServletRequest httpServletRequest;

    @Override
    public void register(RegisterDto registerDto) {
        // 验证码校验
        String captchaId = registerDto.getCaptchaId();
        String tempCaptcha = redisTemplate.opsForValue().get(CommonConstant.CAPTCHA_ID_REDIS_KEY_PREFIX + captchaId);
        if(StringUtil.isEmpty(tempCaptcha)){
            throw new BusinessException(WrapperCode.CAPTCHA_CAPTCHA_EXPIRE);
        }
        if(!tempCaptcha.equals(registerDto.getCaptcha())){
            throw new BusinessException(WrapperCode.CAPTCHA_CAPTCHA_NON_MATCH);
        }

        // 校验短信验证码
        String sessionId = httpServletRequest.getSession().getId();
        String smsCode = redisTemplate.opsForValue().get(CommonConstant.SMS_CODE_REDIS_KEY_PREFIX + sessionId);
        if(StringUtil.isEmpty(smsCode)){
            throw new BusinessException(WrapperCode.CAPTCHA_SMS_CODE_EXPIRE);
        }
        if(!smsCode.equals(registerDto.getSmsCode())){
            throw new BusinessException(WrapperCode.CAPTCHA_SMS_CODE_NON_MATCH);
        }

        // 准备数据
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        BeanUtils.copyProperties(registerDto, user);
        String encodePassword = passwordEncoder.encode(registerDto.getPassword());
        user.setPassword(encodePassword);
        user.setCreateTime(now);
        user.setCreateUser(user.getUsername());
        user.setModifyTime(now);
        user.setModifyUser(user.getUsername());

        // 注册
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e){
            throw new BusinessException(WrapperCode.AUTH_ALREADY_REGISTERED_FAIL);
        }

        // 获取默认角色
        List<Role> roles = roleService.findDefaultRole();

        // 往中间表插入关联数据
        List<UserRole> userRoles = roles.stream()
                .map((role) -> new UserRole(null, user.getId(), role.getId()))
                .collect(Collectors.toList());
        try {
            userRoleMapper.insertBatch(userRoles);
        } catch (DuplicateKeyException e){
            throw new BusinessException(WrapperCode.USER_ADD_FAIL);
        }
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public void checkUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if(user != null){
            throw new BusinessException(WrapperCode.AUTH_ALREADY_REGISTERED_FAIL);
        }
    }

    @Override
    public void alterPassword(PwdAlterDto pwdAlterDto) {
        // 通用数据
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 根据用户名查用户
        User user = userMapper.selectByUsername(username);
        if(user == null){
            throw new BusinessException(WrapperCode.USER_NON_EXIST_USERNAME_FAIL);
        }

        // 对比密码
        if(!passwordEncoder.matches(pwdAlterDto.getOldPassword(), user.getPassword()) ){
            throw new BusinessException(WrapperCode.AUTH_USERNAME_PASSWORD_INCORRECT_AUTH);
        }

        // 准备数据
        user.setPassword(passwordEncoder.encode(pwdAlterDto.getNewPassword()));
        user.setModifyTime(LocalDateTime.now());
        user.setModifyUser(user.getUsername());

        // 更新密码
        int affectNum = userMapper.updateById(user);
        if(affectNum != 1){
            throw new BusinessException(WrapperCode.AUTH_PASSWORD_ALTER_FAIL);
        }

        // 取消之前的Token
        Boolean isDelete = redisTemplate.delete(username);
        if(!isDelete){
            String result = redisTemplate.opsForValue().get(username);
            if(StringUtil.isEmpty(result)){
                throw new BusinessException(WrapperCode.AUTH_PASSWORD_ALTER_FAIL);
            }
        }
    }

    @Override
    public void modifyUserInfo(UserInfoDto userInfoDto) {
        // 通用数据
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime now = LocalDateTime.now();

        // 准备数据
        User user = new User();
        BeanUtils.copyProperties(userInfoDto, user);
        user.setModifyTime(now);
        user.setModifyUser(username);

        // 修改用户
        int affectNum = userMapper.update(user, Wrappers.<User>update().eq("username", username));
        if(affectNum != 1){
            throw new BusinessException(WrapperCode.AUTH_MODIFY_USER_INFO_FAIL);
        }
    }
}
