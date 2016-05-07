package activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.testdemo.chanian.mymobilesafe.R;

import db.dao.CommonsNumberDao;

/**
 * Created by ChanIan on 16/5/7.
 */
public class CommonsActivity extends Activity {
    private ExpandableListView ev_commons;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commons);
        ev_commons = (ExpandableListView)findViewById(R.id.ev_commons);
        mDb = SQLiteDatabase.openDatabase("/data/data/com.testdemo.chanian.mymobilesafe/files/commonnum.db",
                null, SQLiteDatabase.OPEN_READWRITE);
        MyAdapter myAdapter = new MyAdapter();
        ev_commons.setAdapter(myAdapter);
    }
    class MyAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {

            return CommonsNumberDao.getGroupCount(mDb);
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return CommonsNumberDao.getChildrenCount(groupPosition,mDb);
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView;
            if(convertView==null) {
                convertView= LayoutInflater.from(parent.getContext()).
                        inflate(android.R.layout.simple_list_item_1,null);
                textView = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(textView);
            }else {
                textView= (TextView) convertView.getTag();
            }
            textView.setText(CommonsNumberDao.getGroupViewName(groupPosition,mDb));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView textView;
            if(convertView==null) {
                convertView= LayoutInflater.from(parent.getContext()).
                        inflate(android.R.layout.simple_list_item_2,null);
                textView = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(textView);
            }else {
                textView= (TextView) convertView.getTag();
            }
            textView.setText(CommonsNumberDao.getChildViewName(groupPosition,childPosition,mDb));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
