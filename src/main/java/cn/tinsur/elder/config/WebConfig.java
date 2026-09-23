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

import cn.tinsur.elder.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    //拦截所有请求
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/admin/users/login", "/admin/users/captcha", "/app/users/login");
    }

    //把前台String改成Date类型
    @Override
    public void addFormatters(FormatterRegistry registry) {
        String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        List<String> patterns = Arrays.asList(
                "yyyy-MM-dd HH:mm:ss",       // 普通格式
                "yyyy-MM-dd",                // 普通格式
                "yyyy-MM-dd'T'HH:mm:ss.SSSX",// ISO 格式：2025-08-25T09:21:40.922Z
                "yyyy-MM-dd'T'HH:mm:ssX"     // ISO 格式：2025-08-25T09:21:40Z
        );

        registry.addConverter(new Converter<String, Date>() {
            @Override
            public Date convert(String source) {
                for (String pattern : patterns) {
                    try {
                        return new SimpleDateFormat(pattern).parse(source);
                    } catch (ParseException ignored) {
                    }
                }
                throw new RuntimeException("时间转换失败：" + source);
            }
        });
    }
}
