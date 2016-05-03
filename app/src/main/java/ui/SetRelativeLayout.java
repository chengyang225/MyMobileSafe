package ui;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testdemo.chanian.mymobilesafe.R;

/**
 * Created by ChanIan on 16/5/1.
 */
public class SetRelativeLayout extends RelativeLayout {
    private CheckBox cbUpdate;
    private TextView tv_ui_name;
    public boolean getChecked(){
        return cbUpdate.isChecked();

    }
    public void setChecked(boolean checked){
        cbUpdate.setChecked(checked);
    }
    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.set_rel,this);
        tv_ui_name = (TextView)findViewById(R.id.tv_ui_name);
        cbUpdate = (CheckBox) findViewById(R.id.cb_update);

    }

    public SetRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
        String mytext = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "mytext");
        tv_ui_name.setText(mytext);
    }

}
