package com.dzj.app.basiclibrary.String;

/**
 * 正则表达式操作
 * @author hWX201806
 *
 */
public class RegexUtils {

	/**
	 * 多字节字符的范围
	 */
    public static final String MULTICHAR = "[\u0391-\uFFE5]";
    /**
	 * 简体中文字符的范围
	 */
    public static final String SIMPLIFIED_CHINESE = "[\u4e00-\u9fff]";
    /**
	 * 繁体中文字符的范围
	 */
    public static final String TRADITIONAL_CHINESE = "[\uf900-\ufa2d]";
    /**
   	 * 全角字符的范围(\uFF00-\uFFFF是全角范围, \\pP是中文标点符号范围)
   	 */
    public static final String SBC_CHAR = "[\uFF00-\uFFFF\\pP‘’“”]";
    /**
     * 特殊字符
     */
    public static final String SPECIAL_CHAR = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& ;*（）——+|{}【】‘；：”“’。，、？]";
    /**
   	 * 数字
   	 */
    public static final String NUMERIC = "^\\d+$";
    /**
     * 字母
     */
    public static final String ALPHA = "^[a-zA-Z]+$";
    /**
     * 字母和空格
     */
    public static final String ALPHA_SPACE = "^[a-zA-Z\\s]+$";
    /**
     * 字母跟数字
     */
    public static final String ALPHA_NUMERIC = "^[a-zA-Z0-9]+$";
    /**
     * 数字跟空格
     */
    public static final String NUMERIC_SPACE = "^[0-9\\s]+$";
    /**
     * 字母、数字和空格
     */
    public static final String ALPHA_NUMERIC_SPACE = "^[a-zA-Z0-9\\s]+$";
    /**
     * 小数
     */
    public static final String DEMICAL = "^[-+]{0,1}\\d+\\.\\d*$|[-+]{0,1}\\d*\\.\\d+$";
    /**
     * 整数
     */
    public static final String WHOLENUMBER = "[+-]{0,1}0|^\\+{0,1}[1-9]\\d*$|^-[1-9]\\d*$";
    /**
     * 电子邮件
     */
    public static final String EMAIL = "^[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])*@([a-zA-Z0-9]+(-[a-zA-Z0-9]+)?\\.)+[a-zA-Z]{2,}$";
    /**
     * 中国大陆手机号码
     */
    public static final String PHONE_CN = "^(([+]?86)?|0086)?((13[0-9])|(15[^4,\\D])|(18[0,5-9])|(14[5,7]|(17[0,6,7,8])))\\d{8}$";
    
}
