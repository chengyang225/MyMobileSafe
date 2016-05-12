package service;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.testdemo.chanian.mymobilesafe.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import engine.MyWidgetProvider;
import engine.ProcessInfoProvider;
import receiver.ProcessKillReceiver;
import utils.CheckRunningServiceUtils;

/**
 * Created by ChanIan on 16/5/9.
 */
public class KillProcessService extends Service {
    private LockReceiver mReceiver;
    private Timer timer;
    private TimerTask timerTask;
    private AppWidgetManager mAppWidgetManager;
    private ComponentName mProvider;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册广播监听锁屏清理
        mReceiver = new LockReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);
        Log.v("ian", "锁屏清理服务开启了");
        timer = new Timer();
        mAppWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        mProvider = new ComponentName(getApplicationContext(), MyWidgetProvider.class);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
                int process_count = ProcessInfoProvider.getProcessCount(getApplicationContext());
                views.setTextViewText(R.id.process_count, "正在运行软件:" + process_count+"个");
                String process_memory = Formatter.formatFileSize(getApplicationContext(), ProcessInfoProvider.getFreeRam(getApplicationContext()));
                views.setTextViewText(R.id.process_memory, "可用内存:" + process_memory);
                Intent intent = new Intent(getApplicationContext(), ProcessKillReceiver.class);
                intent.setAction("com.ian.action.killprocess");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, 0);
                views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
                mAppWidgetManager.updateAppWidget(mProvider, views);
            }
        };
        timer.schedule(timerTask, 1000, 5000);//每隔5秒执行一次
    }

    @Override
    public void onDestroy() {
        //注销广播
        unregisterReceiver(mReceiver);
        mReceiver = null;
        //定时器和任务停止
        timer.cancel();
        Log.v("ian", "锁屏清理服务关闭了");
        timerTask.cancel();
        super.onDestroy();
    }

    private class LockReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info : processes) {
                am.killBackgroundProcesses(info.processName); //杀掉进程
            }
        }
    }
}
