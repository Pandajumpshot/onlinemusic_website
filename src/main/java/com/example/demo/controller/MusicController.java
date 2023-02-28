package com.example.demo.controller;

import com.example.demo.mapper.MusicMapper;
import com.example.demo.model.User;
import com.example.demo.tools.Constant;
import com.example.demo.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/music")
public class MusicController {

    @Value("${music.local.path}")
    private String SAVE_PATH/* = "/Users/panhongfeng/Desktop/music/"*/;

    @Autowired
    private MusicMapper musicMapper;

    @RequestMapping("/upload")
    public ResponseBodyMessage<Boolean> insertMusic(@RequestParam String singer,
                                                    @RequestParam("filename") MultipartFile file,
                                                    HttpServletRequest request) {
        // 进行服务器的上传
        // 1.request参数用于验证是否已经登录
        // false的意思是 验证是否已经登录用户 如果没有创建 不再创建
        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            // 到这里 说明用户还未登录
            System.out.println("还未登录");
            return new ResponseBodyMessage<>(-1, "请登录后上传", false);
        }
        // 到这里说明已经登录
        String fileNameAndType = file.getOriginalFilename(); // xxx.mp3
        String path = SAVE_PATH + fileNameAndType; // 文件最终保存的路径

        File dest = new File(path);
        if (!dest.exists()) {
            dest.mkdir();
        }
        try {
            file.transferTo(dest);
//            return new ResponseBodyMessage<>(0, "上传成功", true);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseBodyMessage<>(-1, "服务器上传失败", false);
        }

        // 进行数据库的上传
        // 1. 准备数据 2. 调用insert方法
        int index = fileNameAndType.lastIndexOf(".");
        String title = fileNameAndType.substring(0, index); // substring是 [0, k) 的

        User user = (User) httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userid = user.getId();
        // url作用
        // 1. 用于后面播放音乐 发送的是一个http请求
        String url = "/music/get?path=" + title;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sf.format(new Date());

        int ret = musicMapper.insert(title, singer, time, url, userid);
        if (ret == 1) {
            // 上传成功 应该跳转到音乐列表页面
            return new ResponseBodyMessage<>(0, "数据库上传成功", true);
        } else {
            return new ResponseBodyMessage<>(0, "数据库上传失败", false);
        }
    }


}
