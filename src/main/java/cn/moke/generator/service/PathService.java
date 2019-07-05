package cn.moke.generator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.moke.generator.entity.po.Path;

import java.util.Collection;

/**
 * @author moke
 */
public interface PathService {

    /**
     * 添加
     * @param path 后端资源
     */
    void add(Path path);

    /**
     * 批量删除
     * @param ids ids
     */
    void deleteBatch(Collection<String> ids);

    /**
     * 修改
     * @param id id
     * @param path 后端资源
     */
    void alter(String id, Path path);

    /**
     * 明细
     * @param id id
     * @return 后端资源
     */
    Path detail(String id);

    /**
     * 列表查询
     * @param pageNum 页码
     * @param pageSize 单页行数
     * @param sort 排序字段
     * @param isAsc 是否升序
     * @param name 后端资源名
     * @param antUri 后端资源uri（支持ant风格）
     * @return Page<T>
     */
    Page<Path> list(Integer pageNum, Integer pageSize, String sort, Boolean isAsc, String name, String antUri);

}
