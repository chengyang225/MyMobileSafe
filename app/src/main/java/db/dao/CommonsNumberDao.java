package db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

/**
 * Created by ChanIan on 16/5/7.
 */
public class CommonsNumberDao {
    public static int getGroupCount(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT count(*) FROM classlist", null);
        cursor.moveToNext();
        return cursor.getInt(0);

    }
    public static int getChildrenCount(int groupPosition,SQLiteDatabase db) {
        String tableName="table"+(groupPosition+1);
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + tableName, null);
        cursor.moveToNext();
        return cursor.getInt(0);
    }
    public static String getGroupViewName(int groupPosition,SQLiteDatabase db){
        Cursor cursor = db.rawQuery("SELECT name FROM classlist WHERE idx=?", new String[]{String.valueOf(groupPosition+1)});
        cursor.moveToNext();
        return cursor.getString(0);
    }
    public static String getChildViewName(int groupPosition, int childPosition,SQLiteDatabase db){
        String tableName="table"+(groupPosition+1);
        Cursor cursor = db.rawQuery("SELECT name,number FROM " + tableName + " WHERE _id=?", new String[]{String.valueOf(childPosition+1)});
        String ret="";
        while (cursor.moveToNext()){
            String name = cursor.getString(0);
            ret+=name;
            String phone = cursor.getString(1);
            ret+="\n"+phone;
        }
        return ret;
    }
}
