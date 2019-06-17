package iti.edge.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import iti.edge.R;

public class DataStorage {
    private static final String TAG = "perfect-angle";
    Context context;

    public DataStorage(Context context) {
        this.context = context;
    }

    public void takePicture(Mat tmp) {
        Log.i("manual-capture", "takePicture");
        Bitmap bmp;
        try {
            bmp = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(tmp, bmp);
            writeToSDFile(bmp);
        } catch (CvException e) {
            Log.d("Exception", e.getMessage());
        }
        final MediaPlayer captureSoundMediaPlayer = MediaPlayer.create(context,R.raw.shutter);
        captureSoundMediaPlayer.start();
    }

    private void writeToSDFile(Bitmap bitmapImage) {
        Date date = new Date(System.currentTimeMillis());
        String dateString = date.toString();


        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File dir = new File(root + "/PerfectAngle");
        dir.mkdirs();
        Log.i(TAG, "path: " + dir);
        String fname = dateString + "temp.jpg";
        File file = new File(dir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null,
                (path, uri) -> {
                    Log.i(TAG, "Scanned " + path + ":");
                    Log.i(TAG, "-> uri=" + uri);
                });
    }
}
