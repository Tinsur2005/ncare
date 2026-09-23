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
package cn.tinsur.elder.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * kaptcha图形验证码配置：以程序方式生成验证码文本和图片，不启用kaptcha自带的Servlet
 *
 * @author Tinsur
 * @since 2026-09-08
 */
@Configuration
public class KaptchaConfig {

    /**
     * 验证码生成器：4位字母数字组合（剔除0o1i等易混淆字符），主题色与登录页一致
     *
     * @return kaptcha验证码生成器
     */
    @Bean
    public DefaultKaptcha defaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        //边框及颜色
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "220,224,231");
        //图片尺寸
        properties.setProperty("kaptcha.image.width", "120");
        properties.setProperty("kaptcha.image.height", "40");
        //验证码字符集与长度
        properties.setProperty("kaptcha.textproducer.char.string", "23456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        //字符颜色和大小（用后台主题蓝）
        properties.setProperty("kaptcha.textproducer.font.color", "64,158,255");
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        //噪点颜色和水波纹干扰样式
        properties.setProperty("kaptcha.noise.color", "144,180,235");
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple");
        defaultKaptcha.setConfig(new Config(properties));
        return defaultKaptcha;
    }
}