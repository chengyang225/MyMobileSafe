package db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import db.AppLockHelper;

/**
 * Created by ChanIan on 16/5/10.
 */
//对数据库进行增删改查操作
public class AppLockDao {
    private Context mContext;
    private AppLockHelper mHelper;
    private String tableName = "t_applock";

    public AppLockDao(Context context) {
        mHelper = new AppLockHelper(context);
        mContext = context;
    }

    //增加元素
    public boolean insert(String packName) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packname", packName);
        long ret = db.insert(tableName, null, values);
        db.close();
        if (ret != -1) {
            //创建内容观察者监测数据库改变
            Uri uri = Uri.parse("content://com.ian.lockdb.changed");
            mContext.getContentResolver().notifyChange(uri, null);
            return true;

        }
        return false;
    }

    //删除元素
    public boolean delete(String packName) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int rows = db.delete(tableName, "packname=?", new String[]{packName});
        db.close();
        if (rows > 0) {
            //创建内容观察者监测数据库改变
            Uri uri = Uri.parse("content://com.ian.lockdb.changed");
            mContext.getContentResolver().notifyChange(uri, null);
            return true;
        }
        return false;
    }

    //查询元素
    public boolean query(String packName) {
        boolean ret = false;
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{"packname"}, "packname=?", new String[]{packName}
                , null, null, null);
        ret = cursor.moveToNext();
        cursor.close();
        db.close();
        return ret;
    }

    //查询所有元素
    public List<String> queryAll() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(tableName, null, null, null
                , null, null, null);
        List<String> lists = new ArrayList<>();
        while (cursor.moveToNext()) {
            String packname = cursor.getString(cursor.getColumnIndex("packname"));
            lists.add(packname);
        }
        cursor.close();
        db.close();
        return lists;
    }
}
