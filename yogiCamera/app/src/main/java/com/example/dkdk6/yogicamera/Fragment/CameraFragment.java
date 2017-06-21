package com.example.dkdk6.yogicamera.Fragment;

import android.app.Fragment;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.dkdk6.yogicamera.Activity.CameraActivity;
import com.example.dkdk6.yogicamera.R;

/**
 * Created by dkdk6 on 2017-05-17.
 */

public class CameraFragment extends Fragment {
    private ImageButton flashButton, timerButton, gridButton;
    public CameraFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        //버튼 id연결
        flashButton = (ImageButton)v.findViewById(R.id.fragment_flash);
        timerButton = (ImageButton)v.findViewById(R.id.fragment_timer);
        gridButton = (ImageButton)v.findViewById(R.id.fragment_grid);

        //플레시 클릭액션
        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CameraActivity.FlashFlag==0){
                    ((CameraActivity)getActivity()).FlashFlag=1;
                    if(((CameraActivity)getActivity()).cameraView!=null)((CameraActivity)getActivity()).cameraView.flashOn();
                    flashButton.setImageResource(R.drawable.fragment_camera_flash_on);
                }else{
                    ((CameraActivity)getActivity()).FlashFlag=0;
                    if(((CameraActivity)getActivity()).cameraView!=null)((CameraActivity)getActivity()).cameraView.flashOff();
                    flashButton.setImageResource(R.drawable.fragment_camera_flash_off);
                }
            }
        });
        //timer 클릭액션
        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CameraActivity)getActivity()).TimerFlag==1){
                    timerButton.setImageResource(R.drawable.fragment_camera_timer_3s);
                    ((CameraActivity)getActivity()).TimerFlag=3;
                }else if(((CameraActivity)getActivity()).TimerFlag==3){
                    timerButton.setImageResource(R.drawable.fragment_camera_timer_5s);
                    ((CameraActivity)getActivity()).TimerFlag=5;
                }else if(((CameraActivity)getActivity()).TimerFlag==5){
                    timerButton.setImageResource(R.drawable.fragment_camera_timer_10s);
                    ((CameraActivity)getActivity()).TimerFlag=10;
                }else if(((CameraActivity)getActivity()).TimerFlag==10){
                    timerButton.setImageResource(R.drawable.fragment_camera_timer);
                    ((CameraActivity)getActivity()).TimerFlag=1;
                }

            }
        });

        gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((CameraActivity)getActivity()).GridFlag == 0) {
                    ((CameraActivity)getActivity()).GridFlag = 1;
                    ((CameraActivity)getActivity()).line1.setVisibility(View.VISIBLE);
                    ((CameraActivity)getActivity()).line2.setVisibility(View.VISIBLE);
                    ((CameraActivity)getActivity()).line3.setVisibility(View.VISIBLE);
                    ((CameraActivity)getActivity()).line4.setVisibility(View.VISIBLE);
                } else if (((CameraActivity)getActivity()).GridFlag == 1) {
                    ((CameraActivity)getActivity()).GridFlag= 0;
                    ((CameraActivity)getActivity()).line1.setVisibility(View.INVISIBLE);
                    ((CameraActivity)getActivity()).line2.setVisibility(View.INVISIBLE);
                    ((CameraActivity)getActivity()).line3.setVisibility(View.INVISIBLE);
                    ((CameraActivity)getActivity()).line4.setVisibility(View.INVISIBLE);
                }

            }
        });

        return v;
    }

}
