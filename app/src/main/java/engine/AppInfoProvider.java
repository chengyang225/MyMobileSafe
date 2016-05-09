package engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bean.AppInfo;

/**
 * Created by ChanIan on 16/5/8.
 */
public class AppInfoProvider  {
    public static List<AppInfo> getAppInfos(Context context){
        List<AppInfo> infos=new ArrayList<>();
        //得到包管理者获取内容
        PackageManager manager = context.getPackageManager();
//        List<ApplicationInfo> installedApplications = manager.getInstalledApplications(0);
        List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
        for (PackageInfo info :installedPackages){
            AppInfo appInfo = new AppInfo();
            String className = info.packageName;//包名
            appInfo.setPackageName(className);
//            Log.v("ian", "pName:"+className);
            String name = info.applicationInfo.loadLabel(manager).toString();//应用名
            appInfo.setAppName(name);
//            Log.v("ian", "appName"+name);
            Drawable icon = info.applicationInfo.loadIcon(manager);//得到应用图片
            appInfo.setIcon(icon);

            String path = info.applicationInfo.sourceDir;//得到路径
            File file = new File(path);
            appInfo.setAppSize(file.length());
//            Log.v("ian", "size:"+file.length());
            int flags = info.applicationInfo.flags;//得到标签
            if((flags&ApplicationInfo.FLAG_SYSTEM)==0) {//用户应用
                appInfo.setUserApp(true);
//                Log.v("ian", "user:"+1);
            }else {//系统应用
                appInfo.setUserApp(false);
//                Log.v("ian", "user:"+0);

            }

            if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0) {//手机内存
                appInfo.setInRom(true);
//                Log.v("ian", "sd:"+0);
            }else {//sd卡
                appInfo.setInRom(false);
//                Log.v("ian", "sd:"+1);

            }
            infos.add(appInfo);
        }

        return infos;
    }

}
