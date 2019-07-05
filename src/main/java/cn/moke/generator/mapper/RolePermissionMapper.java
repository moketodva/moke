package cn.moke.generator.mapper;

import cn.moke.generator.entity.po.RolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * @author moke
 */
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 根据角色名查询权限名
     * @param roleName 角色名
     * @return 权限名集合
     */
    @Cacheable("RolePermission")
    List<String> selectPermissionNameByRoleName(String roleName);

    /**
     * 批量添加
     * @param rolePermissions 角色-权限
     * @return 影响行数
     */
    int insertBatch(List<RolePermission> rolePermissions);
}
