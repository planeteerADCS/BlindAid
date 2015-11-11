package com.planeteers.blindaid.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by Jose on 11/10/15.
 */
public class ImageUtil {

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }
}
