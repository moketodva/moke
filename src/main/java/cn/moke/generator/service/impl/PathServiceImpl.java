package cn.moke.generator.service.impl;

import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.exception.BusinessException;
import cn.moke.generator.service.PathService;
import cn.moke.generator.utils.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.moke.generator.entity.po.Path;
import cn.moke.generator.mapper.PathMapper;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * @author moke
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class PathServiceImpl implements PathService{

    @Resource
    private PathMapper pathMapper;

    @Override
    public void add(Path path) {
        // 通用数据
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime now = LocalDateTime.now();

        // 准备数据
        path.setId(null);
        path.setCreateTime(now);
        path.setCreateUser(username);
        path.setModifyTime(now);
        path.setModifyUser(username);

        // 添加后端资源
        try {
            pathMapper.insert(path);
        } catch (DuplicateKeyException e){
            throw new BusinessException(WrapperCode.PATH_ADD_REPEAT_FAIL);
        }
    }

    @Override
    public void deleteBatch(Collection<String> ids) {
        // 批量删除后端资源
        int affectNum = pathMapper.deleteBatchIds(ids);
        if(affectNum != ids.size()){
            throw new BusinessException(WrapperCode.PATH_DELETE_FAIL);
        }
    }

    @Override
    public void alter(String id, Path path) {
        // 通用数据
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 准备数据
        path.setId(id);
        path.setCreateTime(null);
        path.setCreateUser(null);
        path.setModifyTime(LocalDateTime.now());
        path.setModifyUser(username);

        // 修改后端资源
        int affectNum = pathMapper.updateById(path);
        if(affectNum != 1){
            throw new BusinessException(WrapperCode.PATH_ALTER_FAIL);
        }
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public Path detail(String id) {
        return pathMapper.selectById(id);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public Page<Path> list(Integer pageNum, Integer pageSize, String sort, Boolean isAsc, String name, String antUri) {
        Page<Path> page = new Page<>(pageNum, pageSize);
        List<Path> paths = pathMapper.selectCascadeList(page, Wrappers.<Path>query()
                .like(!StringUtil.isEmpty(name), "p.name", name)
                .like(!StringUtil.isEmpty(antUri), "p.ant_uri", antUri)
                .orderBy(StringUtil.isEmpty(sort), false, "p.id")
                .orderBy(!StringUtil.isEmpty(sort), isAsc, sort));
        page.setRecords(paths);
        return page;
    }
}
