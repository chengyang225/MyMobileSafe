package activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.testdemo.chanian.mymobilesafe.R;

import receiver.MyReceiver;
import receiver.SMSReceiver;
import ui.SetRelativeLayout;
import utils.IntentUtils;

/**
 * Created by ChanIan on 16/5/2.
 */
public class Setup4Activity extends BaseActivity {
    private SetRelativeLayout set_ui_cb;
    private SharedPreferences.Editor mEdit;
    private ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        set_ui_cb = (SetRelativeLayout) findViewById(R.id.set_ui_cb);
        mEdit = sp.edit();
        componentName = new ComponentName(this, MyReceiver.class);
        boolean status = sp.getBoolean("protected", false);
        set_ui_cb.setChecked(status);
        set_ui_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //记录防盗保护状态
                if (set_ui_cb.getChecked()) {
                    set_ui_cb.setChecked(false);
                    mEdit.putBoolean("protected", false);
                } else {
                    set_ui_cb.setChecked(true);
                    mEdit.putBoolean("protected", true);
                }
                mEdit.commit();
            }
        });
    }

    @Override
    public void showNext() {
        mEdit.putBoolean("finishSetup",true);
        mEdit.commit();
        IntentUtils.startActivityAndFinished(Setup4Activity.this, LostFindActivity.class);
    }

    @Override
    public void showPre() {
        IntentUtils.startActivityAndFinished(Setup4Activity.this, Setup3Activity.class);
    }
    public void onstarted(View view){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "快来下载,有奖金");
        startActivity(intent);
    }
}
