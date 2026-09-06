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
package cn.tinsur.elder.service.impl;

import cn.tinsur.elder.exception.ServiceException;
import cn.tinsur.elder.listener.UserExcelListener;
import cn.tinsur.elder.mapper.PermissionMapper;
import cn.tinsur.elder.mapper.RoleMapper;
import cn.tinsur.elder.mapper.UserRoleMapper;
import cn.tinsur.elder.pojo.entity.Permission;
import cn.tinsur.elder.pojo.entity.Role;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.mapper.UserMapper;
import cn.tinsur.elder.pojo.query.UserQuery;
import cn.tinsur.elder.pojo.vo.PermissionVO;
import cn.tinsur.elder.pojo.vo.UserExcelVO;
import cn.tinsur.elder.pojo.entity.UserRole;
import cn.tinsur.elder.pojo.vo.UserVO;
import cn.tinsur.elder.service.IPermissionService;
import cn.tinsur.elder.service.IUserService;
import cn.tinsur.elder.util.ExcelUtil;
import cn.tinsur.elder.util.Result;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private IPermissionService permissionService;


    /**
     * 获取用户列表（分页），返回 UserVO，并在每个UserVO中填充当前用户的角色列表roles
     * @param userQuery
     * @return
     */
    @Override
    public IPage<UserVO> list(UserQuery userQuery) {
        // 1.先查用户分页，这里使用的是先前的不带角色查询功能的代码
        IPage<User> page = new Page<>(userQuery.getPage(), userQuery.getLimit());
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(!ObjectUtils.isEmpty(userQuery.getName()),User::getName,userQuery.getName())
                .like(!ObjectUtils.isEmpty(userQuery.getEmail()), User::getEmail, userQuery.getEmail())
                .between(!ObjectUtils.isEmpty(userQuery.getBeginCreateTime())
                        && !ObjectUtils.isEmpty(userQuery.getEndCreateTime()),
                        User::getCreateTime, userQuery.getBeginCreateTime(),
                        userQuery.getEndCreateTime())
                .orderByDesc(User::getCreateTime);
        IPage<User> userPage = userMapper.selectPage(page, lambdaQueryWrapper);

        // 2.把查到的当前页的User转成UserVO
        List<UserVO> userVOList = userPage.getRecords().stream()
                .map(user -> {
                    UserVO vo = new UserVO();
                    BeanUtils.copyProperties(user, vo);
                    return vo;
                })
                .toList();

        // 3.给每个VO填上角色，调用下面的fillRoles方法
        fillRoles(userVOList);

        // 4.返回UserVO类型的分页
        IPage<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        voPage.setRecords(userVOList);
        return voPage;
    }

    /**
     * 批量给UserVO填角色
     */
    private void fillRoles(List<UserVO> userVOList) {
        if (userVOList.isEmpty()) return;

        // 1.当前页所有用户id，使用stream流先把所有用户id取出来
        List<Long> userIds = userVOList.stream().map(UserVO::getId).toList();

        // 2.一次查出这些用户的user-role中间表所有关联记录
        //      .in实现只查想查的user_id所对应的角色role
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().in(UserRole::getUserId, userIds));
        if (userRoles.isEmpty()) return;

        // 3.一次查出用到的所有角色，把我们得到的角色id转换成一个个的角色Role对象
        //      可能有多个重复的角色id，只需要一个id获取一次就可以了，使用.distinct去重
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).distinct().toList();
        //      把查询结果List<Role>组装成一个Map<Long, Role>，key是角色id，value是角色对象本身。
        Map<Long, Role> roleMap = roleMapper.selectBatchIds(roleIds).stream()
                .collect(Collectors.toMap(role -> role.getId(), r -> r));

        // 4.按userId分组
        Map<Long, List<UserRole>> groupByUserRole = userRoles.stream()
                .collect(Collectors.groupingBy(UserRole::getUserId));
        //      再把每个用户名下的 role_id 逐条翻译成 Role 对象，收成一个列表
        //      角色被逻辑删除后绑定关系还在，map里取不到会得到null，过滤掉避免前端遍历时崩溃
        Map<Long, List<Role>> groupByUser = new HashMap<>();
        groupByUserRole.forEach((userId, urList) -> {
            List<Role> roles = urList.stream()
                    .map(ur -> roleMap.get(ur.getRoleId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            groupByUser.put(userId, roles);
        });

        // 5.回填：没角色的用户传入空列表，保证前端 row.roles 不为 null
        userVOList.forEach(vo -> vo.setRoles(
                groupByUser.getOrDefault(vo.getId(), Collections.emptyList())));
    }

    /**
     * 导出用户信息
     * @param response
     */
    @Override
    public void exportExcel(HttpServletResponse response) {
        List<User> list = userMapper.selectList(null); //写null则查出所有用户
        List<UserExcelVO> userExcelVOList = list.stream().map(user -> {
            UserExcelVO userExcelVO = new UserExcelVO();
            BeanUtils.copyProperties(user, userExcelVO);
            return userExcelVO;
        }).toList();
        ExcelUtil.exportExcel(response, userExcelVOList, UserExcelVO.class, "用户信息表");
    }

    /**
     * 导入用户信息
     * @param file
     */
    @Override
    public void importExcel(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), UserExcelVO.class, new UserExcelListener(userMapper)).sheet().doRead();
        } catch (ExcelDataConvertException e) {
            //单元格类型解析失败，属于格式问题
            throw new ServiceException("导入失败：Excel格式有误，请使用导出的模板文件");
        } catch (DataIntegrityViolationException e) {
            //数据写入数据库时发生约束冲突，属于数据冲突
            throw new ServiceException("导入失败：数据冲突，存在重复或不符合字段要求的数据");
        } catch (Exception e) {
            throw new ServiceException("导入失败，请检查文件内容后重试");
        }

    }

    /**
     * 判断用户是否存在
     */
    public Boolean isExists(@RequestParam String name) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("name", name));
        return user != null;
    }

    @Override
    public Result add(User user) {
        if(isExists(user.getName())) {
            throw new ServiceException("用户名已存在，换一个用户名试试吧");
        }
        userMapper.insert(user);
        return Result.ok("新增成功");
    }

    @Override
    public Result<List<Role>> getRolesById(Long id) {
        LambdaQueryWrapper<UserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserRole :: getUserId, id);
        List<Long> list =
                userRoleMapper.selectList(lambdaQueryWrapper)
                        .stream()
                        .map(UserRole :: getRoleId)
                        .toList();
        return Result.ok(list);
    }

    /**
     * 根据用户id删除这个用户的所有角色
     * @param id
     */
    @Override
    public void deleteAllRolesById(Long id) {
        LambdaQueryWrapper<UserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserRole :: getUserId, id);
        userRoleMapper.delete(lambdaQueryWrapper);
    }

    /**
     * 根据id添加角色，传入的第二个参数应该是角色ID组成的Long数组
     * @param id
     * @param roleId
     */
    @Override
    public void addRoleById(Long id, Long[] roleId) {
        for (Long role : roleId) {
            UserRole userRole = new UserRole();
            userRole.setUserId(id);
            userRole.setRoleId(role);
            userRoleMapper.insert(userRole);
        }
    }

    /**
     * 根据id更新角色，传入的第二个参数应该是角色ID组成的Long数组
     * 这个方法的实现方法是，先根据id删除user-role中间表中有关这个用户的所有数据，再根据id和roleId数组插入新的数据
     * @param id
     * @param roles
     * @return
     */
    @Override
    public Result updateRoles(Long id, Long[] roles) {
        deleteAllRolesById(id);
        addRoleById(id, roles);
        return Result.ok("更新成功");
    }

    @Override
    public Map<String, Object> selectPermissionByUserId(Long id) {
        //根据用户id查询这个用户所有权限
        List<Permission> permissionList = userMapper.selectPermissionByUserId(id);
        //把List<Permission> 转换成List<PermissionVO>
        List<String> btnList = new ArrayList<>();
        List<PermissionVO> permissionVOList = new ArrayList<>();
        permissionList.forEach(permission -> {
            if (permission.getType() == 2) {//按钮权限
                btnList.add(permission.getPermissionValue());
            } else {//目录和菜单权限
                PermissionVO permissionVO = new PermissionVO();
                BeanUtils.copyProperties(permission, permissionVO);
                permissionVOList.add(permissionVO);
            }
        });
        return Map.of("routerList", permissionService.buildTree(permissionVOList), "btnList", btnList);
    }

    /**
     * 按角色搜索用户：先查出该角色下所有 user-role 中间表记录的 userId，
     * 再根据姓名关键字（可为空）在这些用户中做模糊查询
     */
    @Override
    public List<User> searchByRole(Long roleId, String name) {
        // 1.查该角色下所有 user_role 记录，取出 userId 列表
        List<Long> userIds = userRoleMapper.selectList(
                        new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, roleId))
                .stream()
                .map(UserRole::getUserId)
                .toList();
        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }
        // 2.按姓名（可选）模糊查询这些用户，未输入姓名则返回全部
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .in(User::getId, userIds)
                .like(!ObjectUtils.isEmpty(name), User::getRealName, name);
        return userMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 按姓名搜索所有用户，不做角色限定（供护理计划等"选护理人员"远程下拉框使用）
     * 不判断角色，是为了避免删掉"护工"这个角色后导致绑定人员的功能失效
     * @param name 可选的姓名关键字，为空则返回全部用户
     * @return 用户的List列表
     */
    @Override
    public List<User> searchByName(String name) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //按真实姓名（可输入部分或全部）模糊查询所有用户
        lambdaQueryWrapper.like(!ObjectUtils.isEmpty(name), User::getRealName, name);
        return userMapper.selectList(lambdaQueryWrapper);
    }
}
