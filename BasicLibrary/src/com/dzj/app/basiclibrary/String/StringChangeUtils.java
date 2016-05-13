package com.dzj.app.basiclibrary.String;

/**
 * 字符串改变的工具类
 *
 * Created by daizhongjie on 16/5/13.
 */
public class StringChangeUtils {

    /**
     * 编码转换
     *
     * @param s 字符串
     * @param toEncoding 目标编码
     * @return 转码后的字符串
     */
    public static String changeEncoding(String s, String toEncoding) {
        return changeEncoding(s,"",toEncoding);
    }

    /**
     * 编码转换
     *
     * @param s 字符串
     * @param fromEncoding 源编码
     * @param toEncoding 目标编码
     * @return 转码后的字符串
     */
    public static String changeEncoding(String s, String fromEncoding, String toEncoding) {
        if(StringJudgement.isNullOrWhiteSpace(s)) return s;
        return StringUtils.bytesToString(StringInfoUtils.getBytes(s, fromEncoding), toEncoding);
    }


    /**
     * 在s1字符串的p位置插入s2
     *
     * @param s1 源字符串
     * @param s2 插入的字符串
     * @param p 指定位置
     * @return 字符串
     */
    public static String insert(String s1, String s2, int p) {
        if(StringJudgement.isNullOrWhiteSpace(s1)) return null;
        if(StringJudgement.isNullOrWhiteSpace(s2)) return s1;
        if (p < 0) throw new IllegalArgumentException("The p must not be > 0");
        if(s1.length() < p) p = s1.length();
        String start = s1.substring(0, p);
        String end = s1.substring(p);
        return start + s2 + end;
    }

    /**
     * 返回长度为len的字符串，当s大于len时，取右边的len个字符，当s小于len时，在s的左边补字符c
     *
     * @param s 源字符串
     * @param len 字符长度
     * @param c 补充的字符
     * @return 字符串
     */
    public static String padLeft(String s, int len, char c) {
        if(StringJudgement.isNullOrWhiteSpace(s)) return null;
        if (len < 0) throw new IllegalArgumentException("The len must not be > 0");
        int length = s.length();
        if(length > len) return s.substring(length - len, length);
        if(length == len) return s;
        else{
            int add = len - length;
            char[] chars = new char[add];
            for(int i = 0; i < add; i++){
                chars[i] = c;
            }
            return s + String.valueOf(chars);
        }
    }

    /**
     * 返回长度为len的字符串，当s大于len时，取左边的len个字符，当s小于len时，在s的右边补字符c
     *
     * @param s 源字符串
     * @param len 字符长度
     * @param c 补充的字符
     * @return 字符串
     */
    public static String padRight(String s, int len, char c) {
        if(StringJudgement.isNullOrWhiteSpace(s)) return null;
        if (len < 0) throw new IllegalArgumentException("The len must not be > 0");
        int length = s.length();
        if(length > len) return s.substring(0, len);
        if(length == len) return s;
        else{
            int add = len - length;
            char[] chars = new char[add];
            for(int i = 0; i < add; i++){
                chars[i] = c;
            }
            return String.valueOf(chars) + s;
        }
    }

    /**
     * 反转字符串
     *
     * @param s 字符串
     * @return 转换后的字符串
     */
    public static String reverse(String s) {
        if(StringJudgement.isNullOrWhiteSpace(s)) return null;
        int len = s.length();
        char[] chars = s.toCharArray();
        char[] result = new char[len];
        for(int i = len ; i > 0 ; i--){
            result[len - i] = chars[i-1];
        }
        return String.valueOf(result);
    }

    /**
     * 将字符串s重复len次
     *
     * @param s 源字符串
     * @param len 重复次数
     * @return 字符串
     */
    public static String repeat(String s, int len) {
        if (len < 0) throw new IllegalArgumentException("The len must not be > 0");
        String res = StringUtils.EMPTY;
        for(int i=0; i<len; i++) {
            res += s;
        }
        return res;
    }

    /**
     * 将字符串转换成全角
     * @param s 字符串
     * @return 转换后的字符串
     */
    public static String toSBC(String s) {
        if(StringJudgement.isNullOrWhiteSpace(s)) return s;
        char[] c = s.toCharArray();
        for (int index = 0; index < c.length; index++) {
            // 是否半角空格
            if (c[index] == 32) {
                c[index] = (char) 12288; // 全角空格为12288
            } else if (c[index] >= 33 && c[index] <= 126) {
                // 半角字符的范围 : 33 ~ 126
                c[index] = (char) (c[index] + 65248);	// 全解与半角相差65248
            }
        }
        return String.valueOf(c);
    }

    /**
     * 将字符串转换成半角
     * @param s 字符串
     * @return 转换后的字符串
     */
    public static String toDBC(String s) {
        if(StringJudgement.isNullOrWhiteSpace(s)) return s;
        char[] c = s.toCharArray();
        for (int index = 0; index < c.length; index++) {
            // 是否全角空格
            if (c[index] == 12288) {
                c[index] = (char) 32; // 半角空格为32
            } else if (c[index] >= 65281 && c[index] <= 65374) {
                // 全角字符的范围 : 65281 ~ 65374
                c[index] = (char) (c[index] - 65248);	// 全解与半角相差65248
            }
        }
        return String.valueOf(c);
    }

    /**
     * 首字符大写
     * @param s 字符串
     * @return 转换后的字符串
     */
    public static String capitalize(String s) {
        if(StringJudgement.isNullOrWhiteSpace(s)) return StringUtils.EMPTY;
        String first = s.substring(0, 1);
        return first.toUpperCase() + s.substring(1);
    }

    /**
     * 首字母小写
     * @param s 字符串
     * @return 转换后的字符串
     */
    public static String uncapitalize(String s) {
        if(StringJudgement.isNullOrWhiteSpace(s)) return StringUtils.EMPTY;
        String first = s.substring(0, 1);
        return first.toLowerCase() + s.substring(1);
    }
}
