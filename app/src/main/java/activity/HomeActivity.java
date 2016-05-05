package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.testdemo.chanian.mymobilesafe.R;

import utils.IntentUtils;
import utils.ToastUtils;

/**
 * Created by ChanIan on 16/5/1.
 */
public class HomeActivity extends Activity {
    private GridView gv_home;
    private String[] names = {"手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计",
            "手机杀毒", "缓存清理", "高级工具", "手机设置"};
    private int[] icons = {R.mipmap.a, R.mipmap.b, R.mipmap.c, R.mipmap.d,
            R.mipmap.e, R.mipmap.f, R.mipmap.g, R.mipmap.h, R.mipmap.i};
    private SharedPreferences mSp;
    private View mView;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mSp = getSharedPreferences("config", MODE_PRIVATE);
        gv_home = (GridView) findViewById(R.id.gv_home);
        HomeAdapter adapter = new HomeAdapter();
        gv_home.setAdapter(adapter);
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到设置界面
                switch (position) {
                    case 0:
                        String password = mSp.getString("password", "");
                        if (TextUtils.isEmpty(password)) {
                            //密码为空,第一次需设置密码
                            showSetupDialog();
                        } else {
                            //有密码,弹出对话框验证密码
                            showEnterDialog();
                        }
                        break;
                    case 1:
                        IntentUtils.startActivity(HomeActivity.this, CallBlackActivity.class);
                        break;
                    case 8:
                        IntentUtils.startActivity(HomeActivity.this, SettingActivity.class);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private EditText etSetPwd;
    private EditText etConfirmPwd;
    private Button btnOk;
    private Button btnCancel;

    private void initViews() {
        mView = LayoutInflater.from(this).inflate(R.layout.setup_password, null);
        etSetPwd = (EditText) mView.findViewById(R.id.et_set_pwd);
        etConfirmPwd = (EditText) mView.findViewById(R.id.et_confirm_pwd);
        btnOk = (Button) mView.findViewById(R.id.btn_ok);
        btnCancel = (Button) mView.findViewById(R.id.btn_cancel);
    }

    //验证密码
    private void showEnterDialog() {
        mView = LayoutInflater.from(this).inflate(R.layout.enter_password, null);
        etSetPwd = (EditText) mView.findViewById(R.id.et_set_pwd);
        btnOk = (Button) mView.findViewById(R.id.btn_ok);
        btnCancel = (Button) mView.findViewById(R.id.btn_cancel);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String setPwd = etSetPwd.getText().toString();
                if (TextUtils.isEmpty(setPwd)) {
                    ToastUtils.getToast(HomeActivity.this, "密码不能为空");
                    return;
                }
                //验证密码
                String password = mSp.getString("password", "");
                if (!password.equals(setPwd)) {
                    ToastUtils.getToast(HomeActivity.this, "密码错误");
                    return;
                }
                //已完成设置
                if(mSp.getBoolean("finishSetup",false)) {
                    IntentUtils.startActivity(HomeActivity.this,LostFindActivity.class);
                }else {
                    IntentUtils.startActivity(HomeActivity.this,Setup1Activity.class);
                }
                mDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog = new AlertDialog.Builder(this).setView(mView).create();
        mDialog.show();
    }

    //第一次设置密码
    private void showSetupDialog() {
        initViews();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String setPwd = etSetPwd.getText().toString();
                String conPwd = etConfirmPwd.getText().toString();
                if (TextUtils.isEmpty(setPwd) || TextUtils.isEmpty(conPwd)) {
                    ToastUtils.getToast(HomeActivity.this, "密码不能为空");

                    return;
                }
                if (!setPwd.equals(conPwd)) {
                    ToastUtils.getToast(HomeActivity.this, "输入密码不一致");
                    return;
                }
                //将密码保存
                SharedPreferences.Editor edit = mSp.edit();
                edit.putString("password", setPwd);
                edit.commit();
                mDialog.dismiss();
                showEnterDialog();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog = new AlertDialog.Builder(this).setView(mView).create();
        mDialog.show();
    }

    class HomeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, null);
            ImageView iv_item_icon = (ImageView) view.findViewById(R.id.iv_item_icon);
            iv_item_icon.setImageResource(icons[position]);
            TextView tv_item_name = (TextView) view.findViewById(R.id.tv_item_name);
            tv_item_name.setText(names[position]);
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


    }
}
