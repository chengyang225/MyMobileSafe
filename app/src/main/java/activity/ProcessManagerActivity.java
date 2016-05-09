package activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.testdemo.chanian.mymobilesafe.R;

/**
 * Created by ChanIan on 16/5/9.
 */
public class ProcessManagerActivity extends Activity {
    private TextView tv_process_number;
    private TextView tv_memory_status;
    private TextView tv_process_status;
    private LinearLayout ll_loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        tv_process_number = (TextView)findViewById(R.id.tv_process_number);
        tv_memory_status = (TextView)findViewById(R.id.tv_memory_status);
        ll_loading = (LinearLayout)findViewById(R.id.ll_loading);
        tv_process_status = (TextView)findViewById(R.id.tv_process_status);
    }
}
