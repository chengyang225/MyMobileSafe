package activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.testdemo.chanian.mymobilesafe.R;

import service.CallPhoneBlackServicve;
import ui.SetRelativeLayout;
import utils.CheckRunningServiceUtils;

/**
 * Created by ChanIan on 16/5/1.
 */
public class SettingActivity extends Activity {
    private SetRelativeLayout set_rel;
    private SetRelativeLayout set_black;
    private SharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSp = getSharedPreferences("config", MODE_PRIVATE);
        set_black = (SetRelativeLayout) findViewById(R.id.set_black);
        set_rel = (SetRelativeLayout) findViewById(R.id.set_rel);
        set_rel.setChecked(mSp.getBoolean("update", false));

        //监听checkbox的选中状态->自动更新
        set_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = mSp.edit();
                //将自动更新状态保存到本地
                if (set_rel.getChecked()) {
                    set_rel.setChecked(false);
                    edit.putBoolean("update", false);
                } else {
                    set_rel.setChecked(true);
                    edit.putBoolean("update", true);
                }
                edit.commit();

            }
        });
        //监听checkbox的选中状态->黑名单拦截
        set_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, CallPhoneBlackServicve.class);
                if (set_black.getChecked()) {
                    set_black.setChecked(false);//关闭服务
                    stopService(intent);
                } else {
                    set_black.setChecked(true);//开启服务
                    startService(intent);
                }
            }
        });
    }
    //记录拦截服务开启状态
    @Override
    protected void onStart() {
        boolean state = CheckRunningServiceUtils.getServiceRunningState(this, "service.CallPhoneBlackServicve");
        set_black.setChecked(state);
        super.onStart();
    }
}
