package com.dzj.app.customcamera;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dzj.app.basiclibrary.BasicActivity.BasicActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 自定义相机拍照页面 （模仿系统相机功能）
 */
public class CaptureActivity extends BasicActivity implements View.OnClickListener, SurfaceViewManager.PointFocusListener, Camera.PictureCallback {

    //UI控件
    private View table;
    private Button bntTakePic;
    private Button bntEnter;
    private Button bntCancel;
    private View focusIndex;
    private SurfaceView surfaceView;
    private ImageView flashBtn;
    private ImageView changeBtn;


    //相机获取图片保存路径
    public static final String TAKE_PHOTO_FILE_PAHT_CATEGORY = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "PAYiDaiXian"
            + File.separator + "Photo" + File.separator;

    public String photoname = "android.png";

    private int IS_TOOK = 0;//是否已经拍照 ,0为否
    private SurfaceViewManager surfaceViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
//        photoname = getIntent().getStringExtra("photoname");
        initView();
    }

    @Override
    protected void initView() {
        setContentView(R.layout.camera_activity_takephoto);

        table = findViewById(R.id.table);
        //按钮
        bntTakePic = (Button) findViewById(R.id.bnt_takepicture);
        bntEnter = (Button) findViewById(R.id.bnt_enter);
        bntCancel = (Button) findViewById(R.id.bnt_cancel);
        focusIndex = findViewById(R.id.focus_index);

        bntTakePic.setVisibility(View.VISIBLE);
        bntEnter.setVisibility(View.INVISIBLE);
        bntCancel.setVisibility(View.INVISIBLE);
        bntTakePic.setOnClickListener(this);
        bntEnter.setOnClickListener(this);
        bntCancel.setOnClickListener(this);

        //照相机预览的空间
        surfaceView = (SurfaceView) this
                .findViewById(R.id.surfaceView);
        surfaceViewManager = new SurfaceViewManager(surfaceView, this);
        surfaceViewManager.setPointFocusListener(this);

        flashBtn = (ImageView) findViewById(R.id.flashBtn);
        changeBtn = (ImageView) findViewById(R.id.changeBtn);
        flashBtn.setOnClickListener(this);
        changeBtn.setOnClickListener(this);

        if (CameraManager.getInstance(this).getCameraNumbers() < 2) {
            changeBtn.setVisibility(View.GONE);
        }
        table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //            //doNothing 防止聚焦框出现在拍照区域
            }
        });
    }

    /**
     * 三个按钮点击事件
     */
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bnt_takepicture) {
            CameraManager.getInstance(this).takePicture(this);

        } else if (i == R.id.bnt_enter) {
            setResult(RESULT_OK);
            finish();

        } else if (i == R.id.bnt_cancel) {

            deletePicture();

            bntTakePic.setVisibility(View.VISIBLE);
            bntCancel.setVisibility(View.INVISIBLE);
            bntEnter.setVisibility(View.INVISIBLE);

            CameraManager.getInstance(this).startPreview();

        } else if (i == R.id.flashBtn) {
            if (IS_TOOK == 0) {
                CameraManager.getInstance(this).turnLight(new CameraManager.TurnLightListener() {
                    @Override
                    public void callBack(Camera camera) {
                        String flashMode = camera.getParameters().getFlashMode();
                        if (Camera.Parameters.FLASH_MODE_ON.equals(flashMode)) {
                            flashBtn.setImageResource(R.drawable.camera_flash_on);
                        } else if (Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
                            flashBtn.setImageResource(R.drawable.camera_flash_off);
                        } else if (Camera.Parameters.FLASH_MODE_AUTO.equals(flashMode)) {
                            flashBtn.setImageResource(R.drawable.camera_flash_auto);
                        }
                    }
                });
            }

        } else if (i == R.id.changeBtn) {
            if (IS_TOOK == 0) {
                CameraManager.getInstance(this).switchCamera(surfaceViewManager.getSurfaceHolder());
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_CAMERA: // 按下拍照按钮
                if (event.getRepeatCount() == 0) {
                    CameraManager.getInstance(this).takePicture(this);
                }
            case KeyEvent.KEYCODE_BACK:
                if (IS_TOOK == 0)
                    finish();
                else {
                    CameraManager.getInstance(this).startPreview();
                    bntCancel.performClick();
                    return false;
                }
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void pointFocusCallBack(int pointX, int pointY) {
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(focusIndex.getLayoutParams());
        layout.setMargins(pointX - 60, pointY - 60, 0, 0);
        focusIndex.setLayoutParams(layout);
        focusIndex.setVisibility(View.VISIBLE);
        ScaleAnimation sa = new ScaleAnimation(3f, 1f, 3f, 1f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(800);
        focusIndex.startAnimation(sa);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                focusIndex.setVisibility(View.INVISIBLE);
            }
        }, 800);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {

            deletePicture();
            saveToSDCard(data); // 保存图片到sd卡中

            if ("htc".equals(android.os.Build.BRAND)) {

                setResult(RESULT_OK);
                finish();

            } else {

                bntTakePic.setVisibility(View.INVISIBLE);
                bntCancel.setVisibility(View.VISIBLE);
                bntEnter.setVisibility(View.VISIBLE);

            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        data = null;
    }

    /**
     * 将拍下来的照片存放在SD卡中
     *
     * @param data
     * @throws IOException
     */
    public void saveToSDCard(byte[] data) throws IOException {
        File takePhotoFile = new File(TAKE_PHOTO_FILE_PAHT_CATEGORY);
        if (!takePhotoFile.exists()) {
            takePhotoFile.mkdirs();
        }
        File jpgFile = new File(takePhotoFile, photoname);
        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
        outputStream.write(data); // 写入sd卡中
        outputStream.close(); // 关闭输出流
        IS_TOOK = 1;
    }

    /**
     * 当用户不需要这张
     */
    public void deletePicture() {

        File jpgFile = new File(TAKE_PHOTO_FILE_PAHT_CATEGORY, photoname);
        if (jpgFile.exists()) {
            jpgFile.delete();
        }
        IS_TOOK = 0;
    }

}
