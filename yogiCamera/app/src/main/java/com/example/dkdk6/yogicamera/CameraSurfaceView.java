package com.example.dkdk6.yogicamera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera camera = null;
    private int cameraInfo=0;
    private int timerInfo = 0;

    public void getNumber(int a, int timer){
        cameraInfo=a;
        timerInfo=timer;
    }

    public CameraSurfaceView(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open(cameraInfo);
        try {
            // Camera.Parameter 얻어오기
            Camera.Parameters mParameters= camera.getParameters();
            List<Camera.Size> supportedPreviewSizes = mParameters.getSupportedPreviewSizes();
            Camera.Size previewSize = mParameters.getPreviewSize();
            List<Camera.Size> supportedPictureSizes = mParameters.getSupportedPictureSizes();
            Camera.Size pictureSize = mParameters.getPictureSize();
            mParameters.setPreviewSize(720,480);
            camera.setParameters(mParameters);
            camera.setPreviewDisplay(mHolder);
        } catch (Exception e) {
            Log.e("CameraSurfaceView", "Failed to set camera preview.", e);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.setDisplayOrientation(90); //방향
        camera.startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public boolean capture(final Camera.PictureCallback handler) {
        if (camera != null&&timerInfo==0) {
            camera.autoFocus (new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                    if(success){
                        camera.takePicture(null, null, handler);
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    public void flashOn(){
        Camera.Parameters param = camera.getParameters();
        param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(param);
    }

    public void flashOff(){
        Camera.Parameters param = camera.getParameters();
        param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(param);
    }

}
