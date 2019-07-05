package cn.moke.generator.service.impl;

import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.exception.BusinessException;
import cn.moke.generator.service.DictService;
import cn.moke.generator.utils.StringUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.moke.generator.entity.po.Dict;
import cn.moke.generator.mapper.DictMapper;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * @author moke
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class DictServiceImpl implements DictService{

    @Resource
    private DictMapper dictMapper;

    @Override
    public void add(Dict dict) {
        // 通用数据
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime now = LocalDateTime.now();

        // 准备数据
        dict.setCreateTime(now);
        dict.setCreateUser(username);
        dict.setModifyTime(now);
        dict.setModifyUser(username);

        // 添加字典
        try {
            dictMapper.insert(dict);
        } catch (DuplicateKeyException e){
            throw new BusinessException(WrapperCode.DICT_ADD_REPEAT_FAIL);
        }
    }

    @Override
    public void deleteBatch(Collection<String> ids) {
        // 批量删除字典
        int affectNum = dictMapper.deleteBatchIds(ids);
        if(affectNum != ids.size()){
            throw new BusinessException(WrapperCode.DICT_DELETE_FAIL);
        }
    }

    @Override
    public void alter(String id, Dict dict) {
        // 通用数据
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 准备数据
        dict.setId(id);
        dict.setCreateTime(null);
        dict.setCreateUser(null);
        dict.setModifyTime(LocalDateTime.now());
        dict.setModifyUser(username);

        // 修改字典
        int affectNum = dictMapper.updateById(dict);
        if(affectNum != 1){
            throw new BusinessException(WrapperCode.DICT_ALTER_FAIL);
        }
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public Dict detail(String id) {
        return dictMapper.selectById(id);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public Page<Dict> list(Integer pageNum, Integer pageSize, String sort, Boolean isAsc, String description) {
        Page<Dict> page = new Page<>(pageNum, pageSize);
        return (Page<Dict>) dictMapper.selectPage(page, new QueryWrapper<Dict>()
                .like(!StringUtil.isEmpty(description), "description", description)
                .orderBy(StringUtil.isEmpty(sort), false, "id")
                .orderBy(!StringUtil.isEmpty(sort), isAsc, sort));
    }
}
