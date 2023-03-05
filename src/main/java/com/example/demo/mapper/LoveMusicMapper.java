package com.example.demo.mapper;

import com.example.demo.model.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoveMusicMapper {

    // 通过user_id 和 music_id 查找音乐
    Music findMusicByUserIdAndMusicId(int userId, int musicId);

    // 收藏音乐 插入lovemusic表
    boolean insertLoveMusic(int userId, int musicId);

    // 取消收藏 从lovemusic表当中删除数据
    boolean deleteLoveMusic(int userId, int musicId);

    // 在无参情况下，通过userId查询收藏过的音乐
    List<Music> findLoveMusicByUserId(int userId);

    // 在有参情况下（参数为歌名），支持模糊查询，查询登录用户的收藏的对应歌曲
    List<Music> findLoveMusicBykeyAndUID(String musicName, int userId);
}
