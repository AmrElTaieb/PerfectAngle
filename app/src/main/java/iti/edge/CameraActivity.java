package iti.edge;

import android.Manifest;
import android.app.Activity;
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
import java.util.Iterator;
import java.util.List;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "perfect-angle";
    private static final int PERMISSION_REQUEST_CODE = 200;

    ImageView imageView;
    protected CameraBridgeViewBase mCamera;

    private Mat mRgba;
    private Mat mRgbaF;
    private Mat mRgbaT;

    private Point detectionPoint;
    private int detectionArea;
    private int count = 0;

    private Button faceOne;
    private Button faceTwo;
    private Button faceThree;
    private Button faceFour;
    private Button faceFive;
    private Switch autoCapture;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mCamera.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!checkPermission())
        {
            requestPermission();
        }


        setContentView(R.layout.activity_main);
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCamera = (CameraBridgeViewBase) findViewById(R.id.OpenCVCamera);
        mCamera.setVisibility(SurfaceView.VISIBLE);
        mCamera.setCvCameraViewListener(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Log.i(TAG, "Width " + width);
        Log.i(TAG, "Height " + height);

        imageView = (ImageView) findViewById(R.id.imageView); //doaa bet7b el casting
        detectionPoint = new Point(1, 1);
        detectionArea = 1;
        count = 1;

        faceOne = findViewById(R.id.face_one);
        faceOne.setPressed(true);
        faceOne.setOnClickListener((v)->{
            detectionPoint = new Point(1, 1);
            detectionArea = 1;
            count = 1;

        });
        faceOne.setOnTouchListener((v, event) -> {
            faceOne.setPressed(true);
            if(faceTwo.isPressed())
            {
                faceTwo.setPressed(false);
            } else if(faceThree.isPressed())
            {
                faceThree.setPressed(false);
            } else if(faceFour.isPressed())
            {
                faceFour.setPressed(false);
            } else
            {
                faceFive.setPressed(false);
            }
            return true;
        });
        faceTwo = findViewById(R.id.face_two);
        faceTwo.setOnClickListener((v)->{
            detectionPoint = new Point(2, 2);
            detectionArea = 2;
            count = 2;
        });
        faceTwo.setOnTouchListener((v, event) -> {
            faceTwo.setPressed(true);
            if(faceOne.isPressed())
            {
                faceOne.setPressed(false);
            } else if(faceThree.isPressed())
            {
                faceThree.setPressed(false);
            } else if(faceFour.isPressed())
            {
                faceFour.setPressed(false);
            } else
            {
                faceFive.setPressed(false);
            }
            return true;
        });
        faceThree = findViewById(R.id.face_three);
        faceThree.setOnClickListener((v)->{
            detectionPoint = new Point(3, 3);
            detectionArea = 3;
            count = 3;
        });
        faceThree.setOnTouchListener((v, event) -> {
            faceThree.setPressed(true);
            if(faceOne.isPressed())
            {
                faceOne.setPressed(false);
            } else if(faceTwo.isPressed())
            {
                faceTwo.setPressed(false);
            } else if(faceFour.isPressed())
            {
                faceFour.setPressed(false);
            } else
            {
                faceFive.setPressed(false);
            }
            return true;
        });
        faceFour = findViewById(R.id.face_four);
        faceFour.setOnClickListener((v)->{
            detectionPoint = new Point(4, 4);
            detectionArea = 4;
            count = 4;
        });
        faceFour.setOnTouchListener((v, event) -> {
            faceFour.setPressed(true);
            if(faceOne.isPressed())
            {
                faceOne.setPressed(false);
            } else if(faceTwo.isPressed())
            {
                faceTwo.setPressed(false);
            } else if(faceThree.isPressed())
            {
                faceThree.setPressed(false);
            } else
            {
                faceFive.setPressed(false);
            }
            return true;
        });
        faceFive = findViewById(R.id.face_five);
        faceFive.setOnClickListener((v)->{
            detectionPoint = new Point(5, 5);
            detectionArea = 5;
            count = 5;
            faceFive.setPressed(true);
        });
        faceFive.setOnTouchListener((v, event) -> {
            faceFive.setPressed(true);
            if(faceOne.isPressed())
            {
                faceOne.setPressed(false);
            } else if(faceTwo.isPressed())
            {
                faceTwo.setPressed(false);
            } else if(faceThree.isPressed())
            {
                faceThree.setPressed(false);
            } else
            {
                faceFour.setPressed(false);
            }
            return true;
        });
        autoCapture = findViewById(R.id.auto_capture);
        autoCapture.setChecked(true);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        Log.i(TAG,"onCameraViewStarted");


    }

    @Override
    public void onCameraViewStopped() {
        Log.i(TAG,"onCameraViewStopped");
    }

    @Override
    public Mat onCameraFrame( CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat result=new Mat() ;
        Mat frameToMat= inputFrame.rgba();
        if(faceOne.isPressed()) {
          //  mRgbaF = checkBackViewAngle(frameToMat);
            mRgbaF = checkBackViewAngle(inputFrame);
            result=inputFrame.rgba();
        }
        if(faceTwo.isPressed()) {
             result = checkSideViewAngle(inputFrame);
        }
      //  return result;
       return mRgbaF ;
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

    private Mat checkSideViewAngle(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        Mat input = inputFrame.gray();
        Mat circles = new Mat();
        Imgproc.blur(input, input, new Size(7, 7), new Point(2, 2));
        Imgproc.HoughCircles(input, circles, Imgproc.CV_HOUGH_GRADIENT, 2, 200, 100, 90, 70, 1000);

      //  Log.i(TAG, String.valueOf("size: " + circles.cols()) + ", " + String.valueOf(circles.rows()));

        double x1=0,x2=0,y1=0,y2=0;  // coordinates for the circle
        double r1=0,r2=0 ;           // radius of the circle
        if (circles.cols() == 2) {
            for (int x=0; x < Math.min(circles.cols(), 5); x++ ) // so it doesnt catch more than 5 circles , in this case always 2
            {
           //     Log.i(TAG,"Min of "+circles.cols() +" and 5 is "+ Math.min(circles.cols(), 5));
                double circleVec[] = circles.get(0, x);


                if (circleVec == null) {
                    break;
                }

                if(x == 0)
                {
                    x1 = circleVec[0];
                    y1 = circleVec[1];
                    r1 = circleVec[2];

                }
                else if (x == 1 )
                {
                    x2 = circleVec[0];
                    y2 = circleVec[1];
                    r2 = circleVec[2];
                }

                Log.i(TAG, "circle vec 0: " + circleVec[0] + " cirlce vec 1 :  " + circleVec[1] +" circle vec 2 :"+circleVec[2]);

               // Imgproc.circle(input, center, 3, new Scalar(255, 255, 255), 5);
               // Imgproc.circle(input, center, radius, new Scalar(255, 255, 255), 2);
            }
           if(y1 >= y2 - 35 && y1 <= y2+35 )

            {
                Imgproc.circle(input, new Point(x1, y1), (int) r1, new Scalar(255, 255, 255), 2);
                Imgproc.circle(input, new Point(x2, y2), (int) r2, new Scalar(255, 255, 255), 2);
            }
        }

        circles.release();
        input.release();
        return inputFrame.rgba();
    }

    private Mat checkBackViewAngle( CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
      //getting coordinates of the image on screen
//        int[] values = new int[2]; //top left corner
//        imageView.getLocationOnScreen(values);

//        int widthDp = (int)(imageView.getWidth() / density); // X of the right point
//        int leftDp = (int)(imageView.getLeft() / density);   //left point
//        int heightDp = (int)(imageView.getHeight() / density);
//       // Log.i("qwerty"," X top left "+values[0]+" Y top left "+values[1] + leftDp +" heightDp =  " +heightDp) ;
//        Log.i("qwerty","value[0] = "+values[0]);
//        Log.i("qwerty","value[1] = "+values[1] +" and value[1]+ leftDp = "+ values[1] + leftDp);
//        Log.i("qwerty","leftDp = "+leftDp);
//        Log.i("qwerty","widthDp = "+widthDp);
//        Log.i("qwerty","heightDp = "+heightDp);

        float density = getResources().getDisplayMetrics().density;
        Log.i("qwerty","Density " + density);
        float left = imageView.getLeft() /density;
        float top = imageView.getTop()/density;
        float right = imageView.getRight() /density;
        float bottom = imageView.getBottom()/density;
        Log.i("qwerty","Left " +left);
        Log.i("qwerty","Top  " +top);
        Log.i("qwerty","Right " +right);
        Log.i("qwerty","Bottom " +bottom);
        float imageArea = right * bottom ;





        double maxArea=0;
        Point first = new Point(0,0) ,second = new Point(0,0) ;
        Mat edges = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
     //   mRgba = inputFrame;
        mRgba = inputFrame.rgba();
        Imgproc.cvtColor(mRgba,  mRgba, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(mRgba, mRgba, new Size(5, 5), 0);
        Imgproc.Canny(mRgba, mRgba, 30, 90);
        Imgproc.findContours(mRgba,contours,edges,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        Iterator<MatOfPoint> iterator = contours.iterator();
        Rect rect ;
        while (iterator.hasNext()){
            MatOfPoint contour = iterator.next();
            rect = Imgproc.boundingRect(contour);
            if(rect.area() > maxArea)
            {
                maxArea = rect.area() ;
                first  = new Point(rect.x,rect.y);
                second = new Point(rect.x+rect.width,rect.y+rect.height);

            }
        }

        Imgproc.rectangle(mRgba,first, second,new Scalar(255.0,255.0,255.0));
        double contourArea = (second.x - first.x ) * (second.y - first.y) ;
        if(autoCapture.isChecked())
        {


            if (imageArea >= contourArea -40000 && imageArea <= contourArea + 40000)
            {
                Log.i(TAG , "Photo captured (Success)");
                takePicture(mRgba);
            }
        }
        Log.i(TAG,"Area of contour = "+contourArea);
        Log.i(TAG,"Area of image = "+imageArea);
        Log.i(TAG,"Area of Screnn = "+ (1196*720));
        Log.i(TAG , "First Point X"+first.x +" First Point Y"+first.y);
        Log.i(TAG , "Second Point X"+second.x +" Second Point Y"+second.y);
       // mRgba.convertTo(mRgbaF,CvType.CV_8U);
     return mRgba ;
    }

    private void takePicture(Mat tmp)
    {
        Bitmap bmp ;
        try {
            bmp = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(tmp, bmp);

            writeToSDFile(bmp);
        }
        catch (CvException e){Log.d("Exception",e.getMessage());}
    }

    private void writeToSDFile(Bitmap bitmapImage)
    {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File dir = new File(root + "/PerfectAngle");
        dir.mkdirs();
        Log.i(TAG , "path: " + dir);
        String fname = "temp.jpg";
        File file = new File(dir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                (path, uri) -> {
                    Log.i(TAG, "Scanned " + path + ":");
                    Log.i(TAG, "-> uri=" + uri);
                });
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






















