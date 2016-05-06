package activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.testdemo.chanian.mymobilesafe.R;

import db.dao.NumberLocationDao;

/**
 * Created by ChanIan on 16/5/6.
 */
public class QueryAddressLocationActivity extends Activity {
    private EditText et_queryed_number;
    private TextView tv_location_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_location);
        et_queryed_number = (EditText)findViewById(R.id.et_queryed_number);
        tv_location_name = (TextView)findViewById(R.id.tv_location_name);
    }
    //归属地查询
    public void queryLocationClick(View view){
        String phone = et_queryed_number.getText().toString();
        String address = NumberLocationDao.getAddress(phone);
        tv_location_name.setText("查询到的地址是:"+address);
    }
}
