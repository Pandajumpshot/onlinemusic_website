package com.example.demo.controller;

import com.example.demo.mapper.MusicMapper;
import com.example.demo.model.Music;
import com.example.demo.model.User;
import com.example.demo.tools.Constant;
import com.example.demo.tools.ResponseBodyMessage;
import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        // 2. 到这里检查是否重复上传 (查询数据库当中是否已经存在这首音乐)
        // 判断逻辑是歌曲名和歌手是不是都一样
        Music music = musicMapper.selectUrlByTitleAndSinger(title, singer);
        if (music != null) {
            return new ResponseBodyMessage<>(-1, "数据库当中存在音乐，不可重复上传", false);
        }

        User user = (User) httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userid = user.getId();
        // url作用
        // 1. 用于后面播放音乐 发送的是一个http请求
        String url = "/music/get?path=" + title;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sf.format(new Date());

        try{
            int ret = musicMapper.insert(title, singer, time, url, userid);
            if (ret == 1) {
                // 上传成功 应该跳转到音乐列表页面
                return new ResponseBodyMessage<>(0, "数据库上传成功", true);
            } else {
                return new ResponseBodyMessage<>(-1, "数据库上传失败", false);
            }
        } catch (BindingException e) {
            dest.delete();
            return new ResponseBodyMessage<>(-1, "数据库上传失败", false);
        }

    }

    @RequestMapping("/get")
    public ResponseEntity<byte[]> func(String path) {
        // 实现播放音乐功能
        File file = new File(SAVE_PATH + path);
        byte[] a = null;
        try {
            a = Files.readAllBytes(file.toPath());
            if (a == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(a);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }


    // 实现删除音乐操作
    @RequestMapping("/delete")
    public ResponseBodyMessage<Boolean> delete(@RequestParam String id) {
        int iid = Integer.parseInt(id);
        // 1. 先在数据库当中查找音乐 判断音乐是否存在
        Music music = musicMapper.selectById(iid);
        if (music == null) {
            // 到这 说明音乐数据库当中
            return new ResponseBodyMessage<>(-1, "指定删除的音乐不存在", false);
        } else {
            // 2. 进行删除
            // 2.1 进行数据库删除
            int ret = musicMapper.deleteMusicById(music.getId());
            if (ret == 1) {
                // 2.2 进行服务器删除
                File file = new File(SAVE_PATH + music.getTitle() + ".mp3");
                if (file.delete()) {
                    return new ResponseBodyMessage<>(0, "服务器中的音乐删除成功", true);
                } else {
                    return new ResponseBodyMessage<>(-1, "服务器中的音乐删除失败", false);
                }
            } else {
                return new ResponseBodyMessage<>(-1, "数据库中的音乐删除失败", false);
            }
        }
    }

    // 进行批量删除
    // 加入for循环 循环删除id数组 数组每个元素的删除逻辑相同
    @RequestMapping("/deleteSel")
    public ResponseBodyMessage<Boolean> deleteMusic(@RequestParam("id[]")List<Integer> id) {
        int sum = 0; // 记录删除元素的个数
        for (int i = 0; i < id.size(); i++) {
            Music music = musicMapper.selectById(id.get(i));
            if (music == null) {
                // 到这 说明音乐数据库当中
                return new ResponseBodyMessage<>(-1, "指定删除的音乐不存在", false);
            } else {
                // 2. 进行删除
                // 2.1 进行数据库删除
                int ret = musicMapper.deleteMusicById(music.getId());
                if (ret == 1) {
                    // 2.2 进行服务器删除
                    File file = new File(SAVE_PATH + music.getTitle() + ".mp3");
                    if (file.delete()) {
                        sum += ret;
                    } else {
                        return new ResponseBodyMessage<>(-1, "服务器中的音乐删除失败", false);
                    }
                } else {
                    return new ResponseBodyMessage<>(-1, "数据库中的音乐删除失败", false);
                }
            }
        }
        if (sum == id.size()) {
            return new ResponseBodyMessage<>(0, "整体删除成功", true);
        } else {
            return new ResponseBodyMessage<>(-1, "整体删除失败", false);
        }
    }


    // 进行音乐查询
    @RequestMapping("/findmusic")
    public ResponseBodyMessage<List<Music>> findMusic(@RequestParam(required = false) String musicName) {
        // 将RequestParam的required设置为false 此时musicName可以输入空值
        List<Music> list = null;
        if (musicName == null) {
            list = musicMapper.findMusic();
        } else {
            list = musicMapper.findMusicByName(musicName);
        }
        return new ResponseBodyMessage<>(0, "查询到了所有的音乐", list);
    }

}
