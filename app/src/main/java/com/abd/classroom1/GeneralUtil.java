package com.abd.classroom1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;

/**
 * Created by Abd on 3/17/2016.
 */
public class GeneralUtil {
    public static void buttonEffect(final View button) {
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(button.getResources().getColor(R.color.press_button), PorterDuff.Mode.SRC_OVER);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {

                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }

    public static void openImage (Activity activity,String filePAth){
        File tmpo = new File(filePAth);
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(tmpo), "image/*");
        activity.startActivity(intent);

    }
}
