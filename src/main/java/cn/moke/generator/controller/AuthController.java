package cn.moke.generator.controller;

import cn.moke.generator.entity.dto.*;
import cn.moke.generator.entity.vo.Wrapper;
import cn.moke.generator.service.AuthService;
import cn.moke.generator.utils.WrapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * @author moke
 */
@Api(tags = "认证接口")
@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    @Resource
    private AuthService authService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @ApiOperation("登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper login(@ApiParam("用户登录信息") @RequestBody LoginDto loginDto){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String jwtToken = redisTemplate.opsForValue().get(name);
        return WrapperUtil.ok(jwtToken);
    }

    @ApiOperation("注销")
    @RequestMapping(value = "/logout", method = RequestMethod.PUT)
    public Wrapper logout(){
        return WrapperUtil.ok();
    }

    @ApiOperation("注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper register(@ApiParam("注册信息") @Valid @RequestBody RegisterDto registerDto){
        authService.register(registerDto);
        return WrapperUtil.ok();
    }

    @ApiOperation("校验用户名是否可用")
    @GetMapping("/check")
    public Wrapper checkUsername(@ApiParam("用户名") @NotEmpty(message = "用户名不能为空") @RequestParam(required = false) String username){
        authService.checkUsername(username);
        return WrapperUtil.ok();
    }

    @ApiOperation("修改密码")
    @RequestMapping(value = "/pwd", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper alterPassword(@ApiParam("密码修改信息") @Valid @RequestBody PwdAlterDto pwdAlterDto){
        authService.alterPassword(pwdAlterDto);
        return WrapperUtil.ok();
    }

    @ApiOperation("修改个人信息")
    @RequestMapping(value = "/info", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper modifyUserInfo(@ApiParam("个人信息") @Valid @RequestBody UserInfoDto userInfoDto){
        authService.modifyUserInfo(userInfoDto);
        return WrapperUtil.ok();
    }
}

