package cn.moke.generator.mapper;

import cn.moke.generator.entity.po.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author moke
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 批量添加
     * @param userRoles 用户-角色
     * @return 影响行数
     */
    int insertBatch(List<UserRole> userRoles);
}
