package activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.testdemo.chanian.mymobilesafe.R;

import ui.SetRelativeLayout;

/**
 * Created by ChanIan on 16/5/9.
 */
public class ProcessSettingActivity extends Activity {
    private SetRelativeLayout ui_show_sys_process;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        ui_show_sys_process = (SetRelativeLayout)findViewById(R.id.ui_show_sys_process);
        ui_show_sys_process.setChecked(sp.getBoolean("processSet",true));
        ui_show_sys_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = sp.edit();
                if(ui_show_sys_process.getChecked()) {
                    ui_show_sys_process.setChecked(false);
                    edit.putBoolean("processSet",false);
                }else {
                    ui_show_sys_process.setChecked(true);
                    edit.putBoolean("processSet",true);

                }
                edit.commit();
            }
        });
    }
}
