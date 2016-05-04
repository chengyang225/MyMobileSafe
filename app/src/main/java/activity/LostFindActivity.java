package activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.testdemo.chanian.mymobilesafe.R;

import utils.IntentUtils;

/**
 * Created by ChanIan on 16/5/2.
 */
public class LostFindActivity extends Activity {

    private TextView tv_safe_num;
    private ImageView iv_lock_icon;
    private SharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostfind);
        tv_safe_num = (TextView)findViewById(R.id.tv_safe_num);
        iv_lock_icon = (ImageView)findViewById(R.id.iv_lock_icon);
        mSp=getSharedPreferences("config",MODE_PRIVATE);
        String safeNum = mSp.getString("safeNum", "");
        tv_safe_num.setText(safeNum);
        if(mSp.getBoolean("protected",false)) {//防盗保护开启
            iv_lock_icon.setImageResource(R.mipmap.lock);
        }else {
            iv_lock_icon.setImageResource(R.mipmap.unlock);

        }
    }
    public void resetSetup(View view){
        IntentUtils.startActivityAndFinished(LostFindActivity.this,Setup1Activity.class);
    }
}
