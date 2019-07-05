package cn.moke.generator.controller;

import cn.moke.generator.constant.Group;
import cn.moke.generator.entity.dto.UserDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.moke.generator.entity.po.User;
import cn.moke.generator.entity.vo.Wrapper;
import cn.moke.generator.service.UserService;
import cn.moke.generator.utils.WrapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Set;

/**
 * @author moke
 */
@Api(tags = "用户接口")
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation("添加")
    @RequestMapping(value = "", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper add(@ApiParam("用户") @Validated(Group.Add.class) @RequestBody UserDto userDto){
        userService.add(userDto);
        return WrapperUtil.ok();
    }

    @ApiOperation("批量删除")
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public Wrapper deleteBatch(@ApiParam("id集合") @NotEmpty(message = "id集合不能为空集合") @RequestParam Set<String> ids){
        userService.deleteBatch(ids);
        return WrapperUtil.ok();
    }

    @ApiOperation("修改")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper alter(@ApiParam("id") @PathVariable String id,
                         @ApiParam("用户") @Valid @RequestBody UserDto userDto){
        userService.alter(id, userDto);
        return WrapperUtil.ok();
    }

    @ApiOperation("明细")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Wrapper<User> detail(@ApiParam("id") @PathVariable String id){
        User user = userService.detail(id);
        return WrapperUtil.ok(user);
    }

    @ApiOperation("列表查询")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Wrapper<Page<User>> list(@ApiParam("页码") @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                            @ApiParam("单页行数") @Max(value = 50, message = "单页行数不能超过{value}") @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                            @ApiParam("排序字段") @Pattern(regexp = "age|gender", message = "不存在该排序字段") @RequestParam(required = false) String sort,
                                            @ApiParam("是否升序") @RequestParam(required = false, defaultValue = "true") Boolean isAsc,
                                            @ApiParam("用户名（条件）") @RequestParam(required = false) String username,
                                            @ApiParam("昵称（条件）") @RequestParam(required = false) String nickname){
        Page<User> result = userService.list(pageNum, pageSize, sort, isAsc, username, nickname);
        return WrapperUtil.ok(result);
    }

    @ApiOperation("启用用户")
    @RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
    public Wrapper enable(@ApiParam("id") @PathVariable String id){
        userService.enable(id);
        return WrapperUtil.ok();
    }

    @ApiOperation("禁用用户")
    @RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
    public Wrapper disable(@ApiParam("id") @PathVariable String id){
        userService.disable(id);
        return WrapperUtil.ok();
    }

    @ApiOperation("配置角色")
    @RequestMapping(value = "/setRole/{id}", method = RequestMethod.PUT)
    public Wrapper configureRole(@ApiParam("id") @PathVariable String id,
                                 @ApiParam("角色id集合") @NotNull(message = "角色id集合不能为空") @RequestParam Set<String> roleIds){
        userService.configureRole(id, roleIds);
        return WrapperUtil.ok();
    }
}
