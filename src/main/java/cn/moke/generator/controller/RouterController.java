package cn.moke.generator.controller;

import cn.moke.generator.entity.po.Router;
import cn.moke.generator.entity.vo.Wrapper;
import cn.moke.generator.service.RouterService;
import cn.moke.generator.utils.WrapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

/**
 * @author moke
 */
@Api(tags = "前端路由接口")
@RestController
@RequestMapping("/router")
public class RouterController {

    @Resource
    private RouterService routerService;

    @ApiOperation("添加")
    @RequestMapping(value = "", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper add(@Valid @RequestBody Router router){
        routerService.add(router);
        return WrapperUtil.ok();
    }

    @ApiOperation("批量删除")
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public Wrapper deleteBatch(@ApiParam("id集合") @NotEmpty(message = "空集合") @RequestParam Set<String> ids){
        routerService.deleteBatch(ids);
        return WrapperUtil.ok();
    }

    @ApiOperation("修改")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper alter(@ApiParam("id") @PathVariable String id, @Valid @RequestBody Router router){
        routerService.alter(id, router);
        return WrapperUtil.ok();
    }

    @ApiOperation(value = "获取所有路由树（用于路由注册和菜单）")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Wrapper<List<Router>> listAllRouteTree() {
        List<Router> result = routerService.listAllRouteTree();
        return WrapperUtil.ok(result);
    }

    @ApiOperation(value = "获取所有路由树（用于角色分配权限）")
    @RequestMapping(value = "/checked/{roleId}", method = RequestMethod.GET)
    public Wrapper<List<Router>> listAllRouteTreeChecked(@ApiParam("角色Id") @PathVariable String roleId) {
        List<Router> result = routerService.listAllRouteTreeChecked(roleId);
        return WrapperUtil.ok(result);
    }

    @ApiOperation(value = "获取所有路由树及操作树(用于菜单权限数据操作)")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Wrapper<List<Router>> listAllTree() {
        List<Router> result = routerService.listAllTree();
        return WrapperUtil.ok(result);
    }

    @ApiOperation(value = "获取该用户路由树(用于菜单)")
    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    public Wrapper<List<Router>> listUserMenuTree() {
        List<Router> result = routerService.listUserRouteTree();
        return WrapperUtil.ok(result);
    }
}
