package com.example.dkdk6.yogicamera.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.example.dkdk6.yogicamera.R;

/**
 * Created by dkdk6 on 2017-05-20.
 */

public class ImageListFragment extends Fragment {
    //public ImageView imageTest;
    public ImageView[] imagelist = new ImageView[5];
    private ImageButton cancelButton;
    public interface OnMyListener{
        void onReceivedData(int data);
    }

    private OnMyListener mOnmyListener;
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(getActivity()!=null&&getActivity() instanceof OnMyListener){
            mOnmyListener = (OnMyListener) getActivity();
        }
    }

    public ImageListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_imgaelist, container, false);
        cancelButton = (ImageButton) v.findViewById(R.id.cancel_button);
        imagelist[0] = (ImageView) v.findViewById(R.id.imageListTesting_one);
        imagelist[1] = (ImageView) v.findViewById(R.id.imageListTesting_two);
        imagelist[2] = (ImageView) v.findViewById(R.id.imageListTesting_three);
        imagelist[3] = (ImageView) v.findViewById(R.id.imageListTesting_four);
        imagelist[4] = (ImageView) v.findViewById(R.id.imageListTesting_five);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  onImageListener.OnImageListener(3,5);

                getActivity().getFragmentManager().beginTransaction().remove(ImageListFragment.this).commit();
            }
        });
        imagelist[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //끄고 이미지창띄우기
                //Glide.with(ImageListFragment.this).load("https://maps.googleapis.com/maps/api/place/photo?maxheight=400&maxwidth=400&photoreference=" + ref + "&key=AIzaSyDx53WZsD3RAvLA7wTeD4WqYK4IhQojuVs").into(imagelist[0]);
                if(mOnmyListener != null){
                    mOnmyListener.onReceivedData(1);
                }
                Log.e("버튼클릭", "0번째");
            }
        });

        imagelist[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnmyListener != null){
                    mOnmyListener.onReceivedData(1);
                }
                Log.e("버튼클릭", "1번째");
            }
        });
        imagelist[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnmyListener != null){
                    mOnmyListener.onReceivedData(1);
                }
                Log.e("버튼클릭", "1번째");
            }
        });
        imagelist[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnmyListener != null){
                    mOnmyListener.onReceivedData(1);
                }
                Log.e("버튼클릭", "1번째");
            }
        });
        imagelist[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnmyListener != null){
                    mOnmyListener.onReceivedData(1);
                }
                Log.e("버튼클릭", "1번째");
            }
        });
        return v;
    }
}
