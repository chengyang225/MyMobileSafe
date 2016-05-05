package utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by ChanIan on 16/5/5.
 */
//获取正在运行服务
public class CheckRunningServiceUtils {
    public static boolean getServiceRunningState(Context context,String serviceName){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info :services){
            String name = info.service.getClassName();
            if(serviceName.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
