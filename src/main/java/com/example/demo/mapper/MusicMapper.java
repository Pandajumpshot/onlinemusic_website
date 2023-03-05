package com.example.demo.mapper;

import com.example.demo.model.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MusicMapper {

    // 插入音乐
    int insert(String title, String singer, String time, String url, int userid);

    // 根据歌名和歌手查找url
    Music selectUrlByTitleAndSinger(String title, String singer);

    // 通过歌曲id查找音乐
    Music selectById(int id);

    // 通过musicId删除音乐
    int deleteMusicById(int musicId);

    // 查询所有音乐
    List<Music> findMusic();

    // 根据歌名来查找音乐
    // 支持模糊查询
    List<Music> findMusicByName(String musicName) ;
}
