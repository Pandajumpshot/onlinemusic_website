package com.example.demo.tools;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 测试使用BCryptTest加密过程
public class BCryptTest {
    public static void main(String[] args) {

        String password = "123456"; // 模拟客户端密码

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String newPassword = bCryptPasswordEncoder.encode(password);
        // 每次加密后的newPassword是不一样的
        System.out.println("加密密码为： " + newPassword);

        // BCrypt加密自带一个验证方法 不管加密成什么样 都会匹配成功
        boolean same_password_result = bCryptPasswordEncoder.matches(password, newPassword);
        System.out.println("加密的密码和正确的密码匹配结果为" + same_password_result);

        boolean other_password_result = bCryptPasswordEncoder.matches("7890", newPassword);
        System.out.println("加密的密码和随机的密码匹配结果为" + other_password_result);

    }
}
