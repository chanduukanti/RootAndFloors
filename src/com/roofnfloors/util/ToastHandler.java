package com.roofnfloors.util;

import android.content.Context;
import android.widget.Toast;

public class ToastHandler {
    private static Toast mToast;

    public static void displayToast(int text, Context context) {
        if (mToast != null && mToast.getView().isShown()) {
            mToast.cancel();
            mToast = null;
        } else {
            mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
            mToast.show();
        }
    }
}
