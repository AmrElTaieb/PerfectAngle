package iti.edge;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
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

import iti.edge.data.DataStorage;

import static iti.edge.CameraActivity.imageView;

public class CarAngles extends Activity {
    private Context context;
    private Mat mRgba;
    private DataStorage dataStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void setMat(Mat mRgba) {
        this.mRgba = mRgba;
    }

    public CarAngles(Context context) {
        this.context = context;
        dataStorage = new DataStorage(context);

    }

    private static final String TAG = "perfect-angle";

    public Mat checkSideViewAngle(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat savedImage = new Mat();
        inputFrame.rgba().copyTo(savedImage);
        Mat input = inputFrame.gray();
        Mat circles = new Mat();
        Imgproc.blur(input, input, new Size(7, 7), new Point(2, 2));
        Imgproc.HoughCircles(input, circles, Imgproc.CV_HOUGH_GRADIENT, 2, 200, 100, 90, 70, 1000);
        //  Log.i(TAG, String.valueOf("size: " + circles.cols()) + ", " + String.valueOf(circles.rows()));
        double x1 = 0, x2 = 0, y1 = 0, y2 = 0;  // coordinates for the circle
        double r1 = 0, r2 = 0;           // radius of the circle
        if (circles.cols() == 2) {
            for (int x = 0; x < Math.min(circles.cols(), 5); x++) // so it doesnt catch more than 5 circles , in this case always 2
            {
                double circleVec[] = circles.get(0, x);
                if (circleVec == null) {
                    break;
                }
                if (x == 0) {
                    x1 = circleVec[0];
                    y1 = circleVec[1];
                    r1 = circleVec[2];

                } else if (x == 1) {
                    x2 = circleVec[0];
                    y2 = circleVec[1];
                    r2 = circleVec[2];
                }
                Log.i(TAG, "circle vec 0: " + circleVec[0] + " cirlce vec 1 :  " + circleVec[1] + " circle vec 2 :" + circleVec[2]);


            }
            if (y1 >= y2 - 35 && y1 <= y2 + 35) {
                Imgproc.circle(input, new Point(x1, y1), (int) r1, new Scalar(255, 255, 255), 2);
                Imgproc.circle(input, new Point(x2, y2), (int) r2, new Scalar(255, 255, 255), 2);
                dataStorage.takePicture(savedImage);
            }
        }

        circles.release();
        input.release();
        return inputFrame.rgba();
    }

    public Mat checkBackViewAngle(Mat inputFrame) {

       // mRgba = inputFrame;
        Mat savedImage= new Mat();
        inputFrame.copyTo(savedImage);
        float density = context.getResources().getDisplayMetrics().density;
        Log.i("qwerty", "Density " + density);
        float left = imageView.getLeft() / density;
        float top = imageView.getTop() / density;
        float right = imageView.getRight() / density;
        float bottom = imageView.getBottom() / density;
        Log.i("qwerty", "Left " + left);
        Log.i("qwerty", "Top  " + top);
        Log.i("qwerty", "Right " + right);
        Log.i("qwerty", "Bottom " + bottom);
        float imageArea = right * bottom;

        double maxArea = 0;
        Point first = new Point(0, 0), second = new Point(0, 0);
        Mat edges = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        //   mRgba = inputFrame;
        //  mRgba = inputFrame.rgba();
        Imgproc.cvtColor(inputFrame, inputFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(inputFrame, inputFrame, new Size(5, 5), 0);
        Imgproc.Canny(inputFrame, inputFrame, 15, 45);
        Imgproc.findContours(inputFrame, contours, edges, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Iterator<MatOfPoint> iterator = contours.iterator();
        Rect rect;
        while (iterator.hasNext()) {
            MatOfPoint contour = iterator.next();
            rect = Imgproc.boundingRect(contour);
            if (rect.area() > maxArea) {
                maxArea = rect.area();
                first = new Point(rect.x, rect.y);
                second = new Point(rect.x + rect.width, rect.y + rect.height);

            }
        }

        Imgproc.rectangle(inputFrame, first, second, new Scalar(255.0, 255.0, 255.0));
        double contourArea = (second.x - first.x) * (second.y - first.y);
        if (CameraActivity.getSwitchState()) {
            if (imageArea >= contourArea - 40000 && imageArea <= contourArea + 40000) {
                Log.i(TAG, "Photo captured (Success)");
                dataStorage.takePicture(savedImage);
            }
        }
        Log.i(TAG, "Area of contour = " + contourArea);
        Log.i(TAG, "Area of image = " + imageArea);
        Log.i(TAG, "Area of Screnn = " + (1196 * 720));
        Log.i(TAG, "First Point X" + first.x + " First Point Y" + first.y);
        Log.i(TAG, "Second Point X" + second.x + " Second Point Y" + second.y);
        // mRgba.convertTo(mRgbaF,CvType.CV_8U);
        return mRgba;
    }


}
