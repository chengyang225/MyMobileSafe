package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ChanIan on 16/5/10.
 */
public class AppLockHelper extends SQLiteOpenHelper {
    public AppLockHelper(Context context ) {
        super(context, "applock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE t_applock(_id INTEGER PRIMARY KEY AUTOINCREMENT ,packname TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
