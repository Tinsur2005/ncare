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
package cn.tinsur.elder.controller.admin;


import cn.tinsur.elder.pojo.entity.Role;
import cn.tinsur.elder.pojo.dto.UserPasswordDTO;
import cn.tinsur.elder.pojo.dto.EmailCodeDTO;
import cn.tinsur.elder.pojo.dto.EmailPasswordDTO;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.pojo.query.UserQuery;
import cn.tinsur.elder.pojo.vo.UserVO;
import cn.tinsur.elder.service.IEmailCodeService;
import cn.tinsur.elder.service.IUserService;
import cn.tinsur.elder.util.JwtUtil;
import cn.tinsur.elder.util.PasswordUtil;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-08-24
 */
@RestController
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IEmailCodeService emailCodeService;

    /**
     * 登录
     * @param user
     * @return Result<String>
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody User user) {
        //根据用户名查找这个用户
        User dbUser = userService.getOne(new QueryWrapper<User>().eq("name", user.getName()));
        if(dbUser == null) {
            return Result.error("用户名不存在");
        }
        if(!PasswordUtil.matches(user.getPassword(), dbUser.getPassword())) {
            return Result.error("密码错误，请重新输入");
        }
        //账号密码正确时，判断用户状态
        if (dbUser.getStatus() == 0) {
            return Result.error("该用户已被禁用，无法登录");
        }
        //登录校验成功，生成Token
        Map<String, Object> map = new HashMap<>();
        map.put("id",dbUser.getId());
        map.put("name", dbUser.getName());
        String token = JwtUtil.creatToken(map);
        return Result.ok("登录成功",token);
    }

    /**
     * 分页查询用户列表
     * GET /users?page=1&limit=10&name=xxx&phone=xxx
     */
    @GetMapping
    public Result<IPage<UserVO>> list(UserQuery userQuery) {
        IPage<UserVO> page = userService.list(userQuery);
        return Result.ok(page);
    }

    /**
     * 根据ID查询用户
     * GET /users/1
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    /**
     * 新增用户
     * POST /users
     */
    @PostMapping
    public Result add(@RequestBody User user) {
        return userService.add(user);
    }

    /**
     * 修改用户
     * PUT /users/1
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody User user) {
        //密码留空表示不修改，置null让MyBatis-Plus跳过该列；填了则BCrypt加密后再更新
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(null);
        } else {
            user.setPassword(PasswordUtil.hash(user.getPassword()));
        }
        user.setId(id);
        userService.updateById(user);
        return Result.ok("修改成功");
    }

    /**
     * 根据ID删除用户（逻辑删除）
     * DELETE /users/1
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 批量删除用户
     * DELETE /users
     */
    @DeleteMapping
    public Result deleteBatch(@RequestBody Long[] ids) {
        userService.removeByIds(java.util.Arrays.asList(ids));
        return Result.ok("批量删除成功");
    }

    /**
     * 根据Token查询用户信息
     * @param token
     * @return
     */
    @GetMapping("/userInfo")
    public Result userInfo(@RequestHeader(name = "Authorization") String token) {
        Map<String, Object> map = JwtUtil.parseToken(token);
        Integer id = (Integer) map.get("id");
        User user = userService.getById(id);
        user.setPassword(null);

        Map<String, Object> permissionMap = userService.selectPermissionByUserId(user.getId());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("user", user);
        resultMap.put("routerList", permissionMap.get("routerList"));
        resultMap.put("btnList", permissionMap.get("btnList"));
        return Result.ok(resultMap);
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPassword")
    public Result resetPassword(@RequestHeader String Authorization,
                                @RequestBody UserPasswordDTO userPasswordDTO) {
        Map<String, Object> map = JwtUtil.parseToken(Authorization);
        Integer id = (Integer) map.get("id");
        User user = userService.getById(id);
        //库中是BCrypt哈希，旧密码要用matches比对，不能用equals
        if (!PasswordUtil.matches(userPasswordDTO.getOldPassword(), user.getPassword())) {
            return Result.error("原密码错误");
        }
        if (PasswordUtil.matches(userPasswordDTO.getNewPassword(), user.getPassword())) {
            return Result.error("新密码不能与原密码相同");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setPassword(PasswordUtil.hash(userPasswordDTO.getNewPassword()));
        userService.updateById(updateUser);
        return Result.ok("密码重置成功");
    }

    /**
     * 通过邮箱验证码修改密码：验证码发到当前用户绑定的邮箱，校验通过后设置新密码
     * @param token
     * @param emailPasswordDTO
     * @return
     */
    @PutMapping("/updatePasswordByEmail")
    public Result updatePasswordByEmail(@RequestHeader String Authorization,
                                        @RequestBody EmailPasswordDTO emailPasswordDTO) {
        User user = userService.getById(((Number) JwtUtil.parseToken(Authorization).get("id")).longValue());
        if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
            return Result.error("您尚未绑定邮箱，请先绑定邮箱");
        }
        // 1.校验邮箱验证码（校验通过后验证码自动作废）
        Result verifyResult = emailCodeService.verifyCode(user.getEmail(), IEmailCodeService.SCENE_CHANGE_PASSWORD, emailPasswordDTO.getCode());
        if (verifyResult.getCode() != Result.OK) {
            return verifyResult;
        }
        // 2.校验新密码与原密码不同，与原重置密码接口保持一致（库中是哈希，用matches比对）
        if (PasswordUtil.matches(emailPasswordDTO.getNewPassword(), user.getPassword())) {
            return Result.error("新密码不能与原密码相同");
        }
        // 3.更新密码（BCrypt加密后入库）
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setPassword(PasswordUtil.hash(emailPasswordDTO.getNewPassword()));
        userService.updateById(updateUser);
        return Result.ok("密码修改成功，请重新登录");
    }

    /**
     * 绑定邮箱（当前用户尚未绑定邮箱时使用）：验证码发到待绑定的新邮箱，校验通过后完成绑定
     * @param token
     * @param emailCodeDTO
     * @return
     */
    @PutMapping("/bindEmail")
    public Result bindEmail(@RequestHeader(name = "Authorization") String token,
                            @RequestBody EmailCodeDTO emailCodeDTO) {
        Long id = ((Number) JwtUtil.parseToken(token).get("id")).longValue();
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 1.已绑定过邮箱时引导走更换邮箱流程，避免混淆
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            return Result.error("您已绑定邮箱，如需更换请使用更换邮箱功能");
        }
        // 2.校验发到新邮箱的验证码（校验通过后验证码自动作废）
        Result verifyResult = emailCodeService.verifyCode(emailCodeDTO.getEmail(), IEmailCodeService.SCENE_BIND_EMAIL, emailCodeDTO.getCode());
        if (verifyResult.getCode() != Result.OK) {
            return verifyResult;
        }
        // 3.再次校验邮箱未被其他账号占用（发送验证码后到提交前存在时间差，可能被别人抢先绑定）
        Long count = userService.lambdaQuery()
                .eq(User::getEmail, emailCodeDTO.getEmail())
                .ne(User::getId, id)
                .count();
        if (count > 0) {
            return Result.error("该邮箱已被其他账号绑定");
        }
        // 4.完成绑定
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(emailCodeDTO.getEmail());
        userService.updateById(updateUser);
        return Result.ok("邮箱绑定成功");
    }

    /**
     * 更换绑定邮箱：旧邮箱和新邮箱都要验证，旧邮箱的验证码确认是本人在操作，
     * 新邮箱的验证码确认新邮箱真实有效且归本人所有，两个都校验通过后更新为新邮箱
     * @param token
     * @param emailCodeDTO
     * @return
     */
    @PutMapping("/updateEmail")
    public Result updateEmail(@RequestHeader(name = "Authorization") String token,
                              @RequestBody EmailCodeDTO emailCodeDTO) {
        Long id = ((Number) JwtUtil.parseToken(token).get("id")).longValue();
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 1.必须已绑定过邮箱才能更换
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return Result.error("您尚未绑定邮箱，请先绑定邮箱");
        }
        // 2.新邮箱不能和旧邮箱相同
        if (user.getEmail().equals(emailCodeDTO.getEmail())) {
            return Result.error("新邮箱不能与当前邮箱相同");
        }
        // 3.校验发到旧邮箱的验证码（校验通过后验证码自动作废）
        Result verifyResult = emailCodeService.verifyCode(user.getEmail(), IEmailCodeService.SCENE_CHANGE_EMAIL, emailCodeDTO.getCode());
        if (verifyResult.getCode() != Result.OK) {
            return verifyResult;
        }
        // 4.校验发到新邮箱的验证码（校验通过后验证码自动作废）
        Result verifyNewResult = emailCodeService.verifyCode(emailCodeDTO.getEmail(), IEmailCodeService.SCENE_CHANGE_EMAIL_NEW, emailCodeDTO.getNewCode());
        if (verifyNewResult.getCode() != Result.OK) {
            return verifyNewResult;
        }
        // 5.再次校验新邮箱未被其他账号占用
        Long count = userService.lambdaQuery()
                .eq(User::getEmail, emailCodeDTO.getEmail())
                .ne(User::getId, id)
                .count();
        if (count > 0) {
            return Result.error("该邮箱已被其他账号绑定");
        }
        // 6.更新为新邮箱
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(emailCodeDTO.getEmail());
        userService.updateById(updateUser);
        return Result.ok("邮箱更换成功");
    }

    //导出Excel
    @GetMapping("/exportExcel")
    public void exportExcel (HttpServletResponse response) {
        userService.exportExcel(response);
    }

    //导入Excel
    @PostMapping("/importExcel")
    public Result importExcel (MultipartFile file) {
        userService.importExcel(file);
        return Result.ok("导入成功");
    }

    /**
     * 获取指定用户所有的角色
     * result.data中存放用户所有的角色组成的List列表
     */
    @GetMapping("/getRolesById/{id}")
    public Result<List<Role>> getRolesById(@PathVariable Long id) {
        return userService.getRolesById(id);
    }


    /**
     * 修改更新用户的Roles角色列表
     */
    @PutMapping("/updateRoles/{id}")
    public Result updateRoles (@PathVariable Long id, @RequestBody Long[] roles) {
        return userService.updateRoles(id, roles);
    }

    /**
     * 按角色搜索用户（供护理计划等"选护理人员"远程下拉框使用）
     * GET /users/searchByRole?roleId=3&name=xxx
     */
    @GetMapping("/searchByRole")
    public Result<List<User>> searchByRole(@RequestParam Long roleId,
                                           @RequestParam(required = false) String name) {
        return Result.ok(userService.searchByRole(roleId, name));
    }

    /**
     * 按姓名搜索所有用户（不做角色限定，供护理计划等"选护理人员"远程下拉框使用）
     * GET /users/searchByName?name=xxx
     */
    @GetMapping("/searchByName")
    public Result<List<User>> searchByName(@RequestParam(required = false) String name) {
        return Result.ok(userService.searchByName(name));
    }
}
