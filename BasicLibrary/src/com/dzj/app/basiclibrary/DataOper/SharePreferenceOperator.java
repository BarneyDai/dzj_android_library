//package com.dzj.app.basiclibrary.DataOper;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//
//import java.util.Map;
//
///**
// * SharePreference文件操作者
// *
// * Created by daizhongjie on 16/5/6.
// */
//public class SharePreferenceOperator {
//
//    private SharedPreferences shared;
//
//    /**
//     * 构造函数
//     * @param context 上下文
//     * @param fileName 存储的名称
//     */
//    public SharePreferenceOperator(Context context, String fileName) {
//        this(context,fileName,android.content.Context.MODE_PRIVATE);
//    }
//
//    /**
//     * 构造函数
//     *
//     * @param context 上下文
//     * @param fileName 存储的名称
//     * @param mode 文件的访问模式
//     */
//    public SharePreferenceOperator(Context context, String fileName,int mode) {
//        this.shared = context.getSharedPreferences(fileName, mode);
//    }
//
//    /**
//     * 在存储中新增或更新指定键的值为字符串
//     * @param key 值
//     * @param value 值
//     */
//    public void set(String key , String value) {
//        SharedPreferences.Editor editor = this.shared.edit();
//        editor.putString(key, value);
//        editor.commit();
//    }
//    /**
//     * 在存储中新增或更新指定键的值为布尔值
//     * @param key 值
//     * @param value 值
//     */
//    public void set(String key , boolean value) {
//        SharedPreferences.Editor editor = this.shared.edit();
//        editor.putBoolean(key, value);
//        editor.commit();
//    }
//    /**
//     * 在存储中新增或更新指定键的值为单精度数字
//     * @param key 值
//     * @param value 值
//     */
//    public void set(String key , float value) {
//        SharedPreferences.Editor editor = this.shared.edit();
//        editor.putFloat(key, value);
//        editor.commit();
//    }
//    /**
//     * 在存储中新增或更新指定键的值为整型数字
//     * @param key 值
//     * @param value 值
//     */
//    public void set(String key , int value) {
//        SharedPreferences.Editor editor = this.shared.edit();
//        editor.putInt(key, value);
//        editor.commit();
//    }
//    /**
//     * 在存储中新增或更新指定键的值为长整型数字
//     * @param key 值
//     * @param value 值
//     */
//    public void set(String key , long value) {
//        SharedPreferences.Editor editor = this.shared.edit();
//        editor.putLong(key, value);
//        editor.commit();
//    }
//    /**
//     * 在存储中新增或更新指定键的值为对象
//     * @param key 值
//     */
//    public void setEntity(String key, Object entity) {
//        String base64 = ConvertUtils.entityToBase64(entity);
//        SharedPreferences.Editor editor = this.shared.edit();
//        editor.putString(key, base64);
//        editor.commit();
//    }
//
//    /**
//     * 取得布尔型的键值
//     * @param key 键
//     * @param def 默认值
//     * @return 布尔值
//     */
//    public boolean getBoolean(String key, boolean def) {
//        return this.shared.getBoolean(key, def);
//    }
//    /**
//     * 取得单精度数字的键值
//     * @param key 键
//     * @param def 默认值
//     * @return 单精度数字
//     */
//    public float getFloat(String key, float def) {
//        return this.shared.getFloat(key, def);
//    }
//    /**
//     * 取得整型数字的键值
//     * @param key 键
//     * @param def 默认值
//     * @return 整型数字
//     */
//    public int getInt(String key, int def) {
//        return this.shared.getInt(key, def);
//    }
//    /**
//     * 取得长整型数字的键值
//     * @param key 键
//     * @param def 默认值
//     * @return 长整型数字
//     */
//    public long getLong(String key, long def) {
//        return this.shared.getLong(key, def);
//    }
//    /**
//     * 取得字符串的键值
//     * @param key 键
//     * @return 字符串
//     */
//    public String getString(String key) {
//        return this.shared.getString(key, "");
//    }
//    /**
//     * 取得字符串的键值
//     * @param key 键
//     * @param def 默认值
//     * @return 字符串
//     */
//    public String getString(String key, String def) {
//        return this.shared.getString(key, def);
//    }
//    /**
//     * 取得对象的键值
//     * @param key 键
//     * @return 对象
//     */
//    public Object getEntity(String key) {
//        String base64 = this.shared.getString(key, "");
//        return ConvertUtils.base64ToEntity(base64);
//    }
//
//    /**
//     * 取得所有键值
//     * @return 所有键值
//     */
//    public Map<String, ?> getAll() {
//        return this.shared.getAll();
//    }
//
//    /**
//     * 删除存储中指定键的对象
//     * @param key 键
//     */
//    public void remove(String key) {
//        SharedPreferences.Editor editor = this.shared.edit();
//        editor.remove(key);
//        editor.commit();
//
//    }
//    /**
//     * 清空存储
//     */
//    public void clear() {
//        SharedPreferences.Editor editor = this.shared.edit();
//        editor.clear();
//        editor.commit();
//    }
//
//    /**
//     * 存储中是否存在指定的键
//     * @param key 键
//     * @return true:存在;false:不存在
//     */
//    public boolean contains(String key) {
//        return this.shared.contains(key);
//    }
//
//
//}