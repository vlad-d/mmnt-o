package com.dr.vlad.memento.ocr;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Classifier {

    public static final String TAG = Classifier.class.getSimpleName();
    public static final String CLASSIFIER_LOCATION = "mmnt-o/classifier";
    public static final String CLASSIFIER_NAME = "classifier_data";
    private File mClassifierFolder;
    private File mClassifierImage;
    private Mat trainDataMat = new Mat();
    private List<Integer> trainLabels = new ArrayList<>();
    private List<Symbol> symbols;

    public Classifier() {
        loadTrainData();
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public Classifier setSymbols(List<Symbol> symbols) {
        this.symbols = symbols;
        return this;
    }

    private void createClassifierFolder() {
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mClassifierFolder = new File(storageDirectory + "/" + CLASSIFIER_LOCATION);
        if (!mClassifierFolder.exists()) {
            mClassifierFolder.mkdir();
        }
    }

    private void storeTrainData() {

    }

    private void loadTrainData() {
        createClassifierImage();
        Bitmap bmp = BitmapFactory.decodeFile(mClassifierImage.getPath());
        if (null == bmp) {
            return;
        }
        Utils.bitmapToMat(bmp, trainDataMat);
        Log.d(TAG, trainDataMat.dump());

    }

    private void createClassifierImage() {
        createClassifierFolder();
        try {
            mClassifierImage = new File(mClassifierFolder.getPath() + "/" + CLASSIFIER_NAME + ".jpg");
            if (!mClassifierImage.exists()) {
                mClassifierImage.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean train() {
        for (Symbol symbol : symbols) {

        }
    }


}
