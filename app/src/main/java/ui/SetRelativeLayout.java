package ui;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.testdemo.chanian.mymobilesafe.R;

/**
 * Created by ChanIan on 16/5/1.
 */
public class SetRelativeLayout extends RelativeLayout {
    private CheckBox cbUpdate;
    public boolean getChecked(){
        return cbUpdate.isChecked();
    }
    public void setChecked(boolean checked){
        cbUpdate.setChecked(checked);
    }
    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.set_rel,this);
        cbUpdate = (CheckBox) findViewById(R.id.cb_update);

    }

    public SetRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

}
