/*
 *
 *  * ============================================================
 *  *
 *  *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *  *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *  *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *  *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *  *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *  *      ╚═╝   ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝
 *  *
 *  *  项目名称 : 智慧社区养老系统
 *  *  源码作者 : Tinsur (tinsur.cn)
 *  *  作者主页 : https://www.tinsur.cn
 *  *  联系方式 : me@tinsur.cn
 *  *  开源协议 : GPL 3.0
 *  *
 *  * ============================================================
 *
 */

/*
 * ============================================================
 *
 *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *      ╚═╝   ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝
 *
 *  项目名称 : 智慧社区养老系统
 *  源码作者 : Tinsur (tinsur.cn)
 *  作者主页 : https://www.tinsur.cn
 *  联系方式 : me@tinsur.cn
 *  开源协议 : GPL 3.0
 *
 * ============================================================
 */
package cn.tinsur.elder.service.impl.user;

import cn.tinsur.elder.mapper.RoleMapper;
import cn.tinsur.elder.mapper.RolePermissionMapper;
import cn.tinsur.elder.pojo.entity.Role;
import cn.tinsur.elder.pojo.entity.RolePermission;
import cn.tinsur.elder.pojo.query.RoleQuery;
import cn.tinsur.elder.service.IRoleService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-27
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Override
    public IPage<Role> list(RoleQuery roleQuery) {
        IPage<Role> page = new Page<>(roleQuery.getPage(), roleQuery.getLimit());
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!ObjectUtils.isEmpty(roleQuery.getName()), Role::getName, roleQuery.getName())
                .like(!ObjectUtils.isEmpty(roleQuery.getCode()), Role::getCode, roleQuery.getCode())
                .between(!ObjectUtils.isEmpty(roleQuery.getBeginCreateTime())
                        && !ObjectUtils.isEmpty(roleQuery.getEndCreateTime()),
                        Role::getCreateTime, roleQuery.getBeginCreateTime(),
                        roleQuery.getEndCreateTime())
                .orderByDesc(Role::getCreateTime);
        return roleMapper.selectPage(page, lambdaQueryWrapper);
    }

    @Override
    public List<Long> selectPermissionById(Long id) {
        LambdaQueryWrapper<RolePermission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RolePermission::getRoleId, id);
        List<RolePermission> rolePermissionList = rolePermissionMapper.selectList(lambdaQueryWrapper);
        List<Long> list = rolePermissionList.stream().map(RolePermission::getPermissionId).toList();
        return list;
    }

    /**
     * 根据角色id删除这个角色的所有权限
     * @param id
     */
    private void deletePermissionById(Long id) {
        LambdaQueryWrapper<RolePermission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RolePermission::getRoleId, id);
        rolePermissionMapper.delete(lambdaQueryWrapper);
    }

    /**
     * 根据角色id添加权限，传入的第二个参数应该是权限ID组成的Long数组
     * @param id
     * @param permissionId
     */
    private void addPermissionById(Long id, Long[] permissionId) {
        for (Long permission : permissionId) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(id);
            rolePermission.setPermissionId(permission);
            rolePermissionMapper.insert(rolePermission);
        }
    }

    /**
     * 根据id更新权限，传入的第二个参数应该是权限ID组成的Long数组
     * 这个方法的实现方法是，先根据id删除role-permission中间表中有关这个角色的所有数据，再根据id和permissionId数组插入新的数据
     * @param id
     * @param permissionIds
     * @return
     */
    @Override
    public Result updatePermission(Long id, Long[] permissionIds) {
        deletePermissionById(id);
        addPermissionById(id, permissionIds);
        return Result.ok("更新成功");
    }

}