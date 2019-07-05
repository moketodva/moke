package cn.moke.generator.mapper;

import cn.moke.generator.entity.po.Path;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author moke
 */
public interface PathMapper extends BaseMapper<Path> {

    /**
     * 查询接口权限匹配规则
     * @return
     */
    List<Path> selectPathAndPermission();

    /**
     * 查出接口角色匹配规则
     */
    List<Path> selectPathAndRole();

    /**
     * 列表查询（级联权限）
     * @param page 分页
     * @param wrapper sql定制
     * @return 结果
     */
    List<Path> selectCascadeList(Page<Path> page, @Param(Constants.WRAPPER) Wrapper wrapper);
}
