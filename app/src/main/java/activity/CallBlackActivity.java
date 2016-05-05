package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.testdemo.chanian.mymobilesafe.R;

import java.util.List;

import bean.BlackNumberInfo;
import db.dao.BlackNumberDao;
import utils.ToastUtils;

/**
 * Created by ChanIan on 16/5/4.
 */
public class CallBlackActivity extends Activity {
    private ListView iv_black_lists;
    private List<BlackNumberInfo> mInfos;
    private BlackNumberDao mDao;
    private Button btn_addBlack_cancel;
    private Button btn_addBlack_confirm;
    private AlertDialog mDialog;
    private CallNumberAdapter mAdapter;
    private LinearLayout ll_loading;
    private int startIndex = 0;
    private int loadCount = 20;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ll_loading.setVisibility(View.INVISIBLE);
            super.handleMessage(msg);
            //避免每次刷新都创建一个新的适配器
            if (mAdapter == null) {
                mAdapter = new CallNumberAdapter();
                iv_black_lists.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callnumber);
        iv_black_lists = (ListView) findViewById(R.id.iv_black_lists);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        LinearLayout add_ll = (LinearLayout) findViewById(R.id.add_black_dialog);
        mDao = new BlackNumberDao(this);
        iv_black_lists.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //滚动结束状态停在当前位置
                if (AbsListView.OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
                    //得到listview最后一个item
                    int position = iv_black_lists.getLastVisiblePosition();
                    if (position == mInfos.size() - 1) {//滑到最后一个继续加载
                        startIndex += loadCount;
                        loadDatas();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mInfos = mDao.queryAll();

        loadDatas();

    }

    private void loadDatas() {
        ll_loading.setVisibility(View.VISIBLE);
        //开启多线程加载数据库,避免阻塞
        new Thread() {
            public void run() {
                if (mInfos == null) {//第一次加载
                    mInfos = mDao.queryAndLoad(startIndex, loadCount);
                } else {
                    mInfos.addAll(mDao.queryAndLoad(startIndex, loadCount));
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    class Holder {
        TextView tv_black_number;
        TextView tv_black_mode;
        ImageView iv_delete_black;
    }

    class CallNumberAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mInfos.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(CallBlackActivity.this).inflate(R.layout.black_item, null);
                holder.tv_black_number = (TextView) convertView.findViewById(R.id.tv_black_number);
                holder.tv_black_mode = (TextView) convertView.findViewById(R.id.tv_black_mode);
                holder.iv_delete_black = (ImageView) convertView.findViewById(R.id.iv_delete_black);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            BlackNumberInfo info = mInfos.get(position);
            final String phone = info.getPhone();
            holder.tv_black_number.setText(phone);
            String mode = info.getMode();
            switch (mode) {
                case "1":
                    mode = "电话拦截";
                    break;

                case "2":
                    mode = "短信拦截";
                    break;

                case "3":
                    mode = "全部拦截";
                    break;

                default:
                    mode = "全部拦截";
                    break;
            }
            holder.tv_black_mode.setText(mode);

            holder.iv_delete_black.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(CallBlackActivity.this).setTitle("确认删除").
                            setMessage("请确认删除").setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean ret = mDao.delete(phone);
                                    mInfos.remove(position);
                                    mAdapter.notifyDataSetChanged();
                                    if (ret) {
                                        ToastUtils.getToast(CallBlackActivity.this, "删除成功");
                                    } else {
                                        ToastUtils.getToast(CallBlackActivity.this, "删除失败");
                                    }
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
                    dialog.show();
                }
            });
            return convertView;
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

    public void addClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View inflate = LayoutInflater.from(this).inflate(R.layout.add_black_dialog, null);
        final RadioGroup mGroup = (RadioGroup) inflate.findViewById(R.id.rg_black);
        final EditText et_black = (EditText) inflate.findViewById(R.id.et_black);
        btn_addBlack_confirm = (Button) inflate.findViewById(R.id.btn_addBlack_confirm);
        btn_addBlack_cancel = (Button) inflate.findViewById(R.id.btn_addBlack_cancel);
        btn_addBlack_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //点击确定将数据保存到数据库并显示到页面
        btn_addBlack_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户输入的黑名单
                String phone = et_black.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.getToast(CallBlackActivity.this, "黑名单号码不为空");
                    return;
                }
                //拦截模式
                String mode = "3";
                int modeId = mGroup.getCheckedRadioButtonId();
                switch (modeId) {
                    case R.id.rb_phone:
                        mode = "1";
                        break;
                    case R.id.rb_sms:
                        mode = "2";
                        break;
                    case R.id.rb_all:
                        mode = "3";
                        break;
                }
                boolean ret = mDao.insert(phone, mode);
                if (ret) {//添加成功
                    BlackNumberInfo info = new BlackNumberInfo();
                    info.setMode(mode);
                    info.setPhone(phone);
                    mInfos.add(0, info);
                    mAdapter.notifyDataSetChanged();
                    ToastUtils.getToast(CallBlackActivity.this, "添加成功");
                } else {
                    ToastUtils.getToast(CallBlackActivity.this, "添加失败");

                }
                mDialog.dismiss();
            }
        });
        mDialog = builder.create();
        mDialog.setView(inflate, 0, 0, 0, 0);
        mDialog.show();
    }
}
