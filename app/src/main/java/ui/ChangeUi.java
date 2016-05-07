package ui;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testdemo.chanian.mymobilesafe.R;

/**
 * Created by ChanIan on 16/5/6.
 */
public class ChangeUi extends RelativeLayout {

    private TextView tv_big_tittle;
    private TextView tv_small_tittle;

    public ChangeUi(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
        String mybigtext = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "mybigtext");
        String mysmalltext = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "mysmalltext");
        tv_big_tittle.setText(mybigtext);
        tv_small_tittle.setText(mysmalltext);
    }
    public void setColorForSmallText(String color){
        tv_small_tittle.setText(color);
    }
    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_ui_change,this);
        tv_big_tittle = (TextView) findViewById(R.id.tv_big_tittle);
        tv_small_tittle = (TextView) findViewById(R.id.tv_small_tittle);
    }
}
