package activity;

import android.app.Activity;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Layout;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.testdemo.chanian.mymobilesafe.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import utils.ToastUtils;

/**
 * Created by ChanIan on 16/5/13.
 */
public class CleanCacheActivity extends Activity {
    private FrameLayout fl_scan;
    private ProgressBar pb_cache;
    private TextView tv_cache_name;
    private ListView lv_cache_apps;
    private PackageManager mPm;
    private List<PackageInfo> mInfos;
    private List<CacheInfo> mCacheInfos;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String appName= (String) msg.obj;
            switch (msg.what){
                case SCAN_FINISH://扫描结束
                    fl_scan.setVisibility(View.GONE);
                    Toast.makeText(CleanCacheActivity.this, "扫描结束", Toast.LENGTH_SHORT).show();
                    break;
                case SCAN_APP://正在扫描
                    tv_cache_name.setText("正在扫描"+appName);

                    break;
            }
            //设置适配器
            if(mCacheInfos.size()>0) {
                mAdapter = new CacheAdapter();
                lv_cache_apps.setAdapter(mAdapter);
            }
        }
    };
    private static final int SCAN_FINISH = 1;
    private static final int SCAN_APP=2;
    private CacheAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clean);
        fl_scan = (FrameLayout) findViewById(R.id.fl_scan);
        pb_cache = (ProgressBar) findViewById(R.id.pb_cache);
        tv_cache_name = (TextView)findViewById(R.id.tv_cache_name);
        lv_cache_apps = (ListView) findViewById(R.id.lv_cache_apps);
        mPm = getPackageManager();

        scanApps();
    }


    //扫描数据
    private void scanApps() {
        //得到所有已安装程序集合
        //所有有缓存的程序集合
        mCacheInfos = new ArrayList<>();
        new Thread() {
            public void run() {
                mInfos = mPm.getInstalledPackages(0);
                pb_cache.setMax(mInfos.size());//设置进度条最大值
                int progress = 0;
                for (PackageInfo info : mInfos) {
                    String packageName = info.packageName;
                    progress++;
                    pb_cache.setProgress(progress);//设置当前进度
                    //通过反射调用packagemanage的方法
                    try {
                        Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo",
                                String.class, IPackageStatsObserver.class);
                        method.invoke(mPm, packageName, new MyObserver());
                        Thread.sleep(100);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message msg = Message.obtain();
                msg.what = SCAN_FINISH;
                handler.sendMessage(msg);//发消息通知已扫描结束

            }
        }.start();
    }
    private class CleanCacheObserver extends IPackageDataObserver.Stub{
        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            if(succeeded) {//清理成功
                scanApps();
                ToastUtils.getToast(CleanCacheActivity.this, "清理成功");
            }else {
                scanApps();
                ToastUtils.getToast(CleanCacheActivity.this, "清理失败");
            }
        }
    }

    private class MyObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            String packageName = pStats.packageName;//应用包名
            try {
                long cacheSize = pStats.cacheSize;
                Drawable icon = mPm.getPackageInfo(packageName, 0).applicationInfo.loadIcon(mPm);
                String name = mPm.getPackageInfo(packageName, 0).applicationInfo.loadLabel(mPm).toString();
                if (cacheSize > 0) {//该应用有缓存
                    CacheInfo cacheInfo = new CacheInfo();
                    cacheInfo.appSize = cacheSize;
                    cacheInfo.appName=name;
                    cacheInfo.appIcon=icon;
                    mCacheInfos.add(cacheInfo);
                }
                //将应用名传过去显示在进度条上
                Message msg = Message.obtain();
                msg.what=SCAN_APP;
                msg.obj = name;
                handler.sendMessage(msg);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();

            }
        }
    }

    //清理所有缓存
    public void cleanAll(View view) {
        Method[] methods = PackageManager.class.getMethods();
        for (Method method:methods){
            //清理所有缓存方法
            if("freeStorageAndNotify".equals(method.getName())) {
                try {
                    method.invoke(mPm,Integer.MAX_VALUE,new CleanCacheObserver());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class CacheInfo {
        String appName;
        long appSize;
        Drawable appIcon;
    }
    private class ViewHolder{
        ImageView iv_cache_icon;
        TextView tv_cache_name;
        TextView tv_cache_size;
    }
    private class CacheAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mCacheInfos.size();
        }
        @Override//填充数据
        public android.view.View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            if(convertView==null) {
                convertView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_cache,null);
                holder=new ViewHolder();
                holder.iv_cache_icon= (ImageView) convertView.findViewById(R.id.iv_cache_icon);
                holder.tv_cache_name= (TextView) convertView.findViewById(R.id.tv_cache_name);
                holder.tv_cache_size= (TextView) convertView.findViewById(R.id.tv_cache_size);
                convertView.setTag(holder);
            }else {
                holder= (ViewHolder) convertView.getTag();
            }
            CacheInfo cacheInfo = mCacheInfos.get(position);
            holder.iv_cache_icon.setImageDrawable(cacheInfo.appIcon);
            holder.tv_cache_name.setText(cacheInfo.appName);
            holder.tv_cache_size.setText(Formatter.formatFileSize(getApplicationContext(),cacheInfo.appSize));
            return convertView;
        }
        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


    }
}
