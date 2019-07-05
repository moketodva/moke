package cn.moke.generator.service.impl;

import cn.moke.generator.entity.dto.UserDto;
import cn.moke.generator.entity.po.Role;
import cn.moke.generator.entity.po.UserRole;
import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.exception.BusinessException;
import cn.moke.generator.mapper.RoleMapper;
import cn.moke.generator.mapper.UserRoleMapper;
import cn.moke.generator.utils.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import cn.moke.generator.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.moke.generator.entity.po.User;
import cn.moke.generator.mapper.UserMapper;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author moke
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private PasswordEncoder passwordEncoder;
    private Lock modifyLock = new ReentrantLock();
    private Lock configureLock = new ReentrantLock();

    @Override
    public void add(UserDto userDto) {
        // 通用数据
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime now = LocalDateTime.now();

        // 准备数据
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        user.setCreateTime(now);
        user.setCreateUser(username);
        user.setModifyTime(now);
        user.setModifyUser(username);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 添加用户
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e){
            throw new BusinessException(WrapperCode.USER_ADD_REPEAT_FAIL);
        }

        List<String> roleIds = userDto.getRoleIds();
        if(roleIds != null && roleIds.size() != 0){
            // 查看角色是否存在
            List<Role> roles = roleMapper.selectBatchIdsForUpdate(roleIds);
            if(roles.size() != roleIds.size()){
                throw new BusinessException(WrapperCode.USER_NON_EXIST_ROLE_FAIL);
            }

            // 插入用户-角色关系
            List<UserRole> userRoles = roleIds.stream()
                    .map(roleId -> new UserRole(null, user.getId(), roleId))
                    .collect(Collectors.toList());
            try {
                userRoleMapper.insertBatch(userRoles);
            } catch (DuplicateKeyException e){
                throw new BusinessException(WrapperCode.USER_ADD_REPEAT_FAIL);
            }
        }
    }

    @Override
    public void deleteBatch(Collection<String> ids) {
        // 批量删除用户
        int affectNum = userMapper.deleteBatchIds(ids);
        if(affectNum != ids.size()){
            throw new BusinessException(WrapperCode.USER_DELETE_FAIL);
        }

        // 删除用户-角色关系
        userRoleMapper.delete(new QueryWrapper<UserRole>().in("user_id", ids));

        // 删除令牌
        List<User> users = userMapper.selectBatchIds(ids);
        List<String> usernames = users.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        if(usernames != null && usernames.size() > 0){
            redisTemplate.delete(usernames);
        }
    }

    @Override
    public void alter(String id, UserDto userDto) {
        // 通用数据
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime now = LocalDateTime.now();

        // 准备数据
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        user.setId(id);
        user.setModifyTime(now);
        user.setModifyUser(username);

        // 修改用户
        int affectNum = userMapper.updateById(user);
        if(affectNum != 1){
            throw new BusinessException(WrapperCode.USER_ALTER_FAIL);
        }

        List<String> roleIds = userDto.getRoleIds();
        if(roleIds != null && roleIds.size() != 0){
            // 查看角色是否存在
            List<Role> roles = roleMapper.selectBatchIdsForUpdate(roleIds);
            if(roles.size() != roleIds.size()){
                throw new BusinessException(WrapperCode.USER_NON_EXIST_ROLE_FAIL);
            }
            // 由于该操作流量不大, 直接加上使用简单粗暴线程锁使用方式避免死锁
            // 注：分布式环境仍有死锁风险, 需要使用分布式锁
            int loopCount = 0;
            if(modifyLock.tryLock()){
                try{
                    // 删除用户-角色关系
                    userRoleMapper.delete(new QueryWrapper<UserRole>().eq("user_id", id));
                    // 插入用户-角色关系
                    List<UserRole> userRoles = roleIds.stream()
                            .map(roleId -> new UserRole(null, user.getId(), roleId))
                            .collect(Collectors.toList());
                    try {
                        userRoleMapper.insertBatch(userRoles);
                    } catch (DuplicateKeyException e){
                        throw new BusinessException(WrapperCode.USER_ADD_REPEAT_FAIL);
                    }
                }finally {
                    modifyLock.unlock();
                }
            }else{
                try {
                    Thread.sleep(300L);
                } catch (InterruptedException e) {
                    throw new BusinessException(WrapperCode.USER_ALTER_FAIL);
                }
                loopCount++;
                if(loopCount < 3){
                    alter(id, userDto);
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public User detail(String id) {
        return userMapper.selectCascadeById(id);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public Page<User> list(Integer pageNum, Integer pageSize, String sort, Boolean isAsc, String username, String nickname) {
        Page<User> page = new Page<>(pageNum, pageSize);
        List<User> users = userMapper.selectCascadeList(page, Wrappers.<User>query()
                .like(!StringUtil.isEmpty(username), "username", username)
                .like(!StringUtil.isEmpty(nickname), "nickname", nickname)
                .orderBy(StringUtil.isEmpty(sort), false, "id")
                .orderBy(!StringUtil.isEmpty(sort), isAsc, sort));

        Integer total = userMapper.selectCount(new QueryWrapper<User>()
                .like(!StringUtil.isEmpty(username),"username", username)
                .like(!StringUtil.isEmpty(nickname),"nickname", nickname));
        page.setRecords(users);
        page.setTotal(total);
        return page;
    }

    @Override
    public void enable(String userId) {
        User user = new User();
        user.setId(userId);
        user.setIsLock(false);
        int affectNum = userMapper.updateById(user);
        if(affectNum != 1){
            throw new BusinessException(WrapperCode.USER_ENABLE_FAIL);
        }
    }

    @Override
    public void disable(String userId) {
        User user = new User();
        user.setId(userId);
        user.setIsLock(true);
        int affectNum = userMapper.updateById(user);
        if(affectNum != 1){
            throw new BusinessException(WrapperCode.USER_DISABLE_FAIL);
        }
        user = userMapper.selectById(userId);
        if(user != null){
            redisTemplate.delete(user.getUsername());
        }
    }

    @Override
    @CacheEvict("RolePermission")
    public void configureRole(String userId, Collection<String> roleIds) {
        // 准备数据
        List<UserRole> userRoles = roleIds.stream()
                .map(roleId -> new UserRole(null, userId, roleId))
                .collect(Collectors.toList());

        // 由于该操作流量不大, 直接加上使用简单粗暴线程锁使用方式避免死锁
        // 注：分布式环境仍有死锁风险, 需要使用分布式锁
        int loopCount = 0;
        if(configureLock.tryLock()){
            try{
                userRoleMapper.delete(new QueryWrapper<UserRole>().eq("user_id", userId));
                try{
                    userRoleMapper.insertBatch(userRoles);
                } catch (DuplicateKeyException e){
                    throw new BusinessException(WrapperCode.USER_ADD_REPEAT_FAIL);
                }
            }finally {
                configureLock.unlock();
            }
        }else{
            try {
                Thread.sleep(300L);
            } catch (InterruptedException e) {
                throw new BusinessException(WrapperCode.USER_ALTER_FAIL);
            }
            loopCount++;
            if(loopCount < 3){
                configureRole(userId, roleIds);
            }
        }
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
}
