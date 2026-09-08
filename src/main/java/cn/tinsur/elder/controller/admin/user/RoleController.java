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
package cn.tinsur.elder.controller.admin.user;


import cn.tinsur.elder.pojo.entity.Role;
import cn.tinsur.elder.pojo.query.RoleQuery;
import cn.tinsur.elder.service.IRoleService;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-27
 */
@RestController
@RequestMapping("/admin/roles")
public class RoleController {
    @Autowired
    private IRoleService roleService;

    /**
     * 获取全部角色列表List
     * @return
     */
    @GetMapping("/list")
    public Result<List<Role>> list() {
        List<Role> list = roleService.list();
        return Result.ok(list);
    }

    /**
     * 分页查询角色列表
     * GET /roles?page=1&limit=10&name=xxx&code=xxx
     */
    @GetMapping
    public Result<IPage<Role>> pageList(RoleQuery roleQuery) {
        IPage<Role> page = roleService.list(roleQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询角色
     * GET /roles/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(roleService.getById(id));
    }

    /**
     * 新增角色
     * POST /roles
     */
    @PostMapping
    public Result add(@RequestBody Role role) {
        if(isExists(role.getName())) {
            return Result.error("已有同名称的角色存在，请修改后重试");
        }
        roleService.save(role);
        return Result.ok("新增成功");
    }

    /**
     * 修改角色
     * PUT /roles/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        roleService.updateById(role);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除角色（逻辑删除）
     * DELETE /roles/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除角色
     * DELETE /roles
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        roleService.removeByIds(java.util.Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 判断角色是否存在
     */
    @GetMapping("/isExists")
    public Boolean isExists(@RequestParam String name) {
        Role role = roleService.getOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Role>().eq("name", name));
        return role != null;
    }

    /**
     * 根据ID查该权限的菜单树
     */
    @GetMapping("/selectPermissionById/{id}")
    public Result<List<Long>> selectPermissionById(@PathVariable Long id){
        return Result.ok(roleService.selectPermissionById(id));
    }

    /**
     * 修改更新角色的权限列表
     */
    @PutMapping("/updatePermission/{id}")
    public Result updatePermission (@PathVariable Long id, @RequestBody Long[] permissions) {
        return roleService.updatePermission(id, permissions);
    }
}
