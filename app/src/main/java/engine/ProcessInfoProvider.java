package engine;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import bean.ProcessInfo;

/**
 * Created by ChanIan on 16/5/9.
 */
public class ProcessInfoProvider {
    //获取进程总数
    public  static int getProcessCount(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        return processes.size();
    }
    //获取剩余ram
    public static long getFreeRam(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }
    //获取总ram
    public  static long getTotalRam(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem;

    }
    //获取所有进程
    public static List<ProcessInfo> getProcesses(Context context){
        List<ProcessInfo> infos=new ArrayList<>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        PackageManager manager = context.getPackageManager();
        for (ActivityManager.RunningAppProcessInfo info :processes){
            ProcessInfo processInfo = new ProcessInfo();

//            manager.getPackageInfo()

        }

        return infos;
    }
}
