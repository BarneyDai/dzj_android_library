package com.dzj.app.basiclibrary.String;

import java.io.UnsupportedEncodingException;

/**
 * 字符串一般工具类
 *
 * Created by daizhongjie on 16/5/13.
 */
public class StringUtils {

    /**
     * 空字符串
     */
    public static final String EMPTY = "";
    /**
     * 回车符
     */
    public static final String ENTRY = "\n";

    /**
     * 将字节数组转换成指字编码的字符串
     *
     * @param bs 字节数组
     * @param encoding 编码
     * @return 字符串
     */
    public static String bytesToString(byte[] bs, String encoding) {
        String result = EMPTY;
        if(StringJudgement.isNullOrWhiteSpace(encoding)) {
            result = new String(bs);
        } else {
            try {
                result =new String(bs, encoding);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                result = new String(bs);
            }
        }
        return result;
    }

}
