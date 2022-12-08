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

    @Value("${music_local_path}") // 从配置文件当中获取到文件上传到服务器的路径
    private String SAVE_PATH;

    @Autowired
    private MusicMapper musicMapper;

    @RequestMapping("/upload") // 上传音乐
    // MultipartFile在org.springframework.web.multipart中 处理文件上传的主要类
    public ResponseBodyMessage<Boolean> insertMusic(@RequestParam String singer,
                                                    @RequestParam("filename") MultipartFile file,
                                                    HttpServletRequest request) {
        // 1. 检查是否已经登录
        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            System.out.println("没有登录！");
            return new ResponseBodyMessage<>(-1,"请登录后上传音乐",false);
        }

        // 2. 上传到服务器
        String filenameAndType = file.getOriginalFilename(); // 能够获得xxx.mp3
        String path = SAVE_PATH + "/" + filenameAndType;

        File dest = new File(path);
        if (!dest.exists()) {
            dest.mkdir();
        }

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseBodyMessage<>(-1, "服务器上传失败", false);
        }

         // 接下来将已经上传到服务器的数据 上传到数据库当中
         // 1. 先准备好数据  2. 调用insert
        int dotIndex = filenameAndType.lastIndexOf(".");
        String title = filenameAndType.substring(0, dotIndex);

        User user = (User)httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userid = user.getId();

        // 用于播放音乐 -> http请求
        String url = "/music/get?path=" + title;

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sf.format(new Date());

        int ret = 0;
        ret =  musicMapper.insert(title,singer,time,url,userid);
        if (ret == 1) {
            // 这里应该跳转到音乐列表 页面
            return new ResponseBodyMessage<>(1,"数据库上传成功",true);
        } else {
            return new ResponseBodyMessage<>(-1,"数据库上传失败",false);
        }

    }
}
