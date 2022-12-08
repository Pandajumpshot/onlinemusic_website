package com.example.demo.tools;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    // 固定盐值 
    private static final String salt = "1p2h3f4";

    public static String md5(String src) {
        return DigestUtils.md2Hex(src);
    }


    /**
     * 客户端 第一次加盐 加密
     * @param inputPass
     * @return
     */
    public static String inputPassToFormPass(String inputPass, String salt) {
        String formPass = "" + salt.charAt(1) + salt.charAt(3) + inputPass
                + salt.charAt(5) + salt.charAt(6);
        return md5(formPass);
    }


    /**
     * 服务器 第二次 加盐 加密码
     * @param formPass 客户端第一次加密后 密码
     * @param salt
     * @return
     */
    public static String formPassToDBPass(String formPass , String salt) {
        String dbPass = "" + salt.charAt(1) + salt.charAt(3) + formPass
                + salt.charAt(5) + salt.charAt(6);
        return md5(dbPass);
    }


    /**
     * 将以上两个加密过程调用 一步 将inputPass转化为dbPass
     * @param inputPass
     * @param salt
     * @return
     */
    public static String InputPassToDBPass(String inputPass , String salt) {
        String formPass = inputPassToFormPass(inputPass, salt);
        String dbPass = formPassToDBPass(formPass, salt);
        return dbPass;
    }
}
