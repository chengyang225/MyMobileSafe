package activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testdemo.chanian.mymobilesafe.R;

import java.util.ArrayList;
import java.util.List;

import bean.AppInfo;
import db.dao.AppLockDao;
import engine.AppInfoProvider;

/**
 * Created by ChanIan on 16/5/10.
 */
public class AppLockActivity extends Activity {
    private LinearLayout ll_unlock;
    private LinearLayout ll_lock;
    private TextView tv_unlock_count;
    private TextView tv_lock_count;
    private ListView lv_unlock_app;
    private ListView lv_lock_app;
    private Button btn_unlock;
    private AppLockAdapter mUnLockAdapter;
    private AppLockAdapter mLockAdapter;
    private Button btn_lock;
    private List<AppInfo> mLockInfos;
    private List<AppInfo> mUnLockInfos;
    private List<AppInfo> mInfos;
    private AppLockDao mDao;

    //主线程消息处理
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            mUnLockAdapter = new AppLockAdapter(true);
            mLockAdapter = new AppLockAdapter(false);
            lv_unlock_app.setAdapter(mUnLockAdapter);
            lv_lock_app.setAdapter(mLockAdapter);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        mDao = new AppLockDao(this);
        initViews();
        fillData();
    }

    //填充数据
    private void fillData() {
        new Thread() {
            public void run() {
                mInfos = AppInfoProvider.getAppInfos(getApplicationContext());
                //分离集合
                mLockInfos = new ArrayList<AppInfo>();
                mUnLockInfos = new ArrayList<AppInfo>();
                for (AppInfo info : mInfos) {
                    if (mDao.query(info.getPackageName())) {
                        mLockInfos.add(info);
                    } else {
                        mUnLockInfos.add(info);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    //初始化控件
    private void initViews() {
        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
        ll_lock = (LinearLayout) findViewById(R.id.ll_lock);
        tv_unlock_count = (TextView) findViewById(R.id.tv_unlock_count);
        tv_lock_count = (TextView) findViewById(R.id.tv_lock_count);
        lv_unlock_app = (ListView) findViewById(R.id.lv_unlock_app);
        lv_lock_app = (ListView) findViewById(R.id.lv_lock_app);
        btn_unlock = (Button) findViewById(R.id.btn_unlock);
        btn_lock = (Button) findViewById(R.id.btn_lock);
    }

    //未加锁
    public void unlockClick(View view) {
        ll_lock.setVisibility(View.GONE);
        ll_unlock.setVisibility(View.VISIBLE);
        btn_lock.setBackgroundResource(R.mipmap.tab_right_default);
        btn_unlock.setBackgroundResource(R.mipmap.tab_left_pressed);

    }

    //加锁
    public void lockClick(View view) {
        ll_lock.setVisibility(View.VISIBLE);
        ll_unlock.setVisibility(View.GONE);
        btn_lock.setBackgroundResource(R.mipmap.tab_right_pressed);
        btn_unlock.setBackgroundResource(R.mipmap.tab_left_default);
    }

    //缓存view
    private class ViewHolder {
        private ImageView iv_app_lock_icon;
        private TextView tv_app_lock_name;
        private ImageView iv_app_lock;

    }

    //适配器
    private class AppLockAdapter extends BaseAdapter {
        boolean isUnlock;

        public AppLockAdapter(boolean isUnlock) {
            this.isUnlock = isUnlock;
        }

        @Override
        public int getCount() {
            int count = 0;
            if (isUnlock) {
                count = mUnLockInfos.size();
                tv_unlock_count.setText("未加锁软件:" + count + "个");
            } else {
                count = mLockInfos.size();
                tv_lock_count.setText("已加锁软件:" + count + "个");
            }
            return count;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final View view;

            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                holder = new ViewHolder();
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_app_lock_info, null);
                holder.iv_app_lock_icon = (ImageView) view.findViewById(R.id.iv_app_lock_icon);
                holder.iv_app_lock = (ImageView) view.findViewById(R.id.iv_app_lock);
                holder.tv_app_lock_name = (TextView) view.findViewById(R.id.tv_app_lock_name);
                view.setTag(holder);
            }
            final AppInfo appInfo;
            if (isUnlock) {
                appInfo = mUnLockInfos.get(position);
                holder.iv_app_lock.setBackgroundResource(R.mipmap.list_button_lock_pressed);
            } else {
                appInfo = mLockInfos.get(position);
                holder.iv_app_lock.setBackgroundResource(R.mipmap.list_button_unlock_pressed);
            }
            holder.iv_app_lock_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_app_lock_name.setText(appInfo.getAppName());
            holder.iv_app_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isUnlock) {
                        //添加位移动画
                        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                        ta.setDuration(300);
                        view.startAnimation(ta);
                        //给动画设置监听事件
                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mUnLockInfos.remove(appInfo);
                                mLockInfos.add(appInfo);
                                mDao.insert(appInfo.getPackageName());
                                mUnLockAdapter.notifyDataSetChanged();
                                mLockAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    } else {

                        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                        ta.setDuration(300);
                        view.startAnimation(ta);
                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mLockInfos.remove(appInfo);
                                mUnLockInfos.add(appInfo);
                                mDao.delete(appInfo.getPackageName());
                                mLockAdapter.notifyDataSetChanged();
                                mUnLockAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }
            });
            return view;
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
