package cn.moke.generator.service.impl;

import cn.moke.generator.entity.po.Path;
import cn.moke.generator.entity.po.Router;
import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.exception.BusinessException;
import cn.moke.generator.mapper.PathMapper;
import cn.moke.generator.mapper.RouterMapper;
import cn.moke.generator.service.PermissionService;
import cn.moke.generator.utils.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.moke.generator.entity.po.Permission;
import cn.moke.generator.mapper.PermissionMapper;

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
public class PermissionServiceImpl implements PermissionService{

    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private PathMapper pathMapper;
    @Resource
    private RouterMapper routerMapper;

    @Override
    public void add(Permission permission) {
        // 通用数据
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime now = LocalDateTime.now();

        // 准备数据
        permission.setId(null);
        permission.setCreateTime(now);
        permission.setCreateUser(username);
        permission.setModifyTime(now);
        permission.setModifyUser(username);

        // 添加权限
        try {
            permissionMapper.insert(permission);
        } catch (DuplicateKeyException e){
            throw new BusinessException(WrapperCode.PERMISSION_ADD_REPEAT_FAIL);
        }
    }

    @Override
    public void deleteBatch(Collection<String> ids) {
        List<Path> paths = pathMapper.selectList(Wrappers.<Path>query().in("permission_id", ids));
        List<Router> routers = routerMapper.selectList(Wrappers.<Router>query().in("permission_id", ids));
        if(paths.size() > 0 || routers.size() > 0){
            throw new BusinessException(WrapperCode.PERMISSION_RELATIVE_DELETE_FAIL);
        }
        // 批量删除权限
        int affectNum = permissionMapper.deleteBatchIds(ids);
        if(affectNum != ids.size()){
            throw new BusinessException(WrapperCode.PERMISSION_DELETE_FAIL);
        }
    }

    @Override
    public void alter(String id, Permission permission) {
        // 通用数据
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 准备数据
        permission.setId(id);
        permission.setCreateTime(null);
        permission.setCreateUser(null);
        permission.setModifyTime(LocalDateTime.now());
        permission.setModifyUser(username);

        // 修改权限
        int affectNum = permissionMapper.updateById(permission);
        if(affectNum != 1){
            throw new BusinessException(WrapperCode.PERMISSION_ALTER_FAIL);
        }
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public Permission detail(String id) {
        return permissionMapper.selectById(id);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public Page<Permission> list(Integer pageNum, Integer pageSize, String sort, Boolean isAsc, String name, String description) {
        Page<Permission> page = new Page<>(pageNum, pageSize);
        return (Page<Permission>) permissionMapper.selectPage(page, Wrappers.<Permission>query()
                .like(!StringUtil.isEmpty(name), "name", name)
                .like(!StringUtil.isEmpty(description), "description", description)
                .orderBy(StringUtil.isEmpty(sort), false, "id")
                .orderBy(!StringUtil.isEmpty(sort), isAsc, sort));
    }

}
