package activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.testdemo.chanian.mymobilesafe.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bean.AppInfo;
import engine.AppInfoProvider;


/**
 * Created by ChanIan on 16/5/8.
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {
    private TextView tv_rom_size;
    private TextView tv_sd_size;
    private TextView tv_app_status;
    private ListView lv_apps;
    private List<AppInfo> mInfos;
    private List<AppInfo> mRomInfos;
    private List<AppInfo> mSysInfos;
    private LinearLayout ll_loading;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            ll_loading.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new Myadapter();
                lv_apps.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();

            }
        }
    };
    private Myadapter mAdapter;
    private AppInfo appInfo;
    private View mPopView;
    private PopupWindow mPopupWindow;
    private AppReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manage);
        tv_rom_size = (TextView) findViewById(R.id.tv_rom_size);
        tv_sd_size = (TextView) findViewById(R.id.tv_sd_size);
        lv_apps = (ListView) findViewById(R.id.lv_apps);
        tv_app_status = (TextView) findViewById(R.id.tv_app_status);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        //查询手机和SD卡剩余内存
        File rom = Environment.getDataDirectory();
        long romSize = rom.getFreeSpace();
        File sd = Environment.getExternalStorageDirectory();
        long sdSize = sd.getFreeSpace();
        tv_rom_size.setText("手机内存:" + Formatter.formatFileSize(this, romSize) + "  ");
        tv_sd_size.setText("SD卡内存:" + Formatter.formatFileSize(this, sdSize));
        mReceiver = new AppReceiver();
        //监听软件卸载服务
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(mReceiver,filter);
        fillData();
        lv_apps.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mInfos != null) {
                    if (firstVisibleItem > mRomInfos.size()) {
                        tv_app_status.setText("系统应用" + mSysInfos.size() + "个");
                    } else {
                        tv_app_status.setText("手机应用" + mRomInfos.size() + "个");

                    }
                }
            }
        });

        //弹出功能选项
        lv_apps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                } else if (position == mRomInfos.size() + 1) {
                    return;
                } else if (position <= mRomInfos.size()) {//手机应用
                    appInfo = mRomInfos.get(position - 1);
                } else {
                    appInfo = mSysInfos.get(position - mRomInfos.size() - 2);
                }
                dissPopwindow();

                mPopView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popuwindos, null);
                mPopupWindow = new PopupWindow(mPopView, -2, -2);
                int[] location = new int[2];
                view.getLocationInWindow(location);
                //设置背景供点击
                mPopupWindow.setBackgroundDrawable(new ColorDrawable());
                mPopupWindow.showAtLocation(parent, Gravity.TOP + Gravity.LEFT, 60, location[1]);
                //设置启动动画
                AnimationSet set = new AnimationSet(false);
                AlphaAnimation am = new AlphaAnimation(.2f, 1.0f);
                am.setDuration(500);
                ScaleAnimation sm = new ScaleAnimation(.2f, 1.0f, .2f, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                sm.setDuration(500);
                set.addAnimation(am);
                set.addAnimation(sm);
                mPopView.startAnimation(set);
                //添加点击事件
                mPopView.findViewById(R.id.ll_detail).setOnClickListener(AppManagerActivity.this);
                mPopView.findViewById(R.id.ll_share).setOnClickListener(AppManagerActivity.this);
                mPopView.findViewById(R.id.ll_start).setOnClickListener(AppManagerActivity.this);
                mPopView.findViewById(R.id.ll_uninstall).setOnClickListener(AppManagerActivity.this);
            }
        });
    }

    //填充数据
    private void fillData() {
        new Thread() {
            public void run() {
                ll_loading.setVisibility(View.VISIBLE);
                mInfos = AppInfoProvider.getAppInfos(getApplicationContext());
                mRomInfos = new ArrayList<AppInfo>();
                mSysInfos = new ArrayList<AppInfo>();
                for (AppInfo info : mInfos) {
                    if (info.isUserApp()) {
                        mRomInfos.add(info);
                    } else {
                        mSysInfos.add(info);
                    }
                }
                //                Log.v("ian", "size:" + mInfos.size());
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_share://分享
                shareApp();
                break;
            case R.id.ll_start://启动
                startApp();
                break;
            case R.id.ll_uninstall://卸载
                unInstall();
                break;
            case R.id.ll_detail://详情
                appDetail();
                break;
        }
        dissPopwindow();
    }

    //详情
    private void appDetail() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:"+appInfo.getPackageName()));
        startActivity(intent);
    }

    //卸载
    private void unInstall() {
        if(appInfo.isUserApp()) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:"+appInfo.getPackageName()));
            Log.v("ian", appInfo.getPackageName());
        startActivity(intent);
        }else {
            Toast.makeText(AppManagerActivity.this, "系统应用无法卸载", Toast.LENGTH_SHORT).show();
        }
    }
    class AppReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //刷新界面
            if(appInfo.isUserApp()) {
                mRomInfos.remove(appInfo);
            }else {
                mSysInfos.remove(appInfo);
            }
            mAdapter.notifyDataSetChanged();
        }
    }
    //启动
    private void startApp() {
        //拿到包管理者
        PackageManager manager = getPackageManager();
//        Intent intent = new Intent();
//        intent.setAction("android.intent.action.MAIN");
//        intent.addCategory("android.intent.category.LAUNCHER");
//        List<ResolveInfo> resolveInfos = manager.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
//        for (ResolveInfo info :resolveInfos){
//
//        }
        Intent intent = manager.getLaunchIntentForPackage(appInfo.getPackageName());
        if(intent!=null) {
            startActivity(intent);
        }else {
            Toast.makeText(AppManagerActivity.this, "无法启动", Toast.LENGTH_SHORT).show();
        }
    }

    //分享
    private void shareApp() {
        //通过短信分享
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "一款不错的软件" + appInfo.getAppName());
        startActivity(intent);

    }

    private class Holder {
        TextView tv_app_name;
        TextView tv_app_rom;
        TextView tv_app_size;
        ImageView iv_app_icon;
    }

    class Myadapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mInfos.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            appInfo = null;
            if (position == 0) {//显示手机应用标题
                TextView tv = new TextView(getApplicationContext());
                tv.setText("手机应用" + mRomInfos.size() + "个");
                tv.setBackgroundColor(Color.DKGRAY);
                tv.setTextColor(Color.WHITE);
                return tv;
            } else if (position == mRomInfos.size() + 1) {//显示系统应用标题
                TextView tv = new TextView(getApplicationContext());
                tv.setText("系统应用" + mSysInfos.size() + "个");
                tv.setBackgroundColor(Color.DKGRAY);
                tv.setTextColor(Color.WHITE);
                return tv;

            } else if (position <= mRomInfos.size()) {//手机应用
                appInfo = mRomInfos.get(position - 1);
            } else {
                appInfo = mSysInfos.get(position - mRomInfos.size() - 2);
            }

            Holder holder = null;
            if (convertView != null && convertView instanceof RelativeLayout) {

                holder = (Holder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_app_info, null);
                holder = new Holder();
                holder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                holder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.tv_app_rom = (TextView) convertView.findViewById(R.id.tv_app_rom);
                holder.tv_app_size = (TextView) convertView.findViewById(R.id.tv_app_size);
                convertView.setTag(holder);
            }
            holder.iv_app_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_app_name.setText(appInfo.getAppName());
            holder.tv_app_rom.setText(appInfo.isInRom() ? "手机内存" : "SD卡");
            holder.tv_app_size.setText(Formatter.formatFileSize(getApplicationContext(), appInfo.getAppSize()));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销服务
        unregisterReceiver(mReceiver);
        mReceiver=null;
        dissPopwindow();
    }

    private void dissPopwindow() {
        if (mPopupWindow != null&&mPopupWindow.isShowing()
                ) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }
}
