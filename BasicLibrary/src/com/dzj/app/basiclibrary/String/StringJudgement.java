package com.dzj.app.basiclibrary.String;

/**
 * 一个字符串信息的判断类
 *
 * Created by daizhongjie on 16/5/13.
 */
public class StringJudgement {

    /**
     * 判断字符串是否为NULL或Empty
     *
     * @param s 字符串
     * @return true/false
     */
    public static Boolean isNullOrEmpty(String s) {
        if(s == null || s.isEmpty()) return true;
        return false;
    }

    /**
     * 判断字符串是否为NULL、Emtpy或空白字符组成
     *
     * @param s 字符串
     * @return true/false
     */
    public static Boolean isNullOrWhiteSpace(String s) {
        if(s==null || s.equals("")) return true;
        if(s.trim().equals("")) return true;
        return false;
    }

    /**
     * 字符串是否是指定的基本类型
     *
     * @param s 字符串
     * @param cls 类型
     * @return true/false
     */
    public static Boolean isType(String s, Class<?> cls) {
        if (isNullOrWhiteSpace(s)) return false;
        if (cls == null) return false;
        try {
            if (cls == byte.class || cls == java.lang.Byte.class) {
                Byte.parseByte(s);
                return true;
            } else if (cls == short.class || cls == java.lang.Short.class) {
                Short.parseShort(s);
                return true;
            } else if (cls == int.class || cls == java.lang.Integer.class) {
                Integer.parseInt(s);
                return true;
            } else if (cls == long.class || cls == java.lang.Long.class) {
                Long.parseLong(s);
                return true;
            } else if (cls == short.class || cls == java.lang.Short.class) {
                Short.parseShort(s);
                return true;
            } else if (cls == double.class || cls == java.lang.Double.class) {
                Double.parseDouble(s);
                return true;
            } else if (cls == boolean.class || cls == java.lang.Boolean.class) {
                if(s.equals("true") || s.equals("false")) return true;
                return false;
            } else if (cls == char.class || cls == java.lang.Character.class) {
                if(s.length() == 1) return true;
                return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }


}
