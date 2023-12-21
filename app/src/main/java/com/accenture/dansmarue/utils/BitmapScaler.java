package com.accenture.dansmarue.utils;

import android.graphics.Bitmap;

public class BitmapScaler {

    private BitmapScaler() {
        // Avoid instantiation of the class
    }

    public static Bitmap scaleToFitTheGoodOne(Bitmap b) {

        if (b.getWidth() >= b.getHeight()) {
            return scaleToFitWidth(b, 800);

        } else {
            return scaleToFitHeight(b, 800);
        }

    }


    // scale and keep aspect ratio
    public static Bitmap scaleToFitWidth(Bitmap b, int width) {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, Math.round(b.getHeight() * factor), true);
    }


    // scale and keep aspect ratio
    public static Bitmap scaleToFitHeight(Bitmap b, int height) {
        float factor = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, Math.round(b.getWidth() * factor), height, true);
    }


}