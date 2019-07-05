package cn.moke.generator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.moke.generator.entity.po.Role;

import java.util.Collection;
import java.util.List;

/**
 * @author moke
 */
public interface RoleService {

    /**
     * 添加
     * @param role 角色
     */
    void add(Role role);

    /**
     * 批量删除
     * @param ids ids
     */
    void deleteBatch(Collection<String> ids);

    /**
     * 修改
     * @param id id
     * @param role 角色
     */
    void alter(String id, Role role);

    /**
     * 明细
     * @param id id
     * @return 角色
     */
    Role detail(String id);

    /**
     * 获取列表
     * @param pageNum 页码
     * @param pageSize 单页行数
     * @param sort 排序字段
     * @param isAsc 是否升序
     * @param name 角色名（条件）
     * @param description 角色描述（条件）
     * @return Page<T>
     */
    Page<Role> list(Integer pageNum, Integer pageSize, String sort, Boolean isAsc, String name, String description);

    /**
     * 设置/取消默认角色
     * @param id 主键
     * @param isDefault true设置, false取消
     */
    void setDefaultRole(String id, Boolean isDefault);

    /**
     * 设置菜单权限
     * @param id 主键
     * @param routerIds 路由主键
     */
    void setMenu(String id, Collection<String> routerIds);

    /**
     * 查询默认角色
     * @return 角色集合
     */
    List<Role> findDefaultRole();

}
