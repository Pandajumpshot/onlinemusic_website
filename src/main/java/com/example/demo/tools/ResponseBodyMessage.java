package com.example.demo.tools;

import lombok.Data;

// 通用响应体
@Data
public class ResponseBodyMessage<T>{
    private int status; // 状态码
    private String message; // 返回信息(出错原因 及 具体信息)
    private T data; // 返回给前端

    public ResponseBodyMessage(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
