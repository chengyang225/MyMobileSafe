package activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.testdemo.chanian.mymobilesafe.R;

import utils.IntentUtils;

/**
 * Created by ChanIan on 16/5/6.
 */
public class AToolAcyivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);
    }
    //归属地查询
    public void queryLocationClick(View view){
        IntentUtils.startActivity(this,QueryAddressLocationActivity.class);
    }
    //公共号码查询
    public void queryCommonsClick(View view){
        IntentUtils.startActivity(this,CommonsActivity.class);
    }
}
