package com.dzj.app.customcamera;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * 相机渲染控件状态管理类
 */
public class SurfaceViewManager {

    /**
     * SurfaceView依赖的Activity
     */
    private Activity activity;

    /**
     * 相机渲染控件
     */
    private SurfaceView surfaceView;

    /**
     *
     */
    private SurfaceHolder surfaceHolder;

    /**
     * 相机管理类实例
     */
    private static SurfaceViewManager manager;


    private PointFocusListener pointFocusListener;


    private float pointX, pointY;

    private int mode; //模式

    static final int FOCUS = 1;            // 聚焦
    static final int ZOOM = 2;            // 缩放

    private float dist;

    public PointFocusListener getPointFocusListener() {
        return pointFocusListener;
    }

    public void setPointFocusListener(PointFocusListener pointFocusListener) {
        this.pointFocusListener = pointFocusListener;
    }

    public SurfaceHolder getSurfaceHolder() {
        return surfaceHolder;
    }

    /**
     * 有参构造

     *
     * @param surfaceView 需要进行管理的SurfaceView
     * @param activity  SurfaceView依赖的Activity
     */
    public SurfaceViewManager(SurfaceView surfaceView,Activity activity) {
        this.surfaceView = surfaceView;
        this.activity = activity;

        init();
    }

    private void init(){
        if (surfaceView != null && this.activity != null) {
            this.surfaceHolder = surfaceView.getHolder();
            initHolder();
            initView();
        }
    }


    private void initHolder() {
        surfaceView.getHolder()
                .setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//
//    surfaceView.getHolder().setFixedSize(150, 150); // 设置Surface分辨率
        surfaceView.getHolder().setKeepScreenOn(true);// 屏幕常亮
        surfaceView.getHolder().addCallback(new SurfaceCallback());// 为SurfaceView的句柄添加一个回调函数
    }

    private void initView(){

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    // 主点按下
                    case MotionEvent.ACTION_DOWN:
                        pointX = event.getX();
                        pointY = event.getY();
                        mode = FOCUS;
                        break;
                    // 副点按下
                    case MotionEvent.ACTION_POINTER_DOWN:
                        dist = spacing(event);
                        // 如果连续两点距离大于10，则判定为多点模式
                        if (spacing(event) > 10f) {
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = FOCUS;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == FOCUS) {
                            //pointFocus((int) event.getRawX(), (int) event.getRawY());
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                float tScale = (newDist - dist) / dist;
                                if (tScale < 0) {
                                    tScale = tScale * 10;
                                }
                                CameraManager.getInstance(SurfaceViewManager.this.activity).addZoomIn((int) tScale);
                            }
                        }
                        break;
                }
                return false;
            }
        });

        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CameraManager.getInstance(SurfaceViewManager.this.activity).pointFocus((int) pointX, (int) pointY);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (pointFocusListener != null){
                    pointFocusListener.pointFocusCallBack((int) pointX,(int) pointY);
                }

            }
        });
    }

    /**
     * 两点的距离
     */
    private float spacing(MotionEvent event) {
        if (event == null) {
            return 0;
        }
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    /**
     * 重构相机照相回调类
     * @author pc
     *
     */
    private final class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            CameraManager.getInstance(SurfaceViewManager.this.activity).openCamera(holder);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
//            CameraManager.getInstance(SurfaceViewManager.this.activity).initCamera();

            try {
                CameraManager.getInstance(SurfaceViewManager.this.activity).pointFocus((int) pointX, (int) pointY);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (pointFocusListener != null){
                pointFocusListener.pointFocusCallBack((int) pointX,(int) pointY);
            }

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            CameraManager.getInstance(SurfaceViewManager.this.activity).release();
        }

    }


    public interface PointFocusListener{
        public void pointFocusCallBack(int pointX,int pointY);
    }

}
