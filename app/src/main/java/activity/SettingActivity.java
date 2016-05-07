package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.testdemo.chanian.mymobilesafe.R;

import service.CallPhoneBlackServicve;
import service.ShowAddressService;
import ui.ChangeUi;
import ui.SetRelativeLayout;
import utils.CheckRunningServiceUtils;

/**
 * Created by ChanIan on 16/5/1.
 */
public class SettingActivity extends Activity {
    private SetRelativeLayout set_rel;
    private SetRelativeLayout set_black;
    private SetRelativeLayout set_Address;
    private ChangeUi change_address_color;
    private SharedPreferences mSp;

    private String[] mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSp = getSharedPreferences("config", MODE_PRIVATE);
        set_black = (SetRelativeLayout) findViewById(R.id.set_black);
        set_rel = (SetRelativeLayout) findViewById(R.id.set_rel);
        set_rel.setChecked(mSp.getBoolean("update", false));
        mItems = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
        set_Address = (SetRelativeLayout)findViewById(R.id.set_Address);
        change_address_color = (ChangeUi)findViewById(R.id.change_address_color);
        change_address_color.setColorForSmallText(mItems[mSp.getInt("which",0)]);
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
        //监听checkbox的选中状态->通话归属地显示
        set_Address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ShowAddressService.class);
                if (set_Address.getChecked()) {
                    set_Address.setChecked(false);//关闭服务
                    stopService(intent);
                } else {
                    set_Address.setChecked(true);//开启服务
                    startService(intent);
                }
            }
        });
    }
    //改变提示框背景
    private int checkedItem=0;
    public void changeColor(View view) {

        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("请选择主题颜色").setSingleChoiceItems(mItems, checkedItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem=which;
                        SharedPreferences.Editor edit = mSp.edit();
                        edit.putInt("which",which);
                        edit.commit();
                        dialog.dismiss();
                        //设置主题
                        change_address_color.setColorForSmallText(mItems[checkedItem]);
                    }
                }).create();
        dialog.show();
    }
    //记录拦截服务开启状态
    @Override
    protected void onStart() {
        boolean state = CheckRunningServiceUtils.getServiceRunningState(this, "service.CallPhoneBlackServicve");
        set_black.setChecked(state);
        boolean state1 = CheckRunningServiceUtils.getServiceRunningState(this, "service.ShowAddressService");
        set_Address.setChecked(state1);
        super.onStart();
    }
}
