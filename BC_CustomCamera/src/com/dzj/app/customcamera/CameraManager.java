package com.dzj.app.customcamera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 摄像头管理类
 *
 * 揽括功能（预览、聚焦、拍照、闪光灯、变焦、转换摄像头）
 */
public class CameraManager {

    // TODO: 16/4/14  摄像头是否为空判断

    /**
     * 相机管理类实例
     */
    private static CameraManager manager;

    /**
     * 摄像头Api
     */
    private Camera camera;

    /**
     * 依附的页面
     */
    private Activity activity;

    /**
     * 是否是前置摄像头
     */
    public static boolean CAMERA_FACING_FRONT=false;

    private int curZoomValue = 0;

    //屏幕的宽度
    private int windowWidth;
    //屏幕的高度
    private int windowHight;

/////////////////////////////////////////////////////////////////////////////
    private Camera.Size adapterSize = null;
    private Camera.Size previewSize = null;
    /**
     * 最小预览界面的分辨率
     */
    private static final int MIN_PREVIEW_PIXELS = 480 * 320;
    /**
     * 最大宽高比差
     */
    private static final double MAX_ASPECT_DISTORTION = 0.15;

    private CameraManager(Activity activity){
        this.activity = activity;
        getScreenSize();
    }

    public static CameraManager getInstance(Activity activity){
        if (manager == null) {
            synchronized (SurfaceViewManager.class) {
                if (manager == null && activity != null) {
                    manager = new CameraManager(activity);
                }
                return manager;
            }
        }
        return manager;
    }

    /**
     * 打开摄像头
     *
     * @param surfaceHolder 画面依赖的Surface
     */
    public void openCamera(SurfaceHolder surfaceHolder){
        if (manager != null) {
            openCamera(surfaceHolder,0);
        }
    }

