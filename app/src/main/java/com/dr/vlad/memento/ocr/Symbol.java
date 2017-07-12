package com.dr.vlad.memento.ocr;


import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.MatOfPoint;

public class Symbol {
    private MatOfPoint matSymbol;
    private int intSymbol;

    public Symbol(MatOfPoint matSymbol) {
        this.matSymbol = matSymbol;
    }

    public MatOfPoint getMatSymbol() {
        return matSymbol;
    }

    public Symbol setMatSymbol(MatOfPoint matSymbol) {
        this.matSymbol = matSymbol;
        return this;
    }

    public int getIntSymbol() {
        return intSymbol;
    }

    public Symbol setIntSymbol(int intSymbol) {
        this.intSymbol = intSymbol;
        return this;
    }

    public Bitmap toBitmap() {
        Bitmap result = Bitmap.createBitmap(matSymbol.cols(), matSymbol.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matSymbol, result);
        return result;
    }
}
