package activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.testdemo.chanian.mymobilesafe.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import db.dao.VirusDao;
import utils.AppInfoUtils;
import utils.IntentUtils;
import utils.StringUtils;

public class SplashActivity extends AppCompatActivity {
    private RelativeLayout rlLayout;
    private TextView tvVersionName;
    private static final int NEED_UPDATE = 1;
    private UpdateInfo updateInfo;
    private SharedPreferences mSp;

    //更新信息
    class UpdateInfo {
        int version;
        String downloadurl;
        String desc;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            //提醒升级对话框
            switch (msg.what) {
                case NEED_UPDATE:
                    showUpdateDialog(msg.obj);
                    break;
            }
        }
    };

    //升级对话框
    private void showUpdateDialog(Object obj) {
        updateInfo = (UpdateInfo) obj;
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("升级提醒").setMessage("有新版本,是否升级").
                setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //取消升级,直接跳转到主页
                        IntentUtils.startActivityAndFinished(SplashActivity.this, HomeActivity.class);
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确认升级,使用xUtils断点下载到存储卡然后安装
                HttpUtils httpUtils = new HttpUtils();
                final File file = new File(Environment.getExternalStorageDirectory(), "xx.apk");

                httpUtils.download(updateInfo.downloadurl, file.getAbsolutePath(), false, new RequestCallBack<File>() {
                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        //下载成功
                        //                        Log.i("ian", "SplashActivity onSuccess()");
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setDataAndType(Uri.fromFile(file),
                                "application/vnd.android.package-archive");
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        //下载失败
                        Log.i("ian", "SplashActivity onFailure()");
                    }
                });
            }
        }).create();
        dialog.show();
    }

    //初始化控件
    private void initViews() {
        rlLayout = (RelativeLayout) findViewById(R.id.rl_layout);
        tvVersionName = (TextView) findViewById(R.id.tv_version_name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();
        //获取版本名称
        String versionName = AppInfoUtils.getVersionName(SplashActivity.this);
        //将版本信息显示在启动画面
        tvVersionName.setText(versionName);
        //设置启动动画
        AlphaAnimation animation = new AlphaAnimation(0.2f, 1.0f);
        animation.setDuration(2000);
        rlLayout.startAnimation(animation);
        mSp = getSharedPreferences("config", MODE_PRIVATE);
        copyDb("address.db");
        copyDb("commonnum.db");
        copyDb("antivirus.db");
        createShortCut();
        createStatusBar();
        new Thread() {
            public void run() {
                updateVirus();

            }
        }.start();
        new Thread() {
            public void run() {
                //                if (mSp.getBoolean("update", true)) {
                //                    checkVersion();
                //                } else {
                //                }
                IntentUtils.startActivityForDelayAndFinished(SplashActivity.this,
                        HomeActivity.class, 2000);
            }
        }.start();
    }
    //显示顶层通知栏
    private void createStatusBar() {
        //高版本
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("MyMobileSafe")
                        .setContentText("正在保护你的手机");
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());

        //低版本
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification noti = new Notification(R.mipmap.ic_launcher, "小码哥卫士正在保护您的手机", System.currentTimeMillis());
//        noti.flags = Notification.FLAG_ONGOING_EVENT|Notification.FLAG_NO_CLEAR;
//        Intent intent = new Intent();
//        intent.setAction("com.m520it.mobilsafe.home");
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        noti.setLatestEventInfo(this, "小码哥卫士", "正在保护您的手机", contentIntent);
//        mNotificationManager.notify(0, noti );
    }

    //生成一个桌面快捷方式
    private void createShortCut() {
        //用SharedPreferences保存注册状态,否则每次安装都会生成一个桌面快捷方式
        boolean ret = mSp.getBoolean("isShortCut", false);
        if (!ret) {
            //准备意图和action
            Intent intent = new Intent();
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            //发送图标
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            //发送应用名
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "hehehe");
            //准备一个新意图
            Intent newIntent = new Intent();
            newIntent.setAction("com.ian.mobilesafe.shortcut");
            newIntent.addCategory(Intent.CATEGORY_DEFAULT);
            newIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            //将点击桌面回到首页
            sendBroadcast(newIntent);
            SharedPreferences.Editor edit = mSp.edit();
            edit.putBoolean("isShortCut", true);
            edit.commit();
        }


    }

    private void updateVirus() {
        try {
            //创建url
            URL url = new URL(getString(R.string.virus_url));
            //打开连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);

            //获取响应码
            int code = conn.getResponseCode();
            //获取输入流
            if (code == 200) {
                InputStream stream = conn.getInputStream();
                String js = StringUtils.getJsonString(stream);
                JSONObject jsonObject = new JSONObject(js);
                //获取版本信息
                int version = jsonObject.getInt("version");
                String md5 = jsonObject.getString("md5");
                String desc = jsonObject.getString("desc");
                String name = jsonObject.getString("name");
                String type = jsonObject.getString("type");
                //需要更新
                if (version > VirusDao.getVersion()) {
                    VirusDao.setVersion(version);
                    VirusDao.addVirus(name, md5, desc, type);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //加载本地数据库
    private void copyDb(final String dbName) {
        //耗时操作放在子线程
        new Thread() {
            public void run() {
                //判断是否已经复制过
                File file = new File(getFilesDir(), dbName);
                if (file.exists() && file.length() > 0) {
                    return;
                }
                try {
                    InputStream is = getAssets().open(dbName);
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //检查版本是否最新
    private void checkVersion() {
        try {
            //创建url
            URL url = new URL(getString(R.string.version_url));
            //打开连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);

            //获取响应码
            int code = conn.getResponseCode();
            //获取输入流
            if (code == 200) {
                InputStream stream = conn.getInputStream();
                String js = StringUtils.getJsonString(stream);
                JSONObject jsonObject = new JSONObject(js);
                //获取版本信息
                updateInfo = new UpdateInfo();
                updateInfo.version = jsonObject.getInt("version");
                updateInfo.downloadurl = jsonObject.getString("downloadurl");
                updateInfo.desc = jsonObject.getString("desc");
                //需要更新
                if (updateInfo.version > AppInfoUtils.getVersionCode(SplashActivity.this)) {
                    Message message = Message.obtain();
                    message.obj = updateInfo;
                    message.what = NEED_UPDATE;
                    handler.sendMessageDelayed(message, 2000);
                } else {//不需要更新,跳转到主页
                    IntentUtils.startActivityForDelayAndFinished(SplashActivity.this,
                            HomeActivity.class, 2000);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
