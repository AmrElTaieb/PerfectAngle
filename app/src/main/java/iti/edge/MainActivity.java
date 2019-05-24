package iti.edge;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
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

        Mat edges = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        mRgba = inputFrame.rgba();
        Imgproc.cvtColor(mRgba,  mRgba, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(mRgba, mRgba, new Size(5, 5), 0);
          Imgproc.Canny(mRgba, mRgba, 10, 30);
        Imgproc.findContours(mRgba,contours,edges,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);


        Iterator<MatOfPoint> iterator = contours.iterator();
        while (iterator.hasNext()){
            MatOfPoint contour = iterator.next();
            Rect rect ;
            rect = Imgproc.boundingRect(contour);
            //  Imgproc.rectangle(mRgba,new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(255.0,255.0,255.0));

            Imgproc.circle(mRgba,new Point(rect.x,rect.y),5,new Scalar(200,0,0),2);
            Imgproc.circle(mRgba,new Point(rect.x+rect.width,rect.y),5,new Scalar(200,0,0),2);
            Imgproc.circle(mRgba,new Point(rect.x,rect.y+rect.height),5,new Scalar(200,0,0),2);
            Imgproc.circle(mRgba,new Point(rect.x+rect.width,rect.y+rect.height),5,new Scalar(200,0,0),2);


        }






        mRgba.convertTo(mRgbaF,CvType.CV_8U);
        //Return result
        return mRgba;

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

