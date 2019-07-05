package cn.moke.generator.mapper;

import cn.moke.generator.entity.po.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author moke
 */
public interface RoleMapper extends BaseMapper<Role> {

    List<Role> selectBatchIdsForUpdate(Collection<? extends Serializable> ids);
}
