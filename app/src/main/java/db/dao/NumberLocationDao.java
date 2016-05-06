package db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ChanIan on 16/5/6.
 */
//查询手机归属地
public class NumberLocationDao {
    public static String getAddress(String phone) {
        //因为数据库在本地不是自己创建的,db不能跟以前一样
        String location = phone;
        if (phone.matches("^1[13578]\\d{9}$")) {//电话号码
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.testdemo.chanian.mymobilesafe/files/address.db",
                    null, SQLiteDatabase.OPEN_READWRITE);

            Cursor cursor = db.rawQuery("SELECT location FROM data2 WHERE id=(SELECT outkey FROM data1 WHERE id=?)",
                    new String[]{phone.substring(0, 7)});
            cursor.moveToNext();
            location = cursor.getString(0);
            return location;
        } else {
            switch (phone.length()) {
                case 3:
                    if ("110".equals(phone)) {
                        return "匪警";
                    }
                    if ("119".equals(phone)) {
                        return "火警";
                    }
                    if ("120".equals(phone)) {
                        return "医院";
                    }
                    break;
                case 4:
                    return "模拟器";
                case 5:
                    return "客服";
                case 7:
                    if (phone.startsWith("0") || phone.startsWith("1")) {
                        return phone;
                    }
                    return "本地电话";
                case 8:
                    if (phone.startsWith("0") || phone.startsWith("1")) {
                        return phone;
                    }
                    return "本地电话";
                default:
                    if (phone.length() > 10 && phone.startsWith("0")) {
                        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.testdemo.chanian.mymobilesafe/files/address.db",
                                null, SQLiteDatabase.OPEN_READWRITE);
                        Cursor cursor = db.rawQuery("SELECT location FROM data2 WHERE area=?",
                                new String[]{phone.substring(1, 3)});
                        if (cursor.moveToNext()) {

                            location = cursor.getString(0).substring(0, cursor.getString(0).length() - 2);
                        }
                        cursor.close();
                        cursor = db.rawQuery("SELECT location FROM data2 WHERE area=?",
                                new String[]{phone.substring(1, 4)});
                        if (cursor.moveToNext()) {

                            location = cursor.getString(0).substring(0, cursor.getString(0).length() - 2);
                        }
                        cursor.close();
                    }
            }
        }
        return location;

    }
}
