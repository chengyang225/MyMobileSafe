package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ChanIan on 16/5/4.
 */
public class BlackNumberDbHelper extends SQLiteOpenHelper {
    public BlackNumberDbHelper(Context context) {
        super(context, "ian.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE t_black (_id INTEGER PRIMARY KEY AUTOINCREMENT,phone TEXT,mode TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
