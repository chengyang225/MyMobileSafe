package bean;

import android.graphics.drawable.Drawable;

/**
 * Created by ChanIan on 16/5/9.
 */
public class ProcessInfo {
    private Drawable icon;//应用图片
    private String packageName;//应用包名
    private String appName;//应用名
    private long appSize;//应用大小
    private boolean userApp;//用户进程

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }
}
