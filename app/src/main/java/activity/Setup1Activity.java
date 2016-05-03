package activity;

import android.os.Bundle;

import com.testdemo.chanian.mymobilesafe.R;

import utils.IntentUtils;

/**
 * Created by ChanIan on 16/5/2.
 */
public class Setup1Activity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void showNext() {
        IntentUtils.startActivityAndFinished(Setup1Activity.this,Setup2Activity.class);
    }

    @Override
    public void showPre() {

    }
}
