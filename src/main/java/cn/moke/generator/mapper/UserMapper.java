package cn.moke.generator.mapper;

import cn.moke.generator.entity.po.User;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * @author moke
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询
     * @param username 用户名
     * @return 用户
     */
    User selectByUsername(String username);

    /**
     * 列表查询（级联角色）
     * @param page 分页
//     * @param username 用户名（条件）
//     * @param username 昵称（条件）
     * @return 用户集合
     */
//    List<User> selectCascadeList(Page<User> page, @Param("username") String username, @Param("nickname")String nickname);
    List<User> selectCascadeList(Page<User> page, @Param(Constants.WRAPPER) Wrapper wrapper);

    /**
     * 明细（级联角色）
     * @param id id
     * @return 用户
     */
    User selectCascadeById(Serializable id);
}
