package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MusicMapper {

    // 插入音乐

    /**
     *
     * @param title
     * @param singer
     * @param time
     * @param url
     * @param userid
     * @return
     */
    int insert(String title, String singer, String time, String url, int userid);


    String[] isExist(String title);


}

