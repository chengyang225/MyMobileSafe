package activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.testdemo.chanian.mymobilesafe.R;

import ui.SetRelativeLayout;

/**
 * Created by ChanIan on 16/5/1.
 */
public class SettingActivity extends Activity {
    private SetRelativeLayout set_rel;
    private SharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSp=getSharedPreferences("config",MODE_PRIVATE);
        set_rel = (SetRelativeLayout)findViewById(R.id.set_rel);
        if(mSp.getBoolean("update",false)) {
            set_rel.setChecked(true);
        }else {
            set_rel.setChecked(false);
        }
        //监听checkbox的选中状态
        set_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = mSp.edit();
               //将自动更新状态保存到本地
                if(set_rel.getChecked()) {
                    set_rel.setChecked(false);
                    edit.putBoolean("update",false);
                }else {
                    set_rel.setChecked(true);
                    edit.putBoolean("update",true);
                }
                edit.commit();

            }
        });
    }
}
