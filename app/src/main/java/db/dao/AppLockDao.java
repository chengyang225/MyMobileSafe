package db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import db.AppLockHelper;

/**
 * Created by ChanIan on 16/5/10.
 */
//对数据库进行增删改查操作
public class AppLockDao  {
    private AppLockHelper mHelper;
    private String tableName="t_applock";
    public AppLockDao(Context context) {
        mHelper = new AppLockHelper(context);
    }
    //增加元素
    public boolean insert(String packName){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("packname",packName);
        long ret = db.insert(tableName, null, values);
        db.close();
        if(ret!=-1) {
            return true;
        }
        return false;
    }
    //删除元素
    public boolean delete(String packName){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int rows = db.delete(tableName, "packname=?", new String[]{packName});
        db.close();
        if(rows>0) {
            return true;
        }
        return false;
    }
    //查询了元素
    public boolean query(String packName){
        boolean ret=false;
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{"packname"}, "packname=?", new String[]{packName}
                , null, null, null);
        ret = cursor.moveToNext();
        cursor.close();
        db.close();
        return ret;
    }
}
