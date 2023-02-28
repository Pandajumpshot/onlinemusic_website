package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MusicMapper {

    int insert(String title, String singer, String time, String url, int userid);
}
