package cn.moke.generator.controller;

import cn.moke.generator.config.security.CustomFilterInvocationSecurityMetadataSource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.moke.generator.entity.po.Permission;
import cn.moke.generator.entity.vo.Wrapper;
import cn.moke.generator.service.PermissionService;
import cn.moke.generator.utils.WrapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Set;

/**
 * @author moke
 */
@Api(tags = "权限接口")
@RestController
@RequestMapping("/permission")
@Validated
public class PermissionController {

    @Resource
    private PermissionService permissionService;
    @Resource
    private CustomFilterInvocationSecurityMetadataSource customFilterInvocationSecurityMetadataSource;

    @ApiOperation("添加")
    @RequestMapping(value = "", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper add(@ApiParam("权限") @Valid @RequestBody Permission permission){
        permissionService.add(permission);
        return WrapperUtil.ok();
    }

    @ApiOperation("批量删除")
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public Wrapper deleteBatch(@ApiParam("id集合") @NotEmpty(message = "id集合不能为空集合") @RequestParam(required = false) Set<String> ids){
        permissionService.deleteBatch(ids);
        return WrapperUtil.ok();
    }

    @ApiOperation("修改")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper alter(@ApiParam("id") @PathVariable String id,
                         @ApiParam("权限") @Valid @RequestBody Permission permission){
        permissionService.alter(id, permission);
        return WrapperUtil.ok();
    }

    @ApiOperation("明细")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Wrapper<Permission> detail(@ApiParam("id") @PathVariable String id){
        Permission permission = permissionService.detail(id);
        return WrapperUtil.ok(permission);
    }

    @ApiOperation("列表查询")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Wrapper<Page<Permission>> list(@ApiParam("页码") @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                          @ApiParam("单页行数") @Max(value = 50, message = "单页行数不能超过{value}") @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                          @ApiParam("排序字段") @Pattern(regexp = "name|description", message = "不存在该排序字段") @RequestParam(required = false) String sort,
                                          @ApiParam("是否升序") @RequestParam(required = false, defaultValue = "true") Boolean isAsc,
                                          @ApiParam("权限名（条件）") @RequestParam(required = false) String name,
                                          @ApiParam("权限描述（条件）") @RequestParam(required = false) String description){
        Page<Permission> result = permissionService.list(pageNum, pageSize, sort, isAsc, name, description);
        return WrapperUtil.ok(result);
    }

    @ApiOperation("权限相关更改生效")
    @RequestMapping(value = "/takeEffect", method = RequestMethod.PUT)
    @CacheEvict(value = "RolePermission", allEntries = true)
    public Wrapper<Permission> takeEffect(){
        customFilterInvocationSecurityMetadataSource.loadPath();
        return WrapperUtil.ok();
    }
}
