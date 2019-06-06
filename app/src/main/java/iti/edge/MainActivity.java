package iti.edge;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "perfect-angle";

    protected CameraBridgeViewBase mCamera;

    private Mat mRgba;
    private Mat mRgbaF;
    private Mat mRgbaT;
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

        faceOne = findViewById(R.id.face_one);
        faceOne.setOnClickListener((v)->{
            count = 1;
//            takePicture(mRgba);
        });
        faceTwo = findViewById(R.id.face_two);
        faceTwo.setOnClickListener((v)->{
            count = 2;
        });
        faceThree = findViewById(R.id.face_three);
        faceThree.setOnClickListener((v)->{
            count = 3;
        });
        faceFour = findViewById(R.id.face_four);
        faceFour.setOnClickListener((v)->{
            count = 4;
        });
        faceFive = findViewById(R.id.face_five);
        faceFive.setOnClickListener((v)->{
            count = 5;
        });
        autoCapture = findViewById(R.id.auto_capture);
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
    public Mat onCameraFrame(final CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        Log.i(TAG,"Camera frame iteration ");
        mRgbaF =  runCamera(inputFrame);
        return mRgbaF;
    }

    public Mat runCamera(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        double maxArea=0;
        Point first = new Point(0,0) ,second = new Point(0,0) ;
        Mat edges = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
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
//                Log.i(TAG,"Entered If Condition");
            }
//            Log.i(TAG,"Loop has passed");
        }
//        Log.i(TAG,"Loop has finished");
        Imgproc.rectangle(mRgba,first, second,new Scalar(255.0,255.0,255.0));
        if (first.x >= 250 && first.x <=350   && first.y >=200 && first.y <= 300  &&
               second.x >= 530 && second.x <=650   && second.y >=500 && second.y <= 600)
        {
           Log.i(TAG , "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
           takePicture(mRgba);
        }
//        Log.i(TAG , "First Point"+first.x +" Second Point"+first.y);
//        Log.i(TAG , "Second Point"+second.x +" Second Point"+second.y);
        mRgba.convertTo(mRgbaF,CvType.CV_8U);
        return  mRgbaF ;
    }

    public void takePicture(Mat tmp)
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






















