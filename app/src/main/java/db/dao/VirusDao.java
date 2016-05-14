package db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ChanIan on 16/5/12.
 */
public class VirusDao {
    private static String tableName1 = "datable";
    private static String tableName2 = "version";

    //判断是否是病毒
    public static String isVirus(String md5) {
        String ret = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                "/data/data/com.testdemo.chanian.mymobilesafe/files/antivirus.db",
                null, SQLiteDatabase.OPEN_READWRITE);
        Cursor cursor = db.query(tableName1, new String[]{"md5"}, "md5=?",
                new String[]{md5}, null, null, null);
        if (cursor.moveToNext()) {
            ret = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return ret;
    }

    //得到当前病毒版本号
    public static int getVersion() {
        int version = 0;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                "/data/data/com.testdemo.chanian.mymobilesafe/files/antivirus.db",
                null, SQLiteDatabase.OPEN_READWRITE);
        Cursor cursor = db.query(tableName2, new String[]{"subcnt"}, null, null, null, null, null);
        if (cursor.moveToNext()) {
            version = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return version;

    }

    //重新设置版本号
    public static void setVersion(int version) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                "/data/data/com.testdemo.chanian.mymobilesafe/files/antivirus.db",
                null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("subcnt",version);
        db.update(tableName2, values, null, null);
        db.close();
    }

    public static void addVirus(String name, String md5, String desc, String type) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                "/data/data/com.testdemo.chanian.mymobilesafe/files/antivirus.db",
                null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("md5", md5);
        values.put("desc", desc);
        values.put("type", type);
        db.insert(tableName1, null, values);
        db.close();
    }

}