    /**
     * 打开前置摄像头
     *
     * @param surfaceHolder 画面依赖的Surface
     */
    private void openCamera(SurfaceHolder surfaceHolder,int cameraId){
        if (manager != null) {
            try {
                camera = Camera.open(cameraId);
                camera.setPreviewDisplay(surfaceHolder);
                camera.setDisplayOrientation(getPreviewDegree(this.activity));
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initCamera() {
        if (manager != null) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            //if (adapterSize == null) {
            setUpPicSize(parameters);
            setUpPreviewSize(parameters);
            //}
            if (adapterSize != null) {
                parameters.setPictureSize(adapterSize.width, adapterSize.height);
            }
            if (previewSize != null) {
                parameters.setPreviewSize(previewSize.width, previewSize.height);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
            } else {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            setDispaly(parameters, camera);
            try {
                camera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
            camera.startPreview();
            camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
        }
    }

    /**
     * 摄像头预览
     */
    public void startPreview(){
        if (camera != null) {
            camera.startPreview();
        }
    }

    /**
     * 拍照
     */
    public void takePicture(Camera.PictureCallback callback){
        if (camera != null) {
            camera.takePicture(null, null, callback);
        }
    }

    /**
     * 闪光灯开关   开->关->自动
     */
    public void turnLight(TurnLightListener turnLightListener) {
        if (this.camera == null || this.camera.getParameters() == null
                || this.camera.getParameters().getSupportedFlashModes() == null) {
            return;
        }
        Camera.Parameters parameters = this.camera.getParameters();
        String flashMode = this.camera.getParameters().getFlashMode();
        List<String> supportedModes = this.camera.getParameters().getSupportedFlashModes();

        if (Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)
                && supportedModes.contains(Camera.Parameters.FLASH_MODE_ON)) {//关闭状态

            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);

        } else if (Camera.Parameters.FLASH_MODE_ON.equals(flashMode)) {//开启状态

            if (supportedModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            } else if (supportedModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }

        } else if (Camera.Parameters.FLASH_MODE_AUTO.equals(flashMode)
                && supportedModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {

            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

        }

        this.camera.setParameters(parameters);

        turnLightListener.callBack(this.camera);
    }

    /**
     * 摄像头变焦
     * @param delta
     */
    public void addZoomIn(int delta) {

        try {
            Camera.Parameters params = camera.getParameters();
            Log.d("Camera", "Is support Zoom " + params.isZoomSupported());
            if (!params.isZoomSupported()) {
                return;
            }

            curZoomValue += delta;
            if (curZoomValue < 0) {
                curZoomValue = 0;
            } else if (curZoomValue > params.getMaxZoom()) {
                curZoomValue = params.getMaxZoom();
            }

            if (!params.isSmoothZoomSupported()) {
                params.setZoom(curZoomValue);
                camera.setParameters(params);
                return;
            } else {
                camera.startSmoothZoom(curZoomValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //定点对焦的代码
    public void pointFocus(int x, int y) {
        camera.cancelAutoFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            showPoint(x, y);
        }
        autoFocus();
    }

    /**
     * 转换摄像头
     */
    public void switchCamera(SurfaceHolder surfaceHolder) {

        if (surfaceHolder == null){
            return;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();

        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (!CAMERA_FACING_FRONT) {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    CAMERA_FACING_FRONT=true;
                    release();
                    openCamera(surfaceHolder,i);
                    break;
                }
            } else {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    CAMERA_FACING_FRONT=false;
                    release();
                    openCamera(surfaceHolder,i);
                    break;
                }
            }
        }
    }

    public int getCameraNumbers(){
        return Camera.getNumberOfCameras();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void showPoint(int x, int y) {
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> areas = new ArrayList<Camera.Area>();
            //xy变换了
            int rectY = -x * 2000 / windowWidth + 1000;
            int rectX = y * 2000 / windowHight - 1000;

            int left = rectX < -900 ? -1000 : rectX - 100;
            int top = rectY < -900 ? -1000 : rectY - 100;
            int right = rectX > 900 ? 1000 : rectX + 100;
            int bottom = rectY > 900 ? 1000 : rectY + 100;
            Rect area1 = new Rect(left, top, right, bottom);
            areas.add(new Camera.Area(area1, 800));
            parameters.setMeteringAreas(areas);
        }

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(parameters);
    }

    //实现自动对焦
    private void autoFocus() {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (camera == null) {
                    return;
                }
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            initCamera();//实现相机的参数初始化
                        }
                    }
                });
            }
        };
    }

    private void getScreenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager mWindowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        windowWidth = mWindowManager.getDefaultDisplay().getWidth();
        windowHight = mWindowManager.getDefaultDisplay().getHeight();
    }

    private void setUpPicSize(Camera.Parameters parameters) {

        if (adapterSize != null) {
            return;
        } else {
            adapterSize = findBestPictureResolution();
            return;
        }
    }

    private Camera.Size findBestPictureResolution() {
        Camera.Parameters cameraParameters = camera.getParameters();
        List<Camera.Size> supportedPicResolutions = cameraParameters.getSupportedPictureSizes(); // 至少会返回一个值

        StringBuilder picResolutionSb = new StringBuilder();
        for (Camera.Size supportedPicResolution : supportedPicResolutions) {
            picResolutionSb.append(supportedPicResolution.width).append('x')
                    .append(supportedPicResolution.height).append(" ");
        }

        Camera.Size defaultPictureResolution = cameraParameters.getPictureSize();

        // 排序
        List<Camera.Size> sortedSupportedPicResolutions = new ArrayList<Camera.Size>(
                supportedPicResolutions);
        Collections.sort(sortedSupportedPicResolutions, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        // 移除不符合条件的分辨率
        double screenAspectRatio = (double) windowWidth
                / (double) windowHight;
        Iterator<Camera.Size> it = sortedSupportedPicResolutions.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewResolution = it.next();
            int width = supportedPreviewResolution.width;
            int height = supportedPreviewResolution.height;

            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然后在比较宽高比
            boolean isCandidatePortrait = width > height;
            int maybeFlippedWidth = isCandidatePortrait ? height : width;
            int maybeFlippedHeight = isCandidatePortrait ? width : height;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }
        }

        // 如果没有找到合适的，并且还有候选的像素，对于照片，则取其中最大比例的，而不是选择与屏幕分辨率相同的
        if (!sortedSupportedPicResolutions.isEmpty()) {
            return sortedSupportedPicResolutions.get(0);
        }

        // 没有找到合适的，就返回默认的
        return defaultPictureResolution;
    }

    private void setUpPreviewSize(Camera.Parameters parameters) {

        if (previewSize != null) {
            return;
        } else {
            previewSize = findBestPreviewResolution();
        }
    }

    /**
     * 找出最适合的预览界面分辨率
     *
     * @return
     */
    private Camera.Size findBestPreviewResolution() {
        Camera.Parameters cameraParameters = camera.getParameters();
        Camera.Size defaultPreviewResolution = cameraParameters.getPreviewSize();

        List<Camera.Size> rawSupportedSizes = cameraParameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            return defaultPreviewResolution;
        }

        // 按照分辨率从大到小排序
        List<Camera.Size> supportedPreviewResolutions = new ArrayList<Camera.Size>(rawSupportedSizes);
        Collections.sort(supportedPreviewResolutions, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        StringBuilder previewResolutionSb = new StringBuilder();
        for (Camera.Size supportedPreviewResolution : supportedPreviewResolutions) {
            previewResolutionSb.append(supportedPreviewResolution.width).append('x').append(supportedPreviewResolution.height)
                    .append(' ');
        }

        // 移除不符合条件的分辨率
        double screenAspectRatio = (double) windowWidth
                / (double) windowHight;
        Iterator<Camera.Size> it = supportedPreviewResolutions.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewResolution = it.next();
            int width = supportedPreviewResolution.width;
            int height = supportedPreviewResolution.height;

            // 移除低于下限的分辨率，尽可能取高分辨率
            if (width * height < MIN_PREVIEW_PIXELS) {
                it.remove();
                continue;
            }

            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然preview宽高比后在比较
            boolean isCandidatePortrait = width > height;
            int maybeFlippedWidth = isCandidatePortrait ? height : width;
            int maybeFlippedHeight = isCandidatePortrait ? width : height;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }

            // 找到与屏幕分辨率完全匹配的预览界面分辨率直接返回
            if (maybeFlippedWidth == windowWidth
                    && maybeFlippedHeight == windowHight) {
                return supportedPreviewResolution;
            }
        }

        // 如果没有找到合适的，并且还有候选的像素，则设置其中最大比例的，对于配置比较低的机器不太合适
        if (!supportedPreviewResolutions.isEmpty()) {
            Camera.Size largestPreview = supportedPreviewResolutions.get(0);
            return largestPreview;
        }

        // 没有找到合适的，就返回默认的

        return defaultPreviewResolution;
    }

    //控制图像的正确显示方向
    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Build.VERSION.SDK_INT >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }
    }

    //实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation",
                    new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
            Log.e("Came_e", "图像出错");
        }
    }

    // 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 180;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 0;
                break;
        }
        return degree;
    }

    /**
     * 释放摄像头资源
     */
    public void release(){
        if (camera != null) {
            camera.stopPreview();
            camera.release(); // 释放照相机
            camera = null;
        }
    }

    /**
     * 转换闪光灯
     */
    public interface TurnLightListener{
        public void callBack(Camera camera);
    }

}
