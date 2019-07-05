package cn.moke.generator.service.impl;

import cn.moke.generator.entity.po.*;
import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.exception.BusinessException;
import cn.moke.generator.mapper.*;
import cn.moke.generator.service.RoleService;
import cn.moke.generator.utils.StringUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author moke
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class RoleServiceImpl implements RoleService{

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;
    @Resource
    private RouterMapper routerMapper;
    private Lock lock = new ReentrantLock();

    @Override
    public void add(Role role) {
        // 通用数据
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime now = LocalDateTime.now();

        // 准备数据
        role.setId(null);
        role.setIsDefault(null);
        role.setCreateTime(now);
        role.setCreateUser(username);
        role.setModifyTime(now);
        role.setModifyUser(username);

        // 添加角色
        try {
            roleMapper.insert(role);
        } catch (DuplicateKeyException e){
            throw new BusinessException(WrapperCode.ROLE_ADD_REPEAT_FAIL);
        }
    }

    @Override
    public void deleteBatch(Collection<String> ids) {
        List<UserRole> userRoles = userRoleMapper.selectList(new QueryWrapper<UserRole>().in("role_id", ids));
        if(userRoles.size() > 0){
            throw new BusinessException(WrapperCode.ROLE_RELATIVE_DELETE_FAIL);
        }
        // 批量删除角色
        int affectNum = roleMapper.deleteBatchIds(ids);
        if(affectNum != ids.size()){
            throw new BusinessException(WrapperCode.ROLE_DELETE_FAIL);
        }
    }

    @Override
    public void alter(String id, Role role) {
        // 通用数据
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 准备数据
        role.setId(id);
        role.setCreateTime(null);
        role.setCreateUser(null);
        role.setModifyTime(LocalDateTime.now());
        role.setModifyUser(username);

        // 修改角色
        int affectNum = roleMapper.updateById(role);
        if(affectNum != 1){
            throw new BusinessException(WrapperCode.ROLE_ALTER_FAIL);
        }
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public Role detail(String id) {
        return roleMapper.selectById(id);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public Page<Role> list(Integer pageNum, Integer pageSize, String sort, Boolean isAsc, String name, String description) {
        Page<Role> page = new Page<>(pageNum, pageSize);
        return (Page<Role>) roleMapper.selectPage(page, new QueryWrapper<Role>()
                .like(!StringUtil.isEmpty(name),"name", name)
                .like(!StringUtil.isEmpty(description), "description", description)
                .orderBy(StringUtil.isEmpty(sort), false, "id")
                .orderBy(!StringUtil.isEmpty(sort), isAsc, sort));
    }

    @Override
    public void setDefaultRole(String id, Boolean isDefault) {
        Role role = new Role();
        role.setId(id);
        role.setIsDefault(isDefault);
        int i = roleMapper.updateById(role);
        if(i != 1){
            throw new BusinessException(WrapperCode.ROLE_DEFAULT_FAIL);
        }
    }

    @Override
    public void setMenu(String id, Collection<String> routerIds) {
        // 由于该操作流量不大, 直接加上使用简单粗暴线程锁使用方式避免死锁
        // 注：分布式环境仍有死锁风险, 需要使用分布式锁
        int loopCount = 0;
        if(lock.tryLock()){
            try{
                List<RolePermission> rolePermissions = routerMapper.selectBatchIds(routerIds)
                        .stream()
                        .map(item -> {
                            if(item.getPermissionId() == null){
                                return null;
                            }
                            return new RolePermission(null, id, item.getPermissionId());
                        })
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList());
                rolePermissionMapper.delete(new QueryWrapper<RolePermission>().eq("role_id", id));
                try {
                    rolePermissionMapper.insertBatch(rolePermissions);
                } catch (DuplicateKeyException e){
                    throw new BusinessException(WrapperCode.ROLE_MENU_FAIL);
                }
            }finally {
                lock.unlock();
            }
        }else{
            try {
                Thread.sleep(300L);
            } catch (InterruptedException e) {
                throw new BusinessException(WrapperCode.ROLE_MENU_FAIL);
            }
            loopCount++;
            if(loopCount < 3){
                setMenu(id, routerIds);
            }
        }
    }

    @Override
    @Transactional(readOnly = true,rollbackFor = RuntimeException.class)
    public List<Role> findDefaultRole() {
        return roleMapper.selectList(new QueryWrapper<Role>().eq("is_default", 1));
    }
}
