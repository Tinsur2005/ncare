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
package cn.tinsur.elder.controller.admin.tool;

import cn.tinsur.elder.pojo.dto.EmailCodeDTO;
import cn.tinsur.elder.pojo.entity.User;
import cn.tinsur.elder.service.IEmailCodeService;
import cn.tinsur.elder.service.IUserService;
import cn.tinsur.elder.util.JwtUtil;
import cn.tinsur.elder.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 邮箱验证码 前端控制器：
 * 负责给当前登录的后台用户发送各类场景的邮箱验证码，
 * 验证码校验逻辑在UserController的绑定邮箱、邮箱改密、更换邮箱接口中完成
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@RestController
@RequestMapping("/admin/email-codes")
public class EmailCodeController {

    @Autowired
    private IEmailCodeService emailCodeService;

    @Autowired
    private IUserService userService;

    /**
     * 给当前登录用户发送邮箱验证码
     * scene=BIND_EMAIL：验证码发到待绑定的新邮箱（此时email必填，且不能被其他账号绑定）
     * scene=CHANGE_EMAIL_NEW：更换邮箱时验证新邮箱，验证码发到待更换的新邮箱（此时email必填，且不能被其他账号绑定）
     * scene=CHANGE_PASSWORD / CHANGE_EMAIL：验证码发到当前用户已绑定的邮箱
     * @param token
     * @param emailCodeDTO
     * @return
     */
    @PostMapping("/send")
    public Result send(@RequestHeader(name = "Authorization") String token,
                       @RequestBody EmailCodeDTO emailCodeDTO) {
        // 1.场景不能为空
        String scene = emailCodeDTO.getScene();
        if (ObjectUtils.isEmpty(scene)) {
            return Result.error("使用场景不能为空");
        }
        // 2.绑定邮箱、更换邮箱验证新邮箱场景：验证码发给指定的新邮箱，校验格式和占用情况
        if (IEmailCodeService.SCENE_BIND_EMAIL.equals(scene) || IEmailCodeService.SCENE_CHANGE_EMAIL_NEW.equals(scene)) {
            String email = emailCodeDTO.getEmail();
            if (ObjectUtils.isEmpty(email)) {
                return Result.error("请填写邮箱地址");
            }
            // 该邮箱已被其他账号绑定时提前拦截，避免白白发一封邮件
            Long count = userService.lambdaQuery()
                    .eq(User::getEmail, email)
                    .ne(User::getId, ((Number) JwtUtil.parseToken(token).get("id")).longValue())
                    .count();
            if (count > 0) {
                return Result.error("该邮箱已被其他账号绑定");
            }
            return emailCodeService.sendCode(email, scene);
        }
        // 3.改密、更换邮箱验证旧邮箱场景：验证码发给当前用户已绑定的邮箱
        User user = userService.getById(((Number) JwtUtil.parseToken(token).get("id")).longValue());
        if (user == null || ObjectUtils.isEmpty(user.getEmail())) {
            return Result.error("您尚未绑定邮箱，请先绑定邮箱");
        }
        return emailCodeService.sendCode(user.getEmail(), scene);
    }

    /**
     * 校验邮箱验证码但不作废（供分步验证流程使用）
     * 目前只用于更换邮箱的第一步：先校验发到当前绑定旧邮箱的验证码，校验通过后再进入第二步验证新邮箱，
     * 全部步骤完成时由UserController的updateEmail接口统一校验并作废新旧两个验证码
     * @param token
     * @param emailCodeDTO
     * @return
     */
    @PostMapping("/verify")
    public Result verify(@RequestHeader(name = "Authorization") String token,
                         @RequestBody EmailCodeDTO emailCodeDTO) {
        // 1.分步校验目前只支持更换邮箱验证旧邮箱这一个场景
        if (!IEmailCodeService.SCENE_CHANGE_EMAIL.equals(emailCodeDTO.getScene())) {
            return Result.error("使用场景不正确");
        }
        // 2.验证码发在当前用户已绑定的邮箱上，校验时也按绑定的邮箱来查
        User user = userService.getById(((Number) JwtUtil.parseToken(token).get("id")).longValue());
        if (user == null || ObjectUtils.isEmpty(user.getEmail())) {
            return Result.error("您尚未绑定邮箱，请先绑定邮箱");
        }
        return emailCodeService.checkCode(user.getEmail(), emailCodeDTO.getScene(), emailCodeDTO.getCode());
    }
}