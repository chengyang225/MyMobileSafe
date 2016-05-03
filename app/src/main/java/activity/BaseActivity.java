package activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.testdemo.chanian.mymobilesafe.R;

/**
 * Created by ChanIan on 16/5/2.
 */
public abstract class BaseActivity extends Activity{
    private GestureDetector mGestureDetector;
    public SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        mGestureDetector=new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(Math.abs(e1.getRawY()-e2.getRawY())>100) {

                    return true;
                }

                if(e1.getRawX()-e2.getRawX()>100) {
                    showNext();
                    overridePendingTransition(R.anim.trans_next_in,R.anim.trans_next_out);
                    return true;
                }

                if(e2.getRawX()-e1.getRawX()>100) {
                    showPre();
                    overridePendingTransition(R.anim.pre_next_in,R.anim.pre_next_out);
                    return  true;
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }
    public abstract void showNext();
    public abstract void showPre();
    public void next(View view){
        showNext();
        overridePendingTransition(R.anim.trans_next_in,R.anim.trans_next_out);
    }
    public void pre(View view){
        showPre();
        overridePendingTransition(R.anim.pre_next_in,R.anim.pre_next_out);
    }
}
