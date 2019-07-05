package cn.moke.generator.mapper;

import cn.moke.generator.entity.po.Router;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author moke
 */
public interface RouterMapper extends BaseMapper<Router> {


    List<Router> selectAllRouter();

    List<Router> selectAllByRoleId(String roleId);

    List<Router> selectAllRouterAndOperate();

    List<Router> selectByUsername(String username);
}
