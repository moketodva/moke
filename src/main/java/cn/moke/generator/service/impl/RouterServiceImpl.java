package cn.moke.generator.service.impl;

import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.exception.BusinessException;
import cn.moke.generator.service.RouterService;
import cn.moke.generator.utils.StringUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.moke.generator.entity.po.Router;
import cn.moke.generator.mapper.RouterMapper;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author moke
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class RouterServiceImpl implements RouterService{

    @Resource
    private RouterMapper routerMapper;
    
    @Override
    public void add(Router router) {
        // 通用数据
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime now = LocalDateTime.now();

        // 准备数据
        router.setId(null);
        router.setCreateTime(now);
        router.setCreateUser(username);
        router.setModifyTime(now);
        router.setModifyUser(username);

        // 添加前端路由
        try {
            routerMapper.insert(router);
        } catch (DuplicateKeyException e){
            throw new BusinessException(WrapperCode.ROUTER_ADD_REPEAT_FAIL);
        }
    }

    @Override
    public void deleteBatch(Collection<String> ids) {
        // 批量删除前端路由
        int affectNum = routerMapper.deleteBatchIds(ids);
        if(affectNum != ids.size()){
            throw new BusinessException(WrapperCode.ROUTER_DELETE_FAIL);
        }
    }

    @Override
    public void alter(String id, Router router) {
        // 通用数据
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 准备数据
        router.setId(id);
        router.setCreateTime(null);
        router.setCreateUser(null);
        router.setModifyTime(LocalDateTime.now());
        router.setModifyUser(username);

        // 修改前端路由
        int affectNum = routerMapper.updateById(router);
        if(affectNum != 1){
            throw new BusinessException(WrapperCode.ROUTER_ALTER_FAIL);
        }
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public Router detail(String id) {
        return routerMapper.selectById(id);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public Page<Router> list(Integer pageNum, Integer pageSize, String sorter, Router router) {
        Page<Router> page = new Page<>(pageNum, pageSize);
        return (Page<Router>) routerMapper.selectPage(page, new QueryWrapper<Router>().last("order by " + sorter));
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public List<Router> listAllRouteTree() {
        List<Router> routers = routerMapper.selectAllRouter();
        Map<String, Router> map = new LinkedHashMap<>();
        routers.stream().forEachOrdered(router -> map.put(router.getId(),router));
        return recursionAppendInner(map, null);
    }

    @Override
    public List<Router> listAllRouteTreeChecked(String roleId) {
        List<Router> routers = routerMapper.selectAllByRoleId(roleId);
        Map<String, Router> map = new LinkedHashMap<>();
        routers.stream().forEachOrdered(router -> map.put(router.getId(),router));
        return recursionAppendInner(map, null);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public List<Router> listUserRouteTree() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Router> routers = routerMapper.selectByUsername(name);
        Map<String, Router> map = new LinkedHashMap<>();
        routers.stream().forEachOrdered(router -> map.put(router.getId(),router));
        return recursionAppendInner(map, null);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = RuntimeException.class)
    public List<Router> listAllTree() {
        List<Router> routers = routerMapper.selectAllRouterAndOperate();
        Map<String, Router> map = new LinkedHashMap<>();
        routers.stream().forEachOrdered(router -> map.put(router.getId(),router));
        return recursionAppendInner(map, null);
    }

    private List<Router> recursionAppendInner(Map<String, Router> routerMap, String parentId){
        return routerMap.values().stream().map(router -> {
            if(router == null){
                return null;
            }
            if (router.getParentId() == null && parentId == null
                    || (router.getParentId() != null && router.getParentId().equals(parentId))) {
                String parentPath = routerMap.get(parentId) == null
                        ? "" : routerMap.get(parentId).getAbsolutePath() == null
                        ? "" : routerMap.get(parentId).getAbsolutePath();
                String absolutePath = parentPath + (router.getRelativePath() == null ? "" : "/" + router.getRelativePath());
                router.setAbsolutePath(absolutePath);

                List<Router> children = recursionAppendInner(routerMap, router.getId());

                if(children.size() == 0 && router.isChecked() == true){
                    router.setChecked(true);
                }else{
                    router.setChecked(false);
                }
                router.setChildren(children);
                return router;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
