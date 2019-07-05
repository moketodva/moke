package cn.moke.generator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.moke.generator.entity.po.Permission;

import java.util.Collection;

/**
 * @author moke
 */
public interface PermissionService {

    /**
     * 添加
     * @param permission 权限
     */
    void add(Permission permission);

    /**
     * 批量删除
     * @param ids ids
     */
    void deleteBatch(Collection<String> ids);

    /**
     * 修改
     * @param id id
     * @param permission 权限
     */
    void alter(String id, Permission permission);

    /**
     * 明细
     * @param id id
     * @return 权限
     */
    Permission detail(String id);

    /**
     * 获取列表
     * @param pageNum 页码
     * @param pageSize 单页行数
     * @param sort 排序字段
     * @param isAsc 是否升序
     * @param name 权限名（条件）
     * @param description 权限描述（条件）
     * @return Page<T>
     */
    Page<Permission> list(Integer pageNum, Integer pageSize, String sort, Boolean isAsc, String name, String description);

}
