package cn.moke.generator.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.moke.generator.entity.po.Role;
import cn.moke.generator.entity.vo.Wrapper;
import cn.moke.generator.service.RoleService;
import cn.moke.generator.utils.WrapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Set;

/**
 * @author moke
 */
@Api(tags = "角色接口")
@RestController
@RequestMapping("/role")
@Validated
public class RoleController {

    @Resource
    private RoleService roleService;

    @ApiOperation("添加")
    @RequestMapping(value = "", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper add(@ApiParam("角色") @Valid @RequestBody Role role){
        roleService.add(role);
        return WrapperUtil.ok();
    }

    @ApiOperation("批量删除")
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public Wrapper deleteBatch(@ApiParam("id集合") @NotEmpty(message = "id集合不能为空") @RequestParam(required = false) Set<String> ids){
        roleService.deleteBatch(ids);
        return WrapperUtil.ok();
    }

    @ApiOperation("修改")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper alter(@ApiParam("id") @PathVariable String id,
                         @ApiParam("角色") @Valid @RequestBody Role role){
        roleService.alter(id, role);
        return WrapperUtil.ok();
    }

    @ApiOperation("明细")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Wrapper<Role> detail(@ApiParam("id") @PathVariable String id){
        Role role = roleService.detail(id);
        return WrapperUtil.ok(role);
    }

    @ApiOperation("列表查询")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Wrapper<Page<Role>> list(@ApiParam("页码") @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                    @ApiParam("单页行数") @Max(value = 50, message = "单页行数不能超过{value}") @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                    @ApiParam("排序字段") @Pattern(regexp="name|description", message = "不存在该排序字段") @RequestParam(required = false) String sort,
                                    @ApiParam("是否升序") @RequestParam(required = false, defaultValue = "true") Boolean isAsc,
                                    @ApiParam("角色名（条件）") @RequestParam(required = false) String name,
                                    @ApiParam("角色描述（条件）") @RequestParam(required = false) String description){
        Page<Role> result = roleService.list(pageNum, pageSize, sort, isAsc, name, description);
        return WrapperUtil.ok(result);
    }

    @ApiOperation("设置/取消默认角色")
    @RequestMapping(value = "/default/{id}", method = RequestMethod.PUT)
    public Wrapper setDefaultRole(@ApiParam("id") @PathVariable String id,
                                  @ApiParam("是否设置") @NotNull(message = "是否设置不能为空") @RequestParam(required = false) Boolean isDefault){
        roleService.setDefaultRole(id, isDefault);
        return WrapperUtil.ok();
    }

    @ApiOperation("设置权限菜单")
    @RequestMapping(value = "/router/{id}", method = RequestMethod.PUT)
    public Wrapper setMenu(@ApiParam("id") @PathVariable String id,
                           @ApiParam("路由id集合") @NotNull(message = "路由id集合不能为null") @RequestParam(required = false) Set<String> routerIds){
        roleService.setMenu(id, routerIds);
        return WrapperUtil.ok();
    }
}
