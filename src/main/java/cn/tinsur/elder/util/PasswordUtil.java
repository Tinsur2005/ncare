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

package cn.tinsur.elder.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类：BCrypt哈希（自带随机盐，同一明文每次哈希结果都不同）
 */
public class PasswordUtil {

    //BCryptPasswordEncoder线程安全，静态单例复用即可
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    //明文密码 -> BCrypt哈希（固定60字符，$2a$10$开头）
    public static String hash(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    //明文密码 与 库中哈希 比对
    public static boolean matches(String rawPassword, String hashedPassword) {
        //任一为空直接判失败，避免脏数据导致NPE
        if (rawPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        return ENCODER.matches(rawPassword, hashedPassword);
    }

    public static void main(String[] args) {
        //生成已知明文的哈希，用于手动重置存量账号密码
        System.out.println(hash("123456"));
    }
}