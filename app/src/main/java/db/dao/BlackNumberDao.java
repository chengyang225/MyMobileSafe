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
        mHelper = new BlackNumberDbHelper(context);
    }

    public boolean insert(String phone, String mode) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("mode", mode);
        long id = db.insert("t_black", null, values);
        db.close();
        if (id != -1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean delete(String phone) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int id = db.delete("t_black", "phone=?", new String[]{phone});
        if (id != 0) {
            return true;
        } else {
            return false;
        }
    }

    public String query(String phone) {
        String mode = null;
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query("t_black", new String[]{"mode"},
                "phone=?", new String[]{phone}, null, null, null);
        while (cursor.moveToNext()) {
            mode = cursor.getString(0);
        }
        db.close();
        return mode;
    }

    public List<BlackNumberInfo> queryAll() {
        List<BlackNumberInfo> infos = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query("t_black", null,
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            info.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            info.setMode(cursor.getString(cursor.getColumnIndex("mode")));
            infos.add(info);
        }
        cursor.close();
        db.close();
        return infos;
    }

    public List<BlackNumberInfo> queryAndLoad(int startIndex, int loadCount) {
        List<BlackNumberInfo> infos = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM t_black ORDER BY \"_id\" DESC LIMIT ? OFFSET ?",
                new String[]{String.valueOf(startIndex), String.valueOf(loadCount)});
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            info.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            info.setMode(cursor.getString(cursor.getColumnIndex("mode")));
            infos.add(info);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();
        return infos;
    }
    public int queryCount() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM t_black", null);
        while (cursor.moveToNext()) {
            return cursor.getInt(0);
        }
        db.close();
        return -1;
    }
}
