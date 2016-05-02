package utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by ChanIan on 16/5/1.
 */
public class ToastUtils {
    public static void getToast(final Activity activity, final String text) {
        if ("main".equals(Thread.currentThread().getName())) {
            Toast.makeText(activity, text,Toast.LENGTH_LONG).show();

        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_LONG).show();

                }
            });
        }
    }
}
