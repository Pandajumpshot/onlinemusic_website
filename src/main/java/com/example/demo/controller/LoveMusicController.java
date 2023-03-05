package com.example.demo.controller;

import com.example.demo.mapper.LoveMusicMapper;
import com.example.demo.model.Music;
import com.example.demo.model.User;
import com.example.demo.tools.Constant;
import com.example.demo.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/lovemusic")
public class LoveMusicController {

    @Autowired
    private LoveMusicMapper loveMusicMapper;

    @RequestMapping("/likeMusic")
    public ResponseBodyMessage<Boolean> likeMusic(@RequestParam String id, HttpServletRequest request) {
        int music_id = Integer.parseInt(id);
        System.out.println("music_id" + music_id);
        // 1. 首先要检查是否登录  收藏音乐必须是在已经登录的情况
        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            return new ResponseBodyMessage<>(-1, "用户还没登录 无法收藏音乐", false);
        }

        // 2.获取到userid 查询收藏音乐是否已经在喜欢列表
        User user = (User) httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int user_id = user.getId();
        System.out.println("user_id" + user_id);
        Music music = loveMusicMapper.findMusicByUserIdAndMusicId(user_id,music_id);
        if (music != null) {
            // 之前已经收藏过了 不能再次收藏
            return new ResponseBodyMessage<>(-1, "选定音乐已经收藏过", false);
        }

        // 3. 确定没有重复收藏 接着收藏音乐
        boolean res = loveMusicMapper.insertLoveMusic(user_id, music_id);
        if (res) {
            return new ResponseBodyMessage<>(0, "收藏音乐成功", true);
        }
        return new ResponseBodyMessage<>(-1, "收藏音乐失败", false);
    }

    @RequestMapping("/cancelLike")
    public ResponseBodyMessage<Boolean> cancelLike(@RequestParam String musicId, @RequestParam String userId, HttpServletRequest request) {
        int music_id = Integer.parseInt(musicId);
        System.out.println("music_id" + music_id);
        // 1. 首先要检查是否登录  取消收藏要在登录情况下
        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            return new ResponseBodyMessage<>(-1, "用户还没登录 无法取消收藏音乐", false);
        }
        // 2.获取到userid
        User user = (User) httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int user_id = user.getId();
        System.out.println("user_id" + user_id);
        boolean res = loveMusicMapper.deleteLoveMusic(user_id, music_id);
        if (res) {
            return new ResponseBodyMessage<>(0, "取消收藏音乐成功", true);
        }
        return new ResponseBodyMessage<>(-1, "取消收藏音乐失败", false);
    }

    @RequestMapping("/findlovemusic")
    public ResponseBodyMessage<List<Music>> findLoveMusic(@RequestParam(required = false) String musicName, HttpServletRequest request) {
        // 进行服务器的上传
        // 1.request参数用于验证是否已经登录
        // false的意思是 验证是否已经登录用户 如果没有创建 不再创建
        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            // 到这里 说明用户还未登录
            System.out.println("还未登录");
            return new ResponseBodyMessage<>(-1, "请登录后查询喜欢音乐", null);
        }

        // 2. 到这说明已经登录 判断musicName是否为空
        User user = (User)httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userId = user.getId();
        List<Music> res = null;
        if (musicName == null) {
            res = loveMusicMapper.findLoveMusicByUserId(userId);
        } else {
            res = loveMusicMapper.findLoveMusicBykeyAndUID(musicName, userId);
        }
        return new ResponseBodyMessage<>(0, "查询到了所有的歌曲信息",res);
    }
}
