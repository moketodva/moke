package cn.moke.generator.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.moke.generator.entity.po.Dict;
import cn.moke.generator.entity.vo.Wrapper;
import cn.moke.generator.service.DictService;
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
@Api(tags = "字典接口")
@RestController
@RequestMapping("/dict")
@Validated
public class DictController {

    @Resource
    private DictService dictService;

    @ApiOperation("添加")
    @RequestMapping(value = "", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper add(@ApiParam("字典") @Valid @RequestBody Dict dict){
        dictService.add(dict);
        return WrapperUtil.ok();
    }

    @ApiOperation("批量删除")
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public Wrapper deleteBatch(@ApiParam("id集合") @NotEmpty(message = "id集合不能为空集合") @RequestParam(required = false) Set<String> ids){
        dictService.deleteBatch(ids);
        return WrapperUtil.ok();
    }

    @ApiOperation("修改")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Wrapper alter(@ApiParam("id") @PathVariable String id,
                         @ApiParam("字典") @Valid @RequestBody Dict dict){
        dictService.alter(id, dict);
        return WrapperUtil.ok();
    }

    @ApiOperation("明细")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Wrapper<Dict> detail(@ApiParam("id") @PathVariable String id){
        Dict dict = dictService.detail(id);
        return WrapperUtil.ok(dict);
    }

    @ApiOperation("列表查询")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Wrapper<Page<Dict>> list(@ApiParam("页码") @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                    @ApiParam("单页行数") @Max(value = 50, message = "单页行数不能超过{value}") @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                    @ApiParam("排序字段") @Pattern(regexp = "description|name", message = "不存在该排序字段") @RequestParam(required = false) String sort,
                                    @ApiParam("是否升序") @RequestParam(required = false, defaultValue = "true") Boolean isAsc,
                                    @ApiParam("字典类型描述（条件）") @RequestParam(required = false) String description){
        Page<Dict> result = dictService.list(pageNum, pageSize, sort, isAsc, description);
        return WrapperUtil.ok(result);
    }
}
