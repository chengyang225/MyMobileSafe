package activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.testdemo.chanian.mymobilesafe.R;

import service.WatchDogService;

/**
 * Created by ChanIan on 16/5/12.
 */
public class EnterPwdActivity extends Activity {
    private ImageView iv_app_icon;
    private EditText et_enter_pwd;
    private TextView tv_app_name;
    private PackageManager mPm;
    private String mPackname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        iv_app_icon = (ImageView)findViewById(R.id.iv_app_icon);
        et_enter_pwd = (EditText)findViewById(R.id.et_enter_pwd);
        tv_app_name = (TextView)findViewById(R.id.tv_app_name);
        //设置加锁应用名和图片
        mPm = getPackageManager();
        Intent intent = getIntent();
        mPackname = intent.getStringExtra("packname");
        try {
            Drawable icon = mPm.getApplicationInfo(mPackname, 0).loadIcon(mPm);
            String name = mPm.getApplicationInfo(mPackname, 0).loadLabel(mPm).toString();
            tv_app_name.setText(name);
            iv_app_icon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    //重写系统返回键,直接返回桌面
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        //查看系统源码
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }
    //避免影响到下次启动加锁应用
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    public void enterClick(View view){
        String pwd = et_enter_pwd.getText().toString().trim();
        if("q".equals(pwd)) {//密码正确
            //发送广播,需要临时停止保护
            Intent intent = new Intent();
            intent.setAction("com.ian.watch.dog");
            intent.putExtra("name",mPackname);
            sendBroadcast(intent);
            finish();
        }else {//密码错误
           Toast.makeText(EnterPwdActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
        }
    }
}
