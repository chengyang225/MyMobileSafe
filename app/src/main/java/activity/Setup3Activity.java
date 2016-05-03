package activity;

import android.os.Bundle;

import com.testdemo.chanian.mymobilesafe.R;

import utils.IntentUtils;

/**
 * Created by ChanIan on 16/5/2.
 */
public class Setup3Activity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
    }

    @Override
    public void showNext() {
        IntentUtils.startActivityAndFinished(Setup3Activity.this,Setup4Activity.class);
    }

    @Override
    public void showPre() {
        IntentUtils.startActivityAndFinished(Setup3Activity.this,Setup2Activity.class);
    }
}
