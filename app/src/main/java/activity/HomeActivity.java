package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.testdemo.chanian.mymobilesafe.R;

import utils.IntentUtils;

/**
 * Created by ChanIan on 16/5/1.
 */
public class HomeActivity extends Activity {
    private GridView gv_home;
    private String[] names={"手机防盗","通讯卫士","软件管理","进程管理","流量统计",
            "手机杀毒","缓存清理","高级工具","手机设置"};
    private int[] icons={R.mipmap.a,R.mipmap.b,R.mipmap.c,R.mipmap.d,
            R.mipmap.e,R.mipmap.f,R.mipmap.g,R.mipmap.h,R.mipmap.i};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        gv_home = (GridView)findViewById(R.id.gv_home);
        HomeAdapter adapter = new HomeAdapter();
        gv_home.setAdapter(adapter);
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到设置界面
                switch (position) {
                    case 8:
                        IntentUtils.startActivity(HomeActivity.this,SettingActivity.class);
                    break;

                    default:
                        break;
                }
            }
        });
    }

    class HomeAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return names.length;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, null);
            ImageView iv_item_icon= (ImageView) view.findViewById(R.id.iv_item_icon);
            iv_item_icon.setImageResource(icons[position]);
            TextView tv_item_name= (TextView) view.findViewById(R.id.tv_item_name);
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
