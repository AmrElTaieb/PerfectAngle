package iti.edge;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import iti.edge.data.DataStorage;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "perfect-angle";
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Context cameraContext;
    private CarAngles carAngles;
    static ImageView imageView;
    protected CameraBridgeViewBase mCamera;
    private int mobileHeight, mobileWidth;

    private Mat mRgba;
    private Mat mRgbaF;
    private Mat mRgbaT;

    private Point detectionPoint;
    private int detectionArea;
    private static int count = 0;

    private Button faceOne;
    private Button faceTwo;
    private Button faceThree;
    private Button faceFour;
    private Button manualCapture;
    private static Switch autoCapture;
    private boolean flag = false;

    public static boolean getSwitchState() {
        if (autoCapture.isChecked()) {
            return true;
        } else {
            return false;
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mCamera.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraContext = getApplicationContext();
        getMobileDimensions();
        if (!checkPermission()) {
            requestPermission();
        }
        setContentView(R.layout.activity_main);
        Log.i(TAG, "called onCreate");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCamera = findViewById(R.id.OpenCVCamera);
        mCamera.setVisibility(SurfaceView.VISIBLE);
        mCamera.setCvCameraViewListener(this);
        carAngles = new CarAngles(cameraContext);


        imageView = findViewById(R.id.imageView);
        detectionPoint = new Point(1, 1);
        detectionArea = 1;
        count = 1;

        faceOne = findViewById(R.id.face_one);
        faceOne.setPressed(true);
        faceOne.setOnClickListener((v) -> {
            detectionPoint = new Point(1, 1);
            detectionArea = 1;
            count = 1;

        });
        faceOne.setOnTouchListener((v, event) -> {
            faceOne.setPressed(true);
            if (faceTwo.isPressed()) {
                faceTwo.setPressed(false);
            } else if (faceThree.isPressed()) {
                faceThree.setPressed(false);
            } else {
                faceFour.setPressed(false);
            }
            return true;
        });
        faceTwo = findViewById(R.id.face_two);
        faceTwo.setOnClickListener((v) -> {
            detectionPoint = new Point(2, 2);
            detectionArea = 2;
            count = 2;
        });
        faceTwo.setOnTouchListener((v, event) -> {
            faceTwo.setPressed(true);
            if (faceOne.isPressed()) {
                faceOne.setPressed(false);
            } else if (faceThree.isPressed()) {
                faceThree.setPressed(false);
            } else {
                faceFour.setPressed(false);
            }
            return true;
        });
        faceThree = findViewById(R.id.face_three);
        faceThree.setOnClickListener((v) -> {
            detectionPoint = new Point(3, 3);
            detectionArea = 3;
            count = 3;
        });
        faceThree.setOnTouchListener((v, event) -> {
            faceThree.setPressed(true);
            if (faceOne.isPressed()) {
                faceOne.setPressed(false);
            } else if (faceTwo.isPressed()) {
                faceTwo.setPressed(false);
            } else {
                faceFour.setPressed(false);
            }
            return true;
        });
        faceFour = findViewById(R.id.face_four);
        faceFour.setOnClickListener((v) -> {
            detectionPoint = new Point(4, 4);
            detectionArea = 4;
            count = 4;
        });
        faceFour.setOnTouchListener((v, event) -> {
            faceFour.setPressed(true);
            if (faceOne.isPressed()) {
                faceOne.setPressed(false);
            } else if (faceTwo.isPressed()) {
                faceTwo.setPressed(false);
            } else {
                faceThree.setPressed(false);
            }
            return true;
        });
        manualCapture = findViewById(R.id.manual_capture);
        manualCapture.setOnClickListener((v)->{
            DataStorage dataStorage = new DataStorage(this);
            dataStorage.takePicture(mRgba);
        });
        autoCapture = findViewById(R.id.auto_capture);
        autoCapture.setChecked(false);
    }

    public void getMobileDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mobileHeight = displayMetrics.heightPixels;
        mobileWidth = displayMetrics.widthPixels;
        Log.i(TAG, "Width " + mobileWidth);
        Log.i(TAG, "Height " + mobileHeight);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(height, width, CvType.CV_8UC4);
        carAngles.setMat(mRgba);
        Log.i(TAG, "onCameraViewStarted");


    }

    public void changeCarPic(int x) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (x == 1) {
                    imageView.setImageResource(R.drawable.backviewl);
                } else {
                    imageView.setImageResource(R.drawable.sideview);
                }

            }
        });
    }

    @Override
    public void onCameraViewStopped() {
        Log.i(TAG, "onCameraViewStopped");
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        Mat result = new Mat();
        if (count < 5){
            if (faceOne.isPressed()) {
                changeCarPic(1);
                //   mRgbaF = checkBackViewAngle(frameToMat, frameToMat2);
                // mRgbaF = carAngles.checkBackViewAngle(frameToMat);
                //  result = inputFrame.rgba();
                Thread one = new Thread(()->{
                    count++;
                    Mat res = carAngles.checkBackViewAngle(inputFrame.rgba());
                });
                one.start();
            }
            if (faceTwo.isPressed()) {
                changeCarPic(2);
                Thread one = new Thread(()->{
                    count++;
                    Mat res = carAngles.checkSideViewAngle(inputFrame);
                });
                one.start();
            }
        }


//        if(flag==true) {
//            takePicture(inputFrame.rgba());
//            flag=false;
//        }

        return inputFrame.rgba();
        //return mRgbaF ;
    }

    private boolean checkPermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(CameraActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onPause() {
        //Disable camera
        super.onPause();
        if (mCamera != null)
            mCamera.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mCamera != null)
            mCamera.disableView();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}






















