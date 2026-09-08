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
package cn.tinsur.elder.service.impl.tool;

import cn.tinsur.elder.mapper.EmailCodeMapper;
import cn.tinsur.elder.pojo.entity.EmailCode;
import cn.tinsur.elder.service.IEmailCodeService;
import cn.tinsur.elder.util.MailUtil;
import cn.tinsur.elder.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * 邮箱验证码表 服务实现类
 * </p>
 *
 * @author Tinsur
 * @since 2026-09-04
 */
@Service
public class EmailCodeServiceImpl extends ServiceImpl<EmailCodeMapper, EmailCode> implements IEmailCodeService {

    /**
     * 验证码有效期：5分钟
     */
    private static final long EXPIRE_MILLIS = 5 * 60 * 1000L;

    /**
     * 发送间隔限制：60秒内同邮箱同场景不允许重复发送
     */
    private static final long SEND_INTERVAL_MILLIS = 60 * 1000L;

    @Autowired
    private MailUtil mailUtil;

    /**
     * 向指定邮箱发送验证码
     * 同邮箱同场景60秒内只允许发送一次，发送前会把旧的未使用验证码全部作废
     * @param email 接收验证码的邮箱
     * @param scene 使用场景
     * @return 发送结果
     */
    @Override
    public Result sendCode(String email, String scene) {
        // 1.校验邮箱格式
        if (ObjectUtils.isEmpty(email) || !email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            return Result.error("邮箱格式不正确");
        }
        // 2.发送频率限制：查出该邮箱该场景最近一条记录，60秒内发过就拒绝
        EmailCode latest = getOne(new LambdaQueryWrapper<EmailCode>()
                .eq(EmailCode::getEmail, email)
                .eq(EmailCode::getScene, scene)
                .orderByDesc(EmailCode::getId)
                .last("limit 1"));
        if (latest != null && System.currentTimeMillis() - latest.getCreateTime().getTime() < SEND_INTERVAL_MILLIS) {
            return Result.error("操作过于频繁，请稍后再获取验证码");
        }
        // 3.把该邮箱该场景旧的未使用验证码全部作废，保证始终只有最新一个验证码可用
        update(new LambdaUpdateWrapper<EmailCode>()
                .eq(EmailCode::getEmail, email)
                .eq(EmailCode::getScene, scene)
                .eq(EmailCode::getUsed, 0)
                .set(EmailCode::getUsed, 1));
        // 4.生成6位数字验证码并保存（有效期5分钟）
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        EmailCode emailCode = new EmailCode();
        emailCode.setEmail(email);
        emailCode.setCode(code);
        emailCode.setScene(scene);
        emailCode.setExpireTime(new Date(System.currentTimeMillis() + EXPIRE_MILLIS));
        emailCode.setUsed(0);
        save(emailCode);
        // 5.发送验证码邮件，发送失败时把刚保存的记录作废，避免浪费一次验证机会
        try {
            mailUtil.sendTextMail(email, "【睦邻NCare】邮箱验证码",
                    "您的验证码是：" + code + "，5分钟内有效。为了您的账号安全，请勿泄露给他人。");
        } catch (Exception e) {
            emailCode.setUsed(1);
            updateById(emailCode);
            e.printStackTrace();
            return Result.error("验证码邮件发送失败，请稍后再试");
        }
        return Result.ok("验证码已发送，请查收邮件");
    }

    /**
     * 校验用户填写的验证码但不作废
     * 供分步验证流程使用，例如更换邮箱第一步先校验旧邮箱验证码，最后一步完成时再由业务接口调用verifyCode作废
     * @param email 接收验证码的邮箱
     * @param scene 使用场景
     * @param code  用户填写的验证码
     * @return 校验通过返回Result.ok()，失败返回带原因的Result.error()
     */
    @Override
    public Result checkCode(String email, String scene, String code) {
        return doVerify(email, scene, code);
    }

    /**
     * 校验用户填写的验证码
     * 校验通过后该验证码立即作废，只能使用一次
     * @param email 接收验证码的邮箱
     * @param scene 使用场景
     * @param code  用户填写的验证码
     * @return 校验通过返回Result.ok()，失败返回带原因的Result.error()
     */
    @Override
    public Result verifyCode(String email, String scene, String code) {
        // 1.先按公共逻辑校验验证码是否正确
        Result result = doVerify(email, scene, code);
        if (result.getCode() != Result.OK) {
            return result;
        }
        // 2.校验通过，把该邮箱该场景的未使用验证码全部作废，保证一个验证码只能使用一次
        update(new LambdaUpdateWrapper<EmailCode>()
                .eq(EmailCode::getEmail, email)
                .eq(EmailCode::getScene, scene)
                .eq(EmailCode::getUsed, 0)
                .set(EmailCode::getUsed, 1));
        return Result.ok();
    }

    /**
     * 校验验证码的公共逻辑：基础校验、查最新的未使用验证码、校验有效期、比对验证码，但不作废验证码
     * @param email 接收验证码的邮箱
     * @param scene 使用场景
     * @param code  用户填写的验证码
     * @return 校验通过返回Result.ok()，失败返回带原因的Result.error()
     */
    private Result doVerify(String email, String scene, String code) {
        // 1.基础校验：邮箱和验证码不能为空
        if (ObjectUtils.isEmpty(email) || ObjectUtils.isEmpty(code)) {
            return Result.error("请填写验证码");
        }
        // 2.查出该邮箱该场景最新的未使用验证码
        EmailCode latest = getOne(new LambdaQueryWrapper<EmailCode>()
                .eq(EmailCode::getEmail, email)
                .eq(EmailCode::getScene, scene)
                .eq(EmailCode::getUsed, 0)
                .orderByDesc(EmailCode::getId)
                .last("limit 1"));
        if (latest == null) {
            return Result.error("请先获取验证码");
        }
        // 3.校验是否过期
        if (latest.getExpireTime().before(new Date())) {
            return Result.error("验证码已过期，请重新获取");
        }
        // 4.比对验证码
        if (!latest.getCode().equals(code.trim())) {
            return Result.error("验证码错误");
        }
        return Result.ok();
    }
}