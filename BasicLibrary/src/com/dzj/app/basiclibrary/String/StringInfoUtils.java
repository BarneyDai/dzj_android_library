package com.dzj.app.basiclibrary.String;

import java.io.UnsupportedEncodingException;

/**
 * 一个获取字符串信息的工具类
 *
 * Created by daizhongjie on 16/5/13.
 */
public class StringInfoUtils {

    /**
     * 取得字节长度
     *
     * @param s 字符串
     * @return	字节长度
     */
    public static int getByteLength(String s) {
        return getByteLength(s, "");
    }

    /**
     * 取得字节长度
     *
     * @param s 字符串
     * @param encoding 编码
     * @return	字节长度
     */
    public static int getByteLength(String s, String encoding) {
        if(StringJudgement.isNullOrWhiteSpace(s)) return 0;
        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            String c = s.substring(i, i + 1);
            // 字符是否为多字节
            if (c.matches(RegexUtils.MULTICHAR)) {
                int charLen = getBytes(c, encoding).length;
                result += charLen;
            } else {
                result += 1;
            }
        }
        return result;
    }

    /**
     * 将指定编码的字符串转换成字节数组
     *
     * @param s 字符串
     * @param encoding 编码
     * @return 字节数组
     */
    public static byte[] getBytes(String s, String encoding) {
        byte[] bs = null;
        if(StringJudgement.isNullOrWhiteSpace(encoding)) {
            bs = s.getBytes();
        } else {
            try {
                bs = s.getBytes(encoding);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                bs = s.getBytes();
            }
        }
        return bs;
    }



}
