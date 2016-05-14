package service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import activity.EnterPwdActivity;
import db.dao.AppLockDao;

/**
 * Created by ChanIan on 16/5/12.
 */
public class WatchDogService extends Service {

    private ActivityManager mAm;
    private boolean flag;
    private AppLockDao dao;
    private WatchReceiver mReceiver;
    private String mTempName;
    private List<ActivityManager.RunningTaskInfo> mTasks;
    private String mPackageName;
    private List<String> mInfos;
    private WatchObserver mObserver;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        flag = true;
        dao = new AppLockDao(this);
        //注册广播
        mReceiver = new WatchReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.ian.watch.dog");
        //添加锁屏监听
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mReceiver, filter);

        //添加内容观察者
        mObserver = new WatchObserver(new Handler());
        Uri uri = Uri.parse("content://com.ian.lockdb.changed");
        getContentResolver().registerContentObserver(uri, true, mObserver);
        //获取包名
        mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        mInfos = dao.queryAll();

        //不停启动开门狗监视
        watchDog();
    }

    private void watchDog() {
        new Thread() {
            public void run() {
                while (flag) {
                    long startTime = System.currentTimeMillis();
                    mTasks = mAm.getRunningTasks(1);
                    //得到栈顶的应用包名,并传递过去
                    mPackageName = mTasks.get(0).topActivity.getPackageName();
                    Log.v("ian", "lockPack:" + mPackageName);
                    //如发现属于被锁定应用则跳到输密码界面
                    if (mInfos.contains(mPackageName)) {//是被锁定应用
                        if (mPackageName.equals(mTempName)) {//需要临时停止密码验证

                        } else {
                            Intent intent = new Intent(WatchDogService.this, EnterPwdActivity.class);
                            intent.putExtra("packname", mPackageName);
                            //服务里没有任务栈信息,必须添加任务栈信息
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    } else {//未加锁应用

                    }
                    long endTime = System.currentTimeMillis();
                    long time = endTime - startTime;
                    //整个循环时间差
                    Log.v("ian", "time:" + time);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        //注销广播
        unregisterReceiver(mReceiver);
        mReceiver = null;
        //注销内容观察者
        getContentResolver().unregisterContentObserver(mObserver);
        mObserver=null;
    }

    //广播接收者
    private class WatchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.ian.watch.dog")) {
                mTempName = intent.getStringExtra("name");//密码正确的包名
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                //屏幕锁定
                flag = false;
                mTempName = null;
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                //只有在屏幕开启才添加看门狗功能
                flag = true;
                watchDog();
            }
        }
    }

    //自定义广播观察者
    private class WatchObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public WatchObserver(Handler handler) {
            super(handler);
            //数据库发生改变,需要更新
            mInfos = dao.queryAll();
        }
    }
}
