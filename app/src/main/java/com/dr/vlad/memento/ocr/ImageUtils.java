package com.dr.vlad.memento.ocr;


import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class ImageUtils {

    public static final String TAG = ImageUtils.class.getSimpleName();
    public static final int RESIZED_IMAGE_WIDTH = 200;
    public static final int RESIZED_IMAGE_HEIGHT = 300;
    public static final int SYMBOL_IMAGE_WIDTH = 20;
    public static final int SYMBOL_IMAGE_HEIGHT = 30;
    public static final int MIN_CONTOUR_AREA = 500;

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
        imgThresh = new Mat();
        Imgproc.cvtColor(imgOriginal, imgGrayscale, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(imgGrayscale, imgBlurred, new Size(5, 5), 0);
        Imgproc.threshold(imgBlurred, imgThresh, 100, 255, Imgproc.THRESH_BINARY_INV);

    }

    public ArrayList<MatOfPoint> getContours() {
        ArrayList<MatOfPoint> ptContours = new ArrayList<MatOfPoint>();   // contours vector
        ArrayList<MatOfPoint> validContours = new ArrayList<MatOfPoint>();   // contours vector
        Mat hierarchy = new Mat();        //contours hierarchy
        generateTreshMat();
        Mat imgThreshCopy = imgThresh.clone();
        Imgproc.findContours(imgThreshCopy, ptContours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (MatOfPoint contour : ptContours) {
            if (Imgproc.contourArea(contour) >= MIN_CONTOUR_AREA) {
                validContours.add(contour);
            }
        }

        return validContours;
    }

    public ArrayList<Symbol> getSymbols() {
        ArrayList<Symbol> symbols = new ArrayList<>();
        ArrayList<MatOfPoint> contours = getContours();
        for (MatOfPoint contour : contours) {
            symbols.add(getSymbolFromContour(contour));
        }

        return symbols;
    }

    protected Symbol getSymbolFromContour(MatOfPoint contour) {
        Mat treshCopy = imgThresh.clone();
        Rect boundingRect = Imgproc.boundingRect(contour);
        Mat matRoi = new Mat(treshCopy, boundingRect);
        Imgproc.resize(matRoi, matRoi, new Size(RESIZED_IMAGE_WIDTH, RESIZED_IMAGE_HEIGHT));

        return new Symbol(matRoi);
    }


}
