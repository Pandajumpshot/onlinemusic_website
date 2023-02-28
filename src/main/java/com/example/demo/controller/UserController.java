package com.example.demo.controller;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.tools.Constant;
import com.example.demo.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @RequestMapping("/login1")
    public ResponseBodyMessage<User> login1(@RequestParam String username, @RequestParam String password,
                                           HttpServletRequest request) {
        User userLogin = new User();
        userLogin.setUsername(username);
        userLogin.setPassword(password);

        // 实现mapper接口
        User user1 = userMapper.login(userLogin);

        // 正常情况下 不能直接返回这样的ResponseBodyMessage
        // 返回这样的信息会被抓包泄漏账号密码
        if (user1 == null) {
            System.out.println("登录失败");
            return new ResponseBodyMessage<>(-1,"登录失败",userLogin);
        } else {
            request.getSession().setAttribute(Constant.USERINFO_SESSION_KEY,user1);
            System.out.println("登录成功");
            return new ResponseBodyMessage<>(0,"登录成功",userLogin);
        }
    }

    @RequestMapping("/login")
    public ResponseBodyMessage<User> login(@RequestParam String username, @RequestParam String password,
                                            HttpServletRequest request) {
//        User userLogin = new User();
//        userLogin.setUsername(username);
//        userLogin.setPassword(password);
//
//        // 实现mapper接口
//        User user1 = userMapper.login(userLogin);

        User user = userMapper.selectByName(username);
        if (user == null) {
            System.out.println("登录失败");
            return new ResponseBodyMessage<>(-1,"登录失败",user);
        } else {
            boolean flag = bCryptPasswordEncoder.matches(password, user.getPassword());
            if (!flag) {
                return new ResponseBodyMessage<>(-1,"用户名或者密码错误",user);
            }
            request.getSession().setAttribute(Constant.USERINFO_SESSION_KEY, user);
            return new ResponseBodyMessage<>(0, "登录成功", user);
        }
    }
}
