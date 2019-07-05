package cn.moke.generator.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.moke.generator.entity.po.Dict;

import java.util.Collection;

/**
 * @author moke
 */
public interface DictService {

    /**
     * 添加
     * @param dict 字典
     */
    void add(Dict dict);

    /**
     * 批量删除
     * @param ids ids
     */
    void deleteBatch(Collection<String> ids);

    /**
     * 修改
     * @param id id
     * @param dict 字典
     */
    void alter(String id, Dict dict);

    /**
     * 明细
     * @param id id
     * @return 字典
     */
    Dict detail(String id);

    /**
     * 列表查询
     * @param pageNum 页码
     * @param pageSize 单页行数
     * @param sort 排序字段
     * @param isAsc 是否升序
     * @param description 字典类型描述（条件）
     * @return Page<T>
     */
    Page<Dict> list(Integer pageNum, Integer pageSize, String sort, Boolean isAsc, String description);
}
