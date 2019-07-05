package cn.moke.generator.service;

import cn.moke.generator.entity.dto.UserDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.moke.generator.entity.po.User;

import java.util.Collection;

/**
 * @author moke
 */
public interface UserService {

    /**
     * 添加
     * @param userDto 用户
     */
    void add(UserDto userDto);

    /**
     * 批量删除
     * @param ids ids
     */
    void deleteBatch(Collection<String> ids);

    /**
     * 修改
     * @param id id
     * @param userDto 用户
     */
    void alter(String id, UserDto userDto);

    /**
     * 明细
     * @param id id
     * @return 用户
     */
    User detail(String id);

    /**
     * 列表查询
     * @param pageNum 页码
     * @param pageSize 单页行数
     * @param sort 排序字段
     * @param isAsc 是否升序
     * @param username 用户名（条件）
     * @param nickname 昵称（条件）
     * @return Page<T>
     */
    Page<User> list(Integer pageNum, Integer pageSize, String sort, Boolean isAsc,String username, String nickname);

    /**
     * 启用用户
     * @param id 用户id
     */
    void enable(String id);

    /**
     * 禁用用户
     * @param id 用户id
     */
    void disable(String id);

    /**
     * 配置角色
     * @param userId 用户Id
     * @param roleIds 角色Id集合
     */
    void configureRole(String userId, Collection<String> roleIds);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户
     */
    User findByUsername(String username);
}
