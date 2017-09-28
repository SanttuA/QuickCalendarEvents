package com.apps.santtualatalo.quickcalendarevents;

import android.content.Context;
import android.widget.Toast;

/**
 * Handles creating toast messages
 */
public class SingleToast {
    private static Toast mToast;

    //shows a toast and makes sure only one toast is active
    public static void show(Context context, String text, int duration) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(context, text, duration);
        mToast.show();
    }
}
