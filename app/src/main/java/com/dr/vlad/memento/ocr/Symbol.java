package com.dr.vlad.memento.ocr;


import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class Symbol {
    private Mat matSymbol;
    private Integer intSymbol;

    public Symbol(Mat matSymbol) {
        this.matSymbol = matSymbol;
    }

    public Mat getMatSymbol() {
        return matSymbol;
    }

    public Symbol setMatSymbol(Mat matSymbol) {
        this.matSymbol = matSymbol;
        return this;
    }

    public Integer getIntSymbol() {
        return intSymbol;
    }

    public Symbol setIntSymbol(Integer intSymbol) {
        this.intSymbol = intSymbol;
        return this;
    }

    public Bitmap toBitmap() {
        Bitmap result = Bitmap.createBitmap(matSymbol.cols(), matSymbol.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matSymbol, result);
        return result;
    }

    public String getCharacter() {
        char character = (char) intSymbol.intValue();
        return String.valueOf(character);

    }
}
