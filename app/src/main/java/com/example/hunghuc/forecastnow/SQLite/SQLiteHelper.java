package com.example.hunghuc.forecastnow.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {


    private String dbName;

    public SQLiteHelper(Context context, String dbName, int version) {
        super(context, dbName, null, version);
        this.dbName = dbName;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE City (" +
                "id integer primary key autoincrement, " +
                "city_code text," +
                "city_name text," +
                "keycode text, " +
                "nation_code text, " +
                "nation_name text)";
        db.execSQL(sql);
//        sql = "CREATE TABLE Weather()";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion != oldVersion){
            String sql = "DROP TABLE City";
            db.execSQL(sql);
            onCreate(db);
        }
    }
}
