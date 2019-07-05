package cn.moke.generator.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.moke.generator.entity.po.Path;
import cn.moke.generator.entity.vo.Wrapper;
import cn.moke.generator.service.PathService;
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
import javax.validation.constraints.Pattern;
import java.util.Set;

/**
 * @author moke
 */
@Api(tags = "后端资源接口")
@RestController
@RequestMapping("/path")
@Validated
public class PathController {

    @Resource
    private PathService pathService;

    @ApiOperation("添加")
    @RequestMapping(value = "", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper add(@ApiParam("后端资源") @Valid @RequestBody Path path){
        pathService.add(path);
        return WrapperUtil.ok();
    }

    @ApiOperation("批量删除")
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public Wrapper deleteBatch(@ApiParam("id集合") @NotEmpty(message = "id集合不能为空集合") @RequestParam(required = false) Set<String> ids){
        pathService.deleteBatch(ids);
        return WrapperUtil.ok();
    }

    @ApiOperation("修改")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper alter(@ApiParam("id") @PathVariable String id,
                         @ApiParam("后端资源") @Valid @RequestBody Path path){
        pathService.alter(id, path);
        return WrapperUtil.ok();
    }

    @ApiOperation("明细")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Wrapper<Path> detail(@ApiParam("id") @PathVariable String id){
        Path path = pathService.detail(id);
        return WrapperUtil.ok(path);
    }

    @ApiOperation("列表查询")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Wrapper<Page<Path>> list(@ApiParam("页码") @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                    @ApiParam("单页行数") @Max(value = 50, message = "单页行数不能超过{value}") @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                    @ApiParam("排序字段") @Pattern(regexp = "name|ant_uri", message = "不存在该排序字段") @RequestParam(required = false) String sort,
                                    @ApiParam("是否升序") @RequestParam(required = false, defaultValue = "true") Boolean isAsc,
                                    @ApiParam("后端资源名") @RequestParam(required = false) String name,
                                    @ApiParam("后端资源uri（支持ant风格）") @RequestParam(required = false) String antUri){
        Page<Path> result = pathService.list(pageNum, pageSize, sort, isAsc, name, antUri);
        return WrapperUtil.ok(result);
    }
}
