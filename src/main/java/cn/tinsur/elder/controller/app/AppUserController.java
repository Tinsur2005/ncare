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
package cn.tinsur.elder.controller.app;


import cn.tinsur.elder.pojo.dto.AppLoginDTO;
import cn.tinsur.elder.pojo.entity.Elder;
import cn.tinsur.elder.pojo.entity.Family;
import cn.tinsur.elder.pojo.vo.ElderVO;
import cn.tinsur.elder.service.IBedService;
import cn.tinsur.elder.service.IElderService;
import cn.tinsur.elder.service.IFamilyService;
import cn.tinsur.elder.util.AppAuthHelper;
import cn.tinsur.elder.util.JwtUtil;
import cn.tinsur.elder.util.PasswordUtil;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前台手机端登录/个人信息 前端控制器
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-02
 */
@RestController
@RequestMapping("/app/users")
public class AppUserController {

    @Autowired
    private IElderService elderService;

    @Autowired
    private IFamilyService familyService;

    @Autowired
    private IBedService bedService;

    @Autowired
    private AppAuthHelper appAuthHelper;

    /**
     * 登录（前台手机端统一入口）
     * userType=elder 老人登录，userType=family 家属登录
     * @param appLoginDTO
     * @return Result<String>
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody AppLoginDTO appLoginDTO) {
        //家属登录
        if ("family".equals(appLoginDTO.getUserType())) {
            //根据用户名查找这个家属
            Family dbFamily = familyService.getOne(new QueryWrapper<Family>().eq("name", appLoginDTO.getName()));
            if (dbFamily == null) {
                return Result.error("用户名不存在");
            }
            if (!PasswordUtil.matches(appLoginDTO.getPassword(), dbFamily.getPassword())) {
                return Result.error("密码错误");
            }
            //账号密码正确时，判断用户状态
            if (dbFamily.getStatus() == 0) {
                return Result.error("该用户已被禁用，无法登录");
            }
            //登录校验成功，生成Token（userType用于前后台token隔离）
            Map<String, Object> map = new HashMap<>();
            map.put("id", dbFamily.getId());
            map.put("name", dbFamily.getName());
            map.put("userType", "family");
            String token = JwtUtil.creatToken(map);
            return Result.ok("登录成功", token);
        }
        //老人登录
        Elder dbElder = elderService.getOne(new QueryWrapper<Elder>().eq("name", appLoginDTO.getName()));
        if (dbElder == null) {
            return Result.error("用户名不存在");
        }
        if (!PasswordUtil.matches(appLoginDTO.getPassword(), dbElder.getPassword())) {
            return Result.error("密码错误");
        }
        //账号密码正确时，判断用户状态
        if (dbElder.getStatus() == 0) {
            return Result.error("该用户已被禁用，无法登录");
        }
        //登录校验成功，生成Token（userType用于前后台token隔离）
        Map<String, Object> map = new HashMap<>();
        map.put("id", dbElder.getId());
        map.put("name", dbElder.getName());
        map.put("userType", "elder");
        String token = JwtUtil.creatToken(map);
        return Result.ok("登录成功", token);
    }

    /**
     * 根据Token查询前台登录用户信息（老人或家属），家属同时返回其绑定的老人列表
     * @param token
     * @return
     */
    @GetMapping("/userInfo")
    public Result userInfo(@RequestHeader(name = "Authorization") String token) {
        Map<String, Object> map = JwtUtil.parseToken(token);
        Long id = ((Number) map.get("id")).longValue();
        String userType = (String) map.get("userType");

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("userType", userType);
        if ("family".equals(userType)) {
            //家属：返回家属信息（不暴露密码）和绑定的老人列表
            Family family = familyService.getById(id);
            family.setPassword(null);
            resultMap.put("user", family);
            //绑定的老人转换为ElderVO，带上标签列表（不暴露密码）
            List<ElderVO> elders = familyService.getEldersById(id).getData().stream()
                    .map(elder -> {
                        ElderVO elderVO = elderService.getVOById(elder.getId());
                        elderVO.setPassword(null);
                        return elderVO;
                    })
                    .toList();
            resultMap.put("elders", elders);
        } else {
            //老人：返回自己的信息，密码置为NULL，elders为空列表
            ElderVO elder = elderService.getVOById(id);
            elder.setPassword(null);
            resultMap.put("user", elder);
            resultMap.put("elders", new ArrayList<>());
        }
        return Result.ok(resultMap);
    }

    /**
     * 查询老人当前入住的床位信息（楼栋名称、楼层号、房间号、床位号），未入住时data为null
     * GET /users/bedInfo?elderId=1
     * @param elderId 老人ID
     * @param token
     * @return
     */
    @GetMapping("/bedInfo")
    public Result bedInfo(@RequestParam Long elderId,
                          @RequestHeader(name = "Authorization") String token) {
        //校验归属：老人只能查自己的，家属只能查绑定老人的
        Result checkResult = appAuthHelper.checkElderPermission(token, elderId);
        if (checkResult.getCode() != Result.OK) {
            return checkResult;
        }
        return Result.ok(bedService.getOccupiedByElderId(elderId));
    }
}
