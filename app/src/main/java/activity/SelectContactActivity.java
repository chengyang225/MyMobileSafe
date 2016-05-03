package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.testdemo.chanian.mymobilesafe.R;

import java.util.List;

import utils.ContactInfoUtils;

/**
 * Created by ChanIan on 16/5/3.
 */
public class SelectContactActivity extends Activity{
    private ListView lv_contact;
    private List<ContactInfoUtils.ContactInfo> mInfos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiti_selectcontact);
        lv_contact = (ListView)findViewById(R.id.lv_contact);
        //获取通讯录数据
        mInfos = ContactInfoUtils.getContacts(this);
        //设置适配器
        lv_contact.setAdapter(new ContactAdapter());
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("phone",mInfos.get(position).phone);
                setResult(0,intent);
                finish();
            }
        });
    }
    class Holder{
        TextView tv_name;
        TextView tv_phone;
    }
    class ContactAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mInfos.size();
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder=null;
            if(convertView==null) {
                holder=new Holder();
                convertView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_select_contact,null);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_phoneName);
                holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phoneNum);
                convertView.setTag(holder);
            }else {
                holder= (Holder) convertView.getTag();
            }
            ContactInfoUtils.ContactInfo contactInfo = mInfos.get(position);
            holder.tv_name.setText(contactInfo.name);
            holder.tv_phone.setText(contactInfo.phone);
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
}
