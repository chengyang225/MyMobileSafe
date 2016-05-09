package engine;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.testdemo.chanian.mymobilesafe.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import bean.ProcessInfo;

/**
 * Created by ChanIan on 16/5/9.
 */
public class ProcessInfoProvider {
    //获取进程总数
    public static int getProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        return processes.size();
    }

    //获取剩余ram
    public static long getFreeRam(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    //获取总ram
    public static long getTotalRam(Context context) {
        //API 16以上可用
        //        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //        am.getMemoryInfo(memoryInfo);

        //复写系统代码
        try {
            //读取系统运存文件
            File file = new File("/proc/meminfo");
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            //获取第一行总运存记录
            String info = reader.readLine();
            //只需要数字,去除其他无关信息
            StringBuffer buffer = new StringBuffer();
            for (char c :info.toCharArray()){
                if(c>='0'&&c<='9') {
                    buffer.append(c);
                }
            }
            return Long.parseLong(buffer.toString())*1024;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;

    }

    //获取所有进程
    public static List<ProcessInfo> getProcesses(Context context) {
        List<ProcessInfo> infos = new ArrayList<>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        PackageManager manager = context.getPackageManager();
        for (ActivityManager.RunningAppProcessInfo info : processes) {
            ProcessInfo processInfo = new ProcessInfo();
            String packName = info.processName;//获取包名
            processInfo.setPackageName(packName);

            try {
                PackageInfo packageInfo = manager.getPackageInfo(packName, 0);
                Drawable icon = packageInfo.applicationInfo.loadIcon(manager);//获取图标
                processInfo.setIcon(icon);
                String appName = packageInfo.applicationInfo.loadLabel(manager).toString();//获取应用名
                processInfo.setAppName(appName);
                String path = packageInfo.applicationInfo.sourceDir;
                File file = new File(path);
                long size = file.length();//获取应用大小
                processInfo.setAppSize(size);
                int flags = packageInfo.applicationInfo.flags;
                if((flags&ApplicationInfo.FLAG_SYSTEM)==0) {//用户进程
                    processInfo.setUserApp(true);
                }else {//系统进程
                    processInfo.setUserApp(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                //无应用名和图片的进程
                processInfo.setAppName(packName);
                processInfo.setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));

            }
            infos.add(processInfo);

        }

        return infos;
    }
}
