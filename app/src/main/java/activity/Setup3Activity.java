package activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.testdemo.chanian.mymobilesafe.R;

import utils.IntentUtils;

/**
 * Created by ChanIan on 16/5/2.
 */
public class Setup3Activity extends BaseActivity {
    private EditText et_safe_num;
    private SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        et_safe_num = (EditText) findViewById(R.id.et_safe_num);
        //安全号码回显
        String safeNum = sp.getString("safeNum", "");
        et_safe_num.setText(safeNum);
        edit = sp.edit();
    }

    //跳转到选择通讯录界面
    public void selectClick(View view) {
        Intent intent = new Intent(this, SelectContactActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {//有号码传回来
            String safeNum = data.getStringExtra("phone").trim();
            et_safe_num.setText(safeNum);
        }
    }

    @Override
    public void showNext() {
        String safeNum = et_safe_num.getText().toString().trim();
        //判断安全号码是否为空
        if (TextUtils.isEmpty(safeNum)) {
            Toast.makeText(Setup3Activity.this, "安全号码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            edit.putString("safeNum",safeNum);
            edit.commit();
            IntentUtils.startActivityAndFinished(Setup3Activity.this, Setup4Activity.class);
        }
    }

    @Override
    public void showPre() {
        IntentUtils.startActivityAndFinished(Setup3Activity.this, Setup2Activity.class);
    }
}
