package com.example.dkdk6.yogicamera.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Image;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dkdk6.yogicamera.CameraSurfaceView;
import com.example.dkdk6.yogicamera.Model.NearBySearchResult;
import com.example.dkdk6.yogicamera.Retrofit.GoogleApiCall;
import com.example.dkdk6.yogicamera.http.ClientHttp;
import com.example.dkdk6.yogicamera.Fragment.CameraFragment;
import com.example.dkdk6.yogicamera.Fragment.ImageListFragment;
import com.example.dkdk6.yogicamera.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CameraActivity extends Activity implements Serializable, ImageListFragment.OnMyListener {
    static int CONTROL_GPS_CODE = 0;
    static int CONTROL_FLAGMENT = 0;
    static int DONT_START_FRAGMENT = 0;
    static int ORIANTATION_FLAG_ONE =0;
    static int  ORIANTATION_FLAG_TWO=0;

    public static String Gpstemp;
    public static String IMAGE_FILE = "capture.jpg";
    public static int FlashFlag = 0, TimerFlag = 1, GridFlag = 0;

    public ImageView overlapImage;
    public FragmentManager fragmentManager = getFragmentManager();
    public FragmentManager fragmentManager2 = getFragmentManager();
    public FragmentTransaction fragmentTranscation, fragmentTranscation2;
    public CameraSurfaceView cameraView,FrontcameraView;
    public View line1, line2, line3, line4;
    public ImageButton stop;
    public Bitmap resizedbitmap;
    private String TAG = "CAMERA";

    private OrientationEventListener orientationEventListener;
    private Context mContext = this;
    private DisplayMetrics dm;
    private int tak, cameraCount = 0, fragmentFlug = 0,width,height,alpha = 30;
    private SoundPool soundpool;
    private ImageButton capture, topSubButton, gallery, up, down;
    private CameraFragment camerafragment = new CameraFragment();
    private ImageListFragment imageListFragment = new ImageListFragment();
    private Bitmap bitmap;

    FrameLayout previewFrame;
    final Context context = this;
    LocationManager manager;
    boolean isGpsEnabled = false;

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "YogiCam");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("YogiCam", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        Log.i("YogiCam", "Saved at" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        return mediaFile;
    }

    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        checkDangerousPermissions();
        //overlapImage.setImageAlpha(30);
        dm = getApplicationContext().getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.heightPixels;
        cameraView = new CameraSurfaceView(getApplicationContext());
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_image_one);
        FrontcameraView = new CameraSurfaceView(getApplicationContext());
        FrontcameraView.getNumber(1, 0);
        previewFrame = (FrameLayout) findViewById(R.id.previewFrame);
        previewFrame.addView(cameraView);
        line1 = (View) findViewById(R.id.line1);
        line2 = (View) findViewById(R.id.line2);
        line3 = (View) findViewById(R.id.line3);
        line4 = (View) findViewById(R.id.line4);
        /*여기서 추가*/
        capture = (ImageButton) findViewById(R.id.camera_button);
        gallery = (ImageButton) findViewById(R.id.camera_gallery);
        up = (ImageButton) findViewById(R.id.up);
        down = (ImageButton) findViewById(R.id.down);
        stop = (ImageButton) findViewById(R.id.stop_button);
        topSubButton = (ImageButton) findViewById(R.id.camera_toggleButton);
        soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        tak = soundpool.load(getApplicationContext(), R.raw.camerasound, 1);
        /*
        * top에 있는 버튼 프레그먼트용
        * */
        topSubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTranscation = fragmentManager.beginTransaction();
                if (fragmentFlug == 0) {
                    fragmentFlug = 1;
                    topSubButton.setImageResource(R.drawable.activity_camera_icon_afterclick);
                    fragmentTranscation.replace(R.id.activity_camera, camerafragment);
                } else {
                    fragmentFlug = 0;
                    topSubButton.setImageResource(R.drawable.activity_camera_icon_beforeclick);
                    fragmentTranscation.remove(camerafragment);
                }
                fragmentTranscation.commit();
            }
        });
        fragmentTranscation2 = fragmentManager2.beginTransaction();
        fragmentTranscation2.add(R.id.activity_camera, imageListFragment);
        fragmentTranscation2.hide(imageListFragment);
        fragmentTranscation2.commit();
        findGPS();
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (cameraCount == 0) {
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cameraView.capture(new Camera.PictureCallback() {
                                @Override
                                public void onPictureTaken(byte[] data, Camera camera) {
                                    soundpool.play(tak, 1, 1, 0, 0, 1);
                                    // JPEG 이미지가 byte[] 형태로 들어옵니다
                                    File pictureFile = getOutputMediaFile();
                                    if (pictureFile == null) {
                                        Toast.makeText(mContext, "Error saving!!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    try {
                                        DONT_START_FRAGMENT = 1;
                                        FileOutputStream fos = new FileOutputStream(pictureFile);
                                        fos.write(data);
                                        Log.i("fos", "" + Uri.fromFile(pictureFile));
                                        Toast.makeText(mContext, "저장", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(CameraActivity.this, ImageResultActivity.class);
                                        intent.putExtra("ImageUri", "" + Uri.fromFile(pictureFile));
                                        startActivity(intent);
                                    } catch (FileNotFoundException e) {
                                        Log.d(TAG, "File not found: " + e.getMessage());
                                    } catch (IOException e) {
                                        Log.d(TAG, "Error accessing file: " + e.getMessage());
                                    }
                                }
                            });
                        }
                    }, (TimerFlag * 1000));
                } else if (cameraCount == 1) {
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FrontcameraView.capture(new Camera.PictureCallback() {
                                @Override
                                public void onPictureTaken(byte[] data, Camera camera) {
                                    // JPEG 이미지가 byte[] 형태로 들어옵니다
                                    soundpool.play(tak, 1, 1, 0, 0, 1);
                                    File pictureFile = getOutputMediaFile();
                                    if (pictureFile == null) {
                                        Toast.makeText(mContext, "Error saving!!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    try {
                                        FileOutputStream fos = new FileOutputStream(pictureFile);
                                        fos.write(data);
                                        Toast.makeText(mContext, "저장", Toast.LENGTH_SHORT).show();
                                        fos.close();
                                    } catch (FileNotFoundException e) {
                                        Log.d(TAG, "File not found: " + e.getMessage());
                                    } catch (IOException e) {
                                        Log.d(TAG, "Error accessing file: " + e.getMessage());
                                    }
                                    Intent intent = new Intent(CameraActivity.this, ImageResultActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    }, (TimerFlag * 1000));
                }
            }
        });
    }

    public void onReceivedData(int data) {
        if (data == 1) {
            makeOverrap();
        }
    }

    public void makeOverrap() {
        fragmentTranscation2 = fragmentManager2.beginTransaction();
        fragmentTranscation2.remove(imageListFragment);
        fragmentTranscation2.commit();
        resize();
        orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int i) {
                up.setVisibility(View.VISIBLE);
                down.setVisibility(View.VISIBLE);
                if (i >= 0 && i <= 40 || i >= 310 && i <= 360) {//세로
                    down.setImageResource(R.drawable.down_l);
                    up.setImageResource(R.drawable.up_s);
                    overlapImage.setVisibility(View.INVISIBLE);
                    if(ORIANTATION_FLAG_TWO==0){
                        overlapImage.setImageBitmap(angleResize(rotateImage(resizedbitmap, 0),10));
                        ORIANTATION_FLAG_TWO=1;
                        ORIANTATION_FLAG_ONE=0;
                    }
                    up.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alpha += 10;
                            if (alpha >= 100) {
                                alpha = 100;
                            }
                            overlapImage.setImageAlpha(alpha);
                        }
                    });
                    down.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alpha -= 10;
                            if (alpha <= 0) {
                                alpha = 0;
                            }
                            overlapImage.setImageAlpha(alpha);
                        }
                    });
                    overlapImage.setVisibility(View.VISIBLE);
                } else if (i >= 180 && i <= 360) {
                        /*
                        여기는 가로 왼쪽회전
                         */
                    overlapImage.setVisibility(View.INVISIBLE);
                    down.setImageResource(R.drawable.down_s);
                    up.setImageResource(R.drawable.up_s);
                    if(ORIANTATION_FLAG_ONE==0){
                        overlapImage.setImageBitmap(angleResize(rotateImage(resizedbitmap, 90),100));
                        ORIANTATION_FLAG_ONE=1;
                        ORIANTATION_FLAG_TWO=0;
                    }
                    up.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alpha += 10;
                            if (alpha >= 100) {
                                alpha = 100;
                            }
                            overlapImage.setImageAlpha(alpha);
                        }
                    });
                    down.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alpha -= 10;
                            if (alpha <= 0) {
                                alpha = 0;
                            }
                            overlapImage.setImageAlpha(alpha);
                        }
                    });
                    overlapImage.setVisibility(View.VISIBLE);
                } else if (i <= 180 && i >= 40) {
                    //오른쪽회전
                    overlapImage.setVisibility(View.INVISIBLE);
                    down.setImageResource(R.drawable.up_s);
                    up.setImageResource(R.drawable.down_s);
                    if(ORIANTATION_FLAG_ONE==0){
                        overlapImage.setImageBitmap(angleResize(rotateImage(resizedbitmap, 270),100));
                        ORIANTATION_FLAG_ONE=1;
                        ORIANTATION_FLAG_TWO=0;
                    }
                    up.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alpha += 10;
                            if (alpha >= 100) {
                                alpha = 100;
                            }
                            overlapImage.setImageAlpha(alpha);
                        }
                    });
                    down.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alpha -= 10;
                            if (alpha <= 0) {
                                alpha = 0;
                            }
                            overlapImage.setImageAlpha(alpha);
                        }
                    });
                    overlapImage.setVisibility(View.VISIBLE);
                }
            }
        };
        orientationEventListener.enable();
        stop.setVisibility(View.VISIBLE);
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                orientationEventListener.disable();
                stop.setVisibility(View.INVISIBLE);
                up.setVisibility(View.INVISIBLE);
                down.setVisibility(View.INVISIBLE);
                overlapImage.setVisibility(View.INVISIBLE);
            }
        });
    }
    /*
    이미지 처리 methods
     */
    public void resize() {
        resizedbitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        if (bitmap != null) {
            overlapImage = (ImageView) findViewById(R.id.selected_image_view);
            overlapImage.setImageBitmap(resizedbitmap);
            overlapImage.setImageAlpha(50);
            overlapImage.setVisibility(View.VISIBLE);
        }
    }

    public Bitmap angleResize(Bitmap bitmap,int angle) {
        //왼쪽회전, 오른쪽회전 들어오면 가로세로사이즈변경
        Bitmap result=null;
        if(angle==10){
            //세로인경우
            result = Bitmap.createScaledBitmap(bitmap, width, height, true);
        }else if(angle==100){
            result = Bitmap.createScaledBitmap(bitmap, width, height, true);
        }
        return result;
    }

    public Bitmap rotateImage(Bitmap src, float degree) {

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    /*GPS검색*/
    public void findGPS() {
        startLocationService();
        checkDangerousPermissions();
    }

    public void startLocationService() {
        // 위치 관리자 객체 참조
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 위치 정보를 받을 리스너 생성
        GPSListener gpsListener = new GPSListener();
        isGpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGpsEnabled == false) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("GPS SETTING");
            alertDialogBuilder
                    .setMessage("GPS가 꺼져있습니다. GPS를 켜시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("NO",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    // 프로그램을 종료한다
                                    dialog.cancel();
                                }
                            })
                    .setNegativeButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    context.startActivity(intent);
                                }
                            });
            // 다이얼로그 생성
            AlertDialog alertDialog = alertDialogBuilder.create();
            // 다이얼로그 보여주기
            alertDialog.show();
        }
        long minTime = 3000000;
        float minDistance = 0;
        if (isGpsEnabled == true) {
            try {
                // GPS를 이용한 위치 요청
                manager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        minTime,
                        minDistance,
                        gpsListener);

                // 네트워크를 이용한 위치 요청
                manager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        minTime,
                        minDistance,
                        gpsListener);
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        }

    }

    public class GPSListener implements LocationListener {
        /**
         * 위치 정보가 확인될 때 자동 호출되는 메소드
         */
        double latitude;
        double longitude;
        int site_index = 0;

        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            String msg = "Latitude : " + latitude + "\nLongitude:" + longitude;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            HashMap<String, String> query = new HashMap<>();
            query.put("location", "" + latitude + "," + longitude);
            query.put("radius", "" + 5000);
            query.put("type", "cafe");
            query.put("key", "AIzaSyDx53WZsD3RAvLA7wTeD4WqYK4IhQojuVs");
            retrofit.create(GoogleApiCall.class)
                    .getNearBySearchResult(query)
                    .enqueue(new Callback<NearBySearchResult>() {
                        @Override
                        public void onResponse(Call<NearBySearchResult> call, Response<NearBySearchResult> response) {
                            if (DONT_START_FRAGMENT != 1) {
                                fragmentTranscation2 = fragmentManager2.beginTransaction();
                                fragmentTranscation2.show(imageListFragment);
                                fragmentTranscation2.commit();
                            }
                            if (CONTROL_GPS_CODE == 0) {
                                for (int i = 0; i < response.body().results.size(); i++) {
                                    if (response.body().results.get(i).photos == null) {
                                        Log.v("dkdk6638", "도경의도전");
                                    } else if (response.body().results.get(i).photos != null && site_index < 5) {
                                        getLocation(latitude, longitude);
                                        String ref = response.body().results.get(i).photos.get(0).photo_reference;
                                        Glide.with(imageListFragment).load("https://maps.googleapis.com/maps/api/place/photo?maxheight=400&maxwidth=400&photoreference=" + ref + "&key=AIzaSyDx53WZsD3RAvLA7wTeD4WqYK4IhQojuVs")
                                                .bitmapTransform(new CropSquareTransformation(getApplicationContext()))
                                                .into(imageListFragment.imagelist[site_index]);
                                        site_index++;
                                    }
                                }
                                CONTROL_FLAGMENT = 1;
                            }
                        }

                        @Override
                        public void onFailure(Call<NearBySearchResult> call, Throwable t) {

                        }
                    });
        }

        public void getLocation(double lat, double lng) {
            String str = null;
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.KOREA);
            List<Address> address;
            try {
                if (geocoder != null) {
                    address = geocoder.getFromLocation(lat, lng, 1);
                    if (address != null && address.size() > 0) {
                        str = address.get(0).getAddressLine(0).toString();
                    }
                }
            } catch (IOException e) {
                Log.e("MainActivity", "주소를 찾지 못하였습니다.");
                e.printStackTrace();
            }
            String[] data = str.split(" ");
            Gpstemp = data[0] + " " + data[1] + " " + data[2] + " " + data[3];
            Log.i("GPS", Gpstemp);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    public void gallery_click(View v) {
        final int REQ_CODE_SELECT_IMAGE = 100;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}