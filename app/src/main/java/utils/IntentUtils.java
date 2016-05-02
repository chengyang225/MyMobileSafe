package utils;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by ChanIan on 16/5/1.
 */
//跳转并关闭页面
public class IntentUtils {
    public static void startActivityForDelayAndFinished(final Activity activity,
                                                        final Class<?> clazz, final long time){
        //开线程跳转
        new Thread(){
            public void run(){

                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(activity, clazz);
                activity.startActivity(intent);
                activity.finish();
            }
        }.start();

    }
    public static void startActivityAndFinished(final Activity activity,
                                                        final Class<?> clazz){
        //开线程跳转
        new Thread(){
            public void run(){
                Intent intent = new Intent(activity, clazz);
                activity.startActivity(intent);
                activity.finish();
            }
        }.start();

    }
    //开启新页面
    public static void startActivity(final Activity activity,
                                                        final Class<?> clazz){
        //开线程跳转
        new Thread(){
            public void run(){
                Intent intent = new Intent(activity, clazz);
                activity.startActivity(intent);
            }
        }.start();

    }
}
