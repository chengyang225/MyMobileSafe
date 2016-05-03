package activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.testdemo.chanian.mymobilesafe.R;

import ui.SetRelativeLayout;
import utils.IntentUtils;
import utils.ToastUtils;

/**
 * Created by ChanIan on 16/5/2.
 */
public class Setup2Activity extends BaseActivity {
    private SetRelativeLayout set_ui_cb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        set_ui_cb = (SetRelativeLayout) findViewById(R.id.set_ui_cb);
        final TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String sim = sp.getString("sim", null);
        if (TextUtils.isEmpty(sim)) {//sim为空
            set_ui_cb.setChecked(false);
        } else {
            set_ui_cb.setChecked(true);
        }
        //给checkbox添加点击事件
        set_ui_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = sp.edit();
                if (set_ui_cb.getChecked()) {//被选中
                    set_ui_cb.setChecked(false);
                    edit.putString("sim", null);
                } else {
                    set_ui_cb.setChecked(true);
                    String number = tm.getSimSerialNumber();
                    edit.putString("sim", number);
                }
                edit.commit();
            }
        });
    }

    @Override
    public void showNext() {
        if (set_ui_cb.getChecked()) {

            IntentUtils.startActivityAndFinished(Setup2Activity.this, Setup3Activity.class);
        }else {
            ToastUtils.getToast(Setup2Activity.this,"请绑定sim卡!");
        }
    }

    @Override
    public void showPre() {
        IntentUtils.startActivityAndFinished(Setup2Activity.this, Setup1Activity.class);
    }
}
