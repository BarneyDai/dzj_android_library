//package com.dzj.app.basiclibrary.Convertor;
//
//import java.io.BufferedOutputStream;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.UnsupportedEncodingException;
//import java.util.List;
//import android.graphics.Bitmap;
//import android.graphics.Bitmap.CompressFormat;
//import android.graphics.BitmapFactory;
//import android.util.Base64;
//
///**
// * 转换器工具类
// *
// * Created by daizhongjie on 16/5/10.
// */
//public class ConvertUtils {
//
//    private final static String TAG = ConvertUtils.class.getSimpleName();
//
//    private final static int BUFFER_SIZE = 4096;
//
//    /**
//     * 描述:	InputStream转换为byte数组
//     * @param is	InputStream
//     * @return byte数组
//     */
//    public static byte[] inputStreamToBytes(InputStream is){
//        if(is == null) return null;
//        byte[] buffered = new byte[BUFFER_SIZE];
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        int count = -1;
//        try {
//            while((count = is.read(buffered)) != -1){
//                baos.write(buffered, 0, count);
//            }
//            return baos.toByteArray();
//        } catch (Exception e) {
//            LogManager.getInstance().write(TAG + "\tinputStreamToBytes\t" + e.getMessage());
//            return null;
//        } finally {
//            try {
//                baos.close();
//                is.close();
//            } catch (IOException e) {
//                LogManager.getInstance().write(TAG + "\tinputStreamToBytes\t" + e.getMessage());
//            }
//        }
//    }
//
//    /**
//     * 描述:	InputStream转换成指定编码的字符串
//     * @param is	InputStream
//     * @param encoding	编码方式
//     * @return 字符串
//     */
//    public static String inputStreamToString(InputStream is, String encoding) {
//        if(is == null) return null;
//        try {
//            byte[] bs = inputStreamToBytes(is);
//            if(StringUtils.isNullOrSpace(encoding)) return new String(bs);
//            return new String(bs, encoding);
//        } catch (Exception e) {
//            LogManager.getInstance().write(TAG + "\tinputStreamToString\t" + e.getMessage());
//            return null;
//        }
//    }
//
//    /**
//     * 描述：将指定编码的字符串转换成InputStream
//     * @param s 字符串
//     * @param encoding	指定的编码
//     * @return 输入流
//     */
//    public static InputStream stringToInputStream(String s, String encoding) {
//        if(StringUtils.isNullOrSpace(s)) return null;
//        byte[] bs;
//        ByteArrayInputStream bais = null;
//        try {
//            if(StringUtils.isNullOrSpace(encoding)) bs = s.getBytes();
//            bs = s.getBytes(encoding);
//            bais = new ByteArrayInputStream(bs);
//            return bais;
//        } catch (Exception e) {
//            LogManager.getInstance().write(TAG + "\tstringToInputStream\t" + e.getMessage());
//            return null;
//        } finally {
//            try {
//                bais.close();
//            } catch (IOException e) {
//                LogManager.getInstance().write(TAG + "\tstringToInputStream\t" + e.getMessage());
//            }
//        }
//    }
//
//    /**
//     * 描述：文件转换成字节数组
//     * @param path	文件路径
//     * @return	byte数组
//     */
//    public static byte[] fileToBytes(String path) {
//        if(StringUtils.isNullOrSpace(path)) return null;
//        FileInputStream fis = null;
//        ByteArrayOutputStream baos = null;
//        try {
//            File f = new File(path);
//            if(f.exists() && f.isFile()) {
//                fis = new FileInputStream(f);
//                baos = new ByteArrayOutputStream(BUFFER_SIZE);
//                byte[] b = new byte[BUFFER_SIZE];
//                int n;
//                while ((n = fis.read(b)) != -1) {
//                    baos.write(b, 0, n);
//                }
//                return baos.toByteArray();
//            }
//        } catch (Exception e) {
//            LogManager.getInstance().write(TAG + "\tfileToBytes\t" + e.getMessage());
//        } finally {
//            try {
//                baos.close();
//                fis.close();
//            } catch (IOException e) {
//                LogManager.getInstance().write(TAG + "\tfileToBytes\t" + e.getMessage());
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 描述：字节数组转换成文件
//     * @param bts	字节数组
//     * @param path	保存文件路径
//     * @return	文件
//     */
//    public static File bytesToFile(byte[] bts, String path) {
//        if(bts == null) return null;
//        if(StringUtils.isNullOrSpace(path)) return null;
//        int index = path.lastIndexOf("/");
//        String filePath = path.substring(0, index+1);
//        String fileName = path.substring(index+1);
//        BufferedOutputStream bos = null;
//        File f = null;
//        File dir = new File(filePath);
//        if(!dir.exists() && dir.isDirectory()){
//            dir.mkdirs();
//        }
//        try {
//            f = new File(dir, fileName);
//            if(!f.exists()){
//                f.createNewFile();
//            }
//            bos = new BufferedOutputStream(new FileOutputStream(f, false));
//            bos.write(bts);
//            return f;
//        } catch (Exception e) {
//            LogManager.getInstance().write(TAG + "\tbytesToFile\t" + e.getMessage());
//        } finally {
//            try {
//                bos.close();
//            } catch (Exception e) {
//                LogManager.getInstance().write(TAG + "\tbytesToFile\t" + e.getMessage());
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 描述：将文件转换成Base64字符串
//     * @param path	文件路径
//     * @return	Base64编码字符串
//     */
//    public static String fileToBase64(String path) {
//        if(StringUtils.isNullOrSpace(path)) return null;
//        try {
//            byte[] bts = fileToBytes(path);
//            return Base64.encodeToString(bts, Base64.DEFAULT);
//        } catch(Exception e) {
//            LogManager.getInstance().write(TAG + "\tfileToBase64\t" + e.getMessage());
//            return null;
//        }
//    }
//
//    /**
//     * 描述：将Base64字符串转换成文件
//     * @param s	Base64字符串
//     * @param path	文件路径
//     * @return	Base64编码字符串
//     */
//    public static File base64ToFile(String s, String path) {
//        if(StringUtils.isNullOrSpace(s)) return null;
//        if(StringUtils.isNullOrSpace(path)) return null;
//        try {
//            byte[] bts = Base64.decode(s.getBytes(), Base64.DEFAULT);
//            return bytesToFile(bts, path);
//        } catch (Exception e) {
//            LogManager.getInstance().write(TAG + "\tbase64ToFile\t" + e.getMessage());
//            return null;
//        }
//    }
//
//    /**
//     * 描述：将对象转换成Base64字符串
//     * @param object	对象
//     * @return	Base64编码字符串
//     */
//    public static String entityToBase64(Object object) {
//        if(object == null) return null;
//        String result = null;
//        ByteArrayOutputStream baos = null;
//        ObjectOutputStream oos = null;
//        try {
//            baos = new ByteArrayOutputStream();
//            oos = new ObjectOutputStream(baos);
//            oos.writeObject(object);
//            byte[] bs = Base64.encode(baos.toByteArray(), Base64.DEFAULT);
//            result = new String(bs);
//        } catch (IOException e) {
//            LogManager.getInstance().write(TAG + "\tentityToBase64\t" + e.getMessage());
//        } finally {
//            try {
//                baos.close();
//                oos.close();
//            } catch (IOException e) {
//                LogManager.getInstance().write(TAG + "\tentityToBase64\t" + e.getMessage());
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 描述：将Base64字符串转换为对象
//     * @param s	字符串
//     * @return	对象
//     */
//    public static Object base64ToEntity(String s) {
//        if(StringUtils.isNullOrSpace(s)) return null;
//        Object result = null;
//        byte[] buffer = Base64.decode(s.getBytes(), Base64.DEFAULT);
//        ByteArrayInputStream bais = null;
//        ObjectInputStream ois = null;
//        try {
//            bais = new ByteArrayInputStream(buffer);
//            ois = new ObjectInputStream(bais);
//            result = ois.readObject();
//        } catch (Exception e) {
//            LogManager.getInstance().write(TAG + "\tbase64ToEntity\t" + e.getMessage());
//        }finally{
//            try {
//                ois.close();
//                bais.close();
//            } catch (IOException e) {
//                LogManager.getInstance().write(TAG + "\tbase64ToEntity\t" + e.getMessage());
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 描述：将Base64字符串转换为对象集合
//     * @param s	字符串
//     * @return	对象集合
//     */
//    public static <T> List<T> base64ToList(String s) {
//        if(StringUtils.isNullOrSpace(s)) return null;
//        List<T> list = null;
//        byte[] buffer = Base64.decode(s.getBytes(), Base64.DEFAULT);
//        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
//        try {
//            ObjectInputStream ois = new ObjectInputStream(bais);
//            list = (List<T>)ois.readObject();
//            ois.close();
//        } catch (Exception e) {
//            LogManager.getInstance().write(TAG + "\tbase64ToList\t" + e.getMessage());
//        } finally{
//            try {
//                bais.close();
//            } catch (IOException e) {
//                LogManager.getInstance().write(TAG + "\tbase64ToList\t" + e.getMessage());
//            }
//        }
//        return list;
//    }
//
//    /**
//     * 描述: 将本地图片转换为bitmap
//     * @param path	本地图片路径
//     * @param reqWidth	图片宽度
//     * @param reqHeight	图片高度
//     * @return Bitmap
//     */
//    public static Bitmap pictureToBitmap(String path, int reqWidth, int reqHeight){
//        if(StringUtils.isNullOrSpace(path)) return null;
//        Bitmap bitmap = null;
//        try {
//            File f = new File(path);
//            if(f.exists() && f.isFile()) {
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//                BitmapFactory.decodeFile(path, options);
//                int width = options.outWidth;
//                int height = options.outHeight;
//                options.inSampleSize = 1;
//                if(height > reqHeight || width > reqWidth){
//                    int heightRatio = Math.round((float) height / (float) reqHeight);
//                    int widthRatio = Math.round((float) width / (float) reqWidth);
//                    options.inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
//                }
//                options.inJustDecodeBounds = false;
//                bitmap = BitmapFactory.decodeFile(path, options);
//            }
//            return bitmap;
//        } catch (Exception e) {
//            LogManager.getInstance().write(TAG + "\tpictureToBitmap\t" + e.getMessage());
//            return null;
//        }
//    }
//
//    /**
//     * 描述：将Base64编码的字符串转换成圖片
//     * @param s Base64编码的字符串
//     * @return	Bitmap图片
//     */
//    public static Bitmap base64ToBitmap(String s) {
//        if(StringUtils.isNullOrSpace(s)) return null;
//        Bitmap bmp = null;
//        try {
//            byte[] bitmapArray = Base64.decode(s, Base64.DEFAULT);
//            bmp = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
//        } catch (Exception e) {
//            LogManager.getInstance().write(TAG + "\tbase64ToBitmap\t" + e.getMessage());
//        }
//        return bmp;
//    }
//
//    /**
//     * 描述：将圖片转换成Base64编码的字符串
//     * @param bmp 圖片
//     * @param format	指定的压缩格式
//     * @param rate	压缩比例
//     * @return	Base64编码字符串
//     */
//    public static String bitmapToBase64(Bitmap bmp, CompressFormat format, int rate) {
//        if(bmp == null) return null;
//        if(format == null) format = CompressFormat.PNG;
//        if(rate > 100 || rate <= 0 ) rate = 100;
//        ByteArrayOutputStream baos = null;
//        try {
//            baos = new ByteArrayOutputStream();
//            bmp.compress(format, rate, baos);
//            byte[] bs = baos.toByteArray();
//            return Base64.encodeToString(bs, Base64.DEFAULT);
//        } catch (Exception e) {
//            LogManager.getInstance().write(TAG + "\tbitmapToBase64\t" + e.getMessage());
//            return null;
//        } finally {
//            try {
//                baos.close();
//            } catch (IOException e) {
//                LogManager.getInstance().write(TAG + "\tbitmapToBase64\t" + e.getMessage());
//            }
//        }
//    }
//
//    /**
//     * 描述: byte数组转为16进制字符串
//     * @param bts	byte数组
//     * @return	16进制字符串
//     */
//    public static String bytesToHexString(byte[] bts){
//        if (bts == null) return null;
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < bts.length; i++) {
//            String stmp = Integer.toHexString(bts[i] & 0xff);
//            if (stmp.length() == 1) sb.append("0" + stmp);
//            else sb.append(stmp);
//        }
//        return sb.toString();
//    }
//
//    /**
//     * 描述: 16进制字符串转为byte数组
//     * @param hexString	16进制字符串
//     * @return	byte数组
//     */
//    public static byte[] stringToBytes(String hexString) {
//        if (StringUtils.isNullOrSpace(hexString)) return null;
//        if (hexString.length() % 2 == 1) return null;
//        byte[] ret = new byte[hexString.length() / 2];
//        try {
//            for (int i = 0; i < hexString.length(); i += 2) {
//                ret[i / 2] = Integer.decode("0x" + hexString.substring(i, i + 2)).byteValue();
//            }
//        } catch (Exception e) {
//            ret = null;
//            LogManager.getInstance().write(TAG + "\tstringToBytes\t" + e.getMessage());
//        }
//        return ret;
//    }
//
//    /**
//     * 描述: 指定编码的字符串转byte数组
//     * @param s	字符串
//     * @param encoding	编码方式
//     * @return byte数组
//     */
//    public static byte[] stringToBytes(String s, String encoding){
//        if(StringUtils.isNullOrSpace(s)) return null;
//        byte[] bts = null;
//        if(StringUtils.isNullOrSpace(encoding)){
//            bts = s.getBytes();
//        } else {
//            try {
//                bts = s.getBytes(encoding);
//            } catch (UnsupportedEncodingException e) {
//                LogManager.getInstance().write(TAG + "/tstringToBytes/t" + e.getMessage());
//            }
//        }
//        return bts;
//    }
//
//    /**
//     * 描述:byte数组转为指定编码的字符串
//     * @param bts	byte数组
//     * @param encoding	编码方式
//     * @return	字符串
//     */
//    public static String bytesToString(byte[] bts, String encoding){
//        if(bts == null) return null;
//        String s = null;
//        if(StringUtils.isNullOrSpace(encoding)){
//            s = new String(bts);
//        } else {
//            try {
//                s = new String(bts, encoding);
//            } catch (UnsupportedEncodingException e) {
//                LogManager.getInstance().write(TAG + "/tbytesToString/t" + e.getMessage());
//            }
//        }
//        return s;
//    }
//
//
//}
