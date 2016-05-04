package db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import bean.BlackNumberInfo;
import db.BlackNumberDbHelper;

/**
 * Created by ChanIan on 16/5/4.
 */
public class BlackNumberDao {
    private BlackNumberDbHelper mHelper;

    public BlackNumberDao(Context context) {
        mHelper=new BlackNumberDbHelper(context);
    }
    public boolean insert(String phone,String mode){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone",phone);
        values.put("mode",mode);
        long id = db.insert("t_black", null, values);
        db.close();
        if(id!=-1) {
            return true;
        }else {
            return false;
        }
    }
    public boolean delete(String phone){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int id = db.delete("t_black", "phone=?", new String[]{phone});
        if(id!=0) {
            return true;
        }else {
            return false;
        }
    }
    public String query(String phone){
        String mode=null;
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query("t_table", new String[]{ "mode"},
                "phone=?", new String[]{phone}, null, null, null);
        while (cursor.moveToNext()){
            mode = cursor.getString(0);
        }
        db.close();
        return mode;
    }
    public List<BlackNumberInfo> queryAll(){
        List<BlackNumberInfo> infos=new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query("t_table", new String[]{"phone", "mode"},
                null, null, null, null, null);
        while (cursor.moveToNext()){
            BlackNumberInfo info = new BlackNumberInfo();
            info.setPhone(cursor.getString(0));
            info.setMode(cursor.getString(1));
        }
        db.close();
        return infos;
    }
}
