package com.example.demo.tools;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 测试使用BCryptTest加密过程
public class BCryptTest {
    public static void main(String[] args) {

        String password = "1234"; // 模拟客户端密码

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String newPassword = bCryptPasswordEncoder.encode(password);
        // 每次加密后的newPassword是不一样的
        System.out.println("加密密码为： " + newPassword);

        // BCrypt加密自带一个验证方法 不管加密成什么样 都会匹配成功
        // 参数放置 第一个参数原始密码 第二个参数BCrypt加密密码
        boolean other_password_result = bCryptPasswordEncoder.matches(password, newPassword);
        System.out.println(other_password_result);

    }
}
