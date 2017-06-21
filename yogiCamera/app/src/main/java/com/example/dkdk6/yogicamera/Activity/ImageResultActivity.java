package com.example.dkdk6.yogicamera.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.dkdk6.yogicamera.R;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by dkdk6 on 2017-05-26.
 */

public class ImageResultActivity extends AppCompatActivity {
    public static ImageView imageResult;
    public static String path;
    public Uri myUri;
    public Bitmap receiveBitmap;
    public int rotationAngle=90;
    private EditText gpsInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagetag);
        CameraActivity.DONT_START_FRAGMENT=0;
        gpsInfo = (EditText)findViewById(R.id.editText);
        if(CameraActivity.Gpstemp!=null){
            Log.i("GPS이미지결과정보",CameraActivity.Gpstemp);
            gpsInfo.setText("#"+CameraActivity.Gpstemp);
        }
        Intent intent = getIntent();
        path = intent.getStringExtra("ImageUri");
        imageResult = (ImageView) findViewById(R.id.capture_image);
        if(path!=null&&!path.isEmpty()){
            path = "file://"+path;
            myUri = Uri.parse(path);
            try {
                receiveBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), myUri);
                ImageResize(receiveBitmap);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void ImageResize(Bitmap bitmap) {
        int width = (int) (getWindowManager()).getDefaultDisplay().getWidth();
        Bitmap resizedbitmap = Bitmap.createScaledBitmap(bitmap, width, width, true);
        //이미지회전
        resizedbitmap=rotateImage(resizedbitmap,90);
        imageResult.setImageBitmap(resizedbitmap);
    }
    public Bitmap rotateImage(Bitmap src, float degree) {
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }


}
