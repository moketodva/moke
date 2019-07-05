package cn.moke.generator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.moke.generator.entity.po.Router;

import java.util.Collection;
import java.util.List;

/**
 * @author moke
 */
public interface RouterService {

    /**
     * 添加
     * @param router 前端路由
     */
    void add(Router router);

    /**
     * 批量删除
     * @param ids ids
     */
    void deleteBatch(Collection<String> ids);

    /**
     * 修改
     * @param id id
     * @param router 前端路由
     */
    void alter(String id, Router router);

    /**
     * 明细
     * @param id id
     * @return 前端路由
     */
    Router detail(String id);

    /**
     * 获取列表
     * @param pageNum 页码
     * @param pageSize 单页行数
     * @param sorter 排序规则
     * @param router 列表查询的条件表单
     * @return Page<T>
     */
    Page<Router> list(Integer pageNum, Integer pageSize, String sorter, Router router);

    /**
     * 获取所有路由树
     */
    List<Router> listAllRouteTree();

    /**
     * 获取所有路由树（包含checked）
     * @param roleId
     * @return
     */
    List<Router> listAllRouteTreeChecked(String roleId);

    /**
     * 获取该用户的路由树
     */
    List<Router> listUserRouteTree();

    /**
     *
     */
    List<Router> listAllTree();
}
