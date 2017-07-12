package com.dr.vlad.memento.ocr;


import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageUtils {

    private Mat imgOriginal;
    private Mat imgThresh;

    public ImageUtils(Bitmap bitmap) {
        this.imgOriginal = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, this.imgOriginal);
    }

    private void generateTreshMat() {
        if (null != imgThresh) {
            return;
        }

        Mat imgGrayscale = new Mat();
        Mat imgBlurred = new Mat();
        Imgproc.cvtColor(imgOriginal, imgGrayscale, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(imgGrayscale, imgBlurred, new Size(5, 5), 0);
        Imgproc.threshold(imgGrayscale, imgThresh, 100, 255, Imgproc.THRESH_BINARY_INV);

    }
}
