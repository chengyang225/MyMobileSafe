package activity;

import android.os.Bundle;

import com.testdemo.chanian.mymobilesafe.R;

import utils.IntentUtils;

/**
 * Created by ChanIan on 16/5/2.
 */
public class Setup4Activity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
    }

    @Override
    public void showNext() {
        IntentUtils.startActivityAndFinished(Setup4Activity.this, LostFindActivity.class);
    }

    @Override
    public void showPre() {
        IntentUtils.startActivityAndFinished(Setup4Activity.this, Setup3Activity.class);
    }
}
