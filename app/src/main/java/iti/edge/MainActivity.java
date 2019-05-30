package iti.edge;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    Mat mRgba;
    Mat mRgbaF;
    Mat mRgbaT;

    protected CameraBridgeViewBase mCamera;

    //Create load callback
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("loading openCv", "OpenCV loaded successfully");
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
        Log.i("onCreate", "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCamera = (CameraBridgeViewBase) findViewById(R.id.OpenCVCamera);
        mCamera.setVisibility(SurfaceView.VISIBLE);
        mCamera.setCvCameraViewListener(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Log.i("zzzzzzzz", "Width " + width);
        Log.i("zzzzzzzz", "height " + height);

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        Log.i("camera started","");
    }

    @Override
    public void onCameraViewStopped() {
        Log.i("camera stopped","");
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Log.i("loop","Camera frame iteration ");

         runCamera(inputFrame);
//        //Return result
        return mRgbaF;

    }

    public void runCamera(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
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

                Log.i("Condition","Entered If Condition");
            }

            Log.i("iteration","Loop has passed");
//            Imgproc.circle(mRgba,new Point(rect.x,rect.y),5,new Scalar(200,0,0),2);
//            Imgproc.circle(mRgba,new Point(rect.x+rect.width,rect.y),5,new Scalar(200,0,0),2);
//            Imgproc.circle(mRgba,new Point(rect.x,rect.y+rect.height),5,new Scalar(200,0,0),2);
//            Imgproc.circle(mRgba,new Point(rect.x+rect.width,rect.y+rect.height),5,new Scalar(200,0,0),2);
        }
        Log.i("finish","Loop has finished");
        Imgproc.rectangle(mRgba,first, second,new Scalar(255.0,255.0,255.0));
         Log.i("coordinates" , "First Point"+first.x +" Second Point"+first.y);
        Log.i("coordinates" , "Second Point"+second.x +" Second Point"+second.y);



        mRgba.convertTo(mRgbaF,CvType.CV_8U);
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

