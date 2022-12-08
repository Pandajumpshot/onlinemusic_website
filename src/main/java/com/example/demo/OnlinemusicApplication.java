package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// exclude是过滤掉security包的配置项（排除掉接口运行保护） 只需用到security包底下的BCryptPasswordEncoder
@SpringBootApplication(exclude =
        {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
public class OnlinemusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlinemusicApplication.class, args);
    }

}
