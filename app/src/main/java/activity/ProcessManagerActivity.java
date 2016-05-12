package activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.testdemo.chanian.mymobilesafe.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bean.ProcessInfo;
import engine.ProcessInfoProvider;

/**
 * Created by ChanIan on 16/5/9.
 */
public class ProcessManagerActivity extends Activity {
    private TextView tv_process_number;
    private SharedPreferences sp;
    private TextView tv_memory_status;
    private TextView tv_process_status;
    private ListView lv_processes;
    private LinearLayout ll_loading;
    private List<ProcessInfo> mInfos;
    private List<ProcessInfo> mUserInfos;
    private List<ProcessInfo> mSysInfos;
    private List<ProcessInfo> mInKillInfos;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            ll_loading.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new ProcessAdapter();
                lv_processes.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    };
    private ProcessInfo mProcessInfo;
    private ProcessAdapter mAdapter;
    private long mTotalRam;
    private long mFreeRam;
    private int mCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        tv_process_number = (TextView) findViewById(R.id.tv_process_number);
        tv_memory_status = (TextView) findViewById(R.id.tv_memory_status);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_process_status = (TextView) findViewById(R.id.tv_process_status);
        lv_processes = (ListView) findViewById(R.id.lv_processes);
        mInfos = new ArrayList<>();
        mFreeRam = ProcessInfoProvider.getFreeRam(this);


        mCount = ProcessInfoProvider.getProcessCount(this);
        tv_process_number.setText("运行进程:" + mCount + "个");
        mTotalRam = ProcessInfoProvider.getTotalRam(this);
        tv_memory_status.setText("剩余内存/总内存:" + Formatter.formatFileSize(this, mFreeRam) +
                "/" + Formatter.formatFileSize(this, mTotalRam));
        fillData();
        //添加滚动事件监听
        lv_processes.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mUserInfos != null && mSysInfos != null) {
                    if (mInfos != null) {
                        if (firstVisibleItem > mUserInfos.size()) {
                            tv_process_status.setText("系统应用" + mSysInfos.size() + "个");
                        } else {
                            tv_process_status.setText("手机应用" + mUserInfos.size() + "个");

                        }
                    }
                }
            }
        });
        //添加点击事件监听
        lv_processes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {//显示手机应用标题
                    return;
                } else if (position == mUserInfos.size() + 1) {//显示系统应用标题
                    return;
                } else if (position <= mUserInfos.size()) {//手机应用
                    mProcessInfo = mUserInfos.get(position - 1);
                } else {
                    mProcessInfo = mSysInfos.get(position - mUserInfos.size() - 2);
                }
                //不要让自己的单选框被选中
                if (getPackageName().equals(mProcessInfo.getPackageName())) {
                    return;
                }
                if (mProcessInfo.isChecked()) {
                    mProcessInfo.setChecked(false);
                } else {
                    mProcessInfo.setChecked(true);
                }
                //刷新数据
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    //开启多线程加载数据
    private void fillData() {
        new Thread() {
            public void run() {
                ll_loading.setVisibility(View.VISIBLE);
                mInfos = ProcessInfoProvider.getProcesses(getApplicationContext());
                mUserInfos = new ArrayList<ProcessInfo>();
                mSysInfos = new ArrayList<ProcessInfo>();
                for (ProcessInfo info : mInfos) {
                    if (info.isUserApp()) {
                        mUserInfos.add(info);
                    } else {
                        mSysInfos.add(info);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    //全选
    public void selectAll(View view) {
        for (ProcessInfo info : mUserInfos) {
            //不要让自己的单选框被选中
            if (getPackageName().equals(mProcessInfo.getPackageName())) {
                continue;
            }
            info.setChecked(true);
        }
        for (ProcessInfo info : mSysInfos) {
            //不要让自己的单选框被选中
            if (getPackageName().equals(mProcessInfo.getPackageName())) {
                continue;
            }
            info.setChecked(true);
        }
        mAdapter.notifyDataSetChanged();
    }

    //反选
    public void deselectAll(View view) {
        for (ProcessInfo info : mUserInfos) {
            info.setChecked(!info.isChecked());
        }
        for (ProcessInfo info : mSysInfos) {
            info.setChecked(!info.isChecked());
        }
        mAdapter.notifyDataSetChanged();
    }

    //清理
    public void cleanAll(View view) {
        int count = 0;//杀掉的进程数
        long savedMem = 0;//释放的空间
        mInKillInfos = new ArrayList<>();
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ProcessInfo info : mUserInfos) {
            if (info.isChecked()) {
                am.killBackgroundProcesses(info.getPackageName());//杀掉进程
                count++;
                savedMem += info.getAppSize();
                mInKillInfos.add(info);//结束后统一移除
            }
        }
        for (ProcessInfo info : mSysInfos) {
            if (info.isChecked()) {
                am.killBackgroundProcesses(info.getPackageName());//杀掉进程
                count++;
                savedMem += info.getAppSize();
                mInKillInfos.add(info);
            }
        }
        for (ProcessInfo info : mInKillInfos) {
            if (info.isUserApp()) {
                mUserInfos.remove(info);//将以杀掉进程从集合移除
            } else {
                mSysInfos.remove(info);
            }
        }
        mAdapter.notifyDataSetChanged();
        Toast.makeText(ProcessManagerActivity.this, "已关闭" + count + "个进程,释放了" +
                Formatter.formatFileSize(getApplicationContext(), savedMem) + "空间", Toast.LENGTH_SHORT).show();
        //刷新显示进程数量
        mCount -= count;
        mFreeRam += savedMem;
        tv_process_number.setText("运行进程:" + mCount + "个");
        tv_memory_status.setText("剩余内存/总内存:" + Formatter.formatFileSize(this, mFreeRam) +
                "/" + Formatter.formatFileSize(this, mTotalRam));
    }

    //设置
    public void setting(View view) {
        Intent intent = new Intent(this, ProcessSettingActivity.class);
        startActivity(intent);
    }

    private class Holder {
        TextView tv_app_name;
        TextView tv_app_size;
        ImageView iv_app_icon;
        CheckBox cb_process;
    }

    //准备适配器
    private class ProcessAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (sp.getBoolean("processSet", true)) {
                return mUserInfos.size() + mSysInfos.size() + 2;
            } else {
                return mUserInfos.size() + 1;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            mProcessInfo = null;
            if (position == 0) {//显示手机应用标题
                TextView tv = new TextView(getApplicationContext());
                tv.setText("用户进程" + mUserInfos.size() + "个");
                tv.setBackgroundColor(Color.DKGRAY);
                tv.setTextColor(Color.WHITE);
                return tv;
            } else if (position == mUserInfos.size() + 1) {//显示系统应用标题
                TextView tv = new TextView(getApplicationContext());
                tv.setText("系统进程" + mSysInfos.size() + "个");
                tv.setBackgroundColor(Color.DKGRAY);
                tv.setTextColor(Color.WHITE);
                return tv;

            } else if (position <= mUserInfos.size()) {//手机应用
                mProcessInfo = mUserInfos.get(position - 1);
            } else {
                mProcessInfo = mSysInfos.get(position - mUserInfos.size() - 2);
            }

            Holder holder = null;
            if (convertView != null && convertView instanceof RelativeLayout) {

                holder = (Holder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_process_info, null);
                holder = new Holder();
                holder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                holder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.tv_app_size = (TextView) convertView.findViewById(R.id.tv_process_size);
                holder.cb_process = (CheckBox) convertView.findViewById(R.id.cb_process);
                convertView.setTag(holder);
            }
            holder.iv_app_icon.setImageDrawable(mProcessInfo.getIcon());
            holder.cb_process.setChecked(mProcessInfo.isChecked());
            //不让自己的应用出现单选框
            if (getPackageName().equals(mProcessInfo.getPackageName())) {
                holder.cb_process.setVisibility(View.INVISIBLE);
            } else {
                holder.cb_process.setVisibility(View.VISIBLE);
            }
            holder.tv_app_name.setText(mProcessInfo.getAppName());
            holder.tv_app_size.setText(Formatter.formatFileSize(getApplicationContext(), mProcessInfo.getAppSize()));
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

    //刷新数据
    @Override
    protected void onStart() {
        super.onStart();
        if(mAdapter!=null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
