package com.example.demo.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeTest {

    // 模拟如何获取到系统时间
    public static void main(String[] args) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sf.format(new Date());

        System.out.println("当前的时间" + time);

    }
}
