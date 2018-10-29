package com.example.hunghuc.forecastnow;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.hunghuc.forecastnow.Function.GlobalVariable;
import com.example.hunghuc.forecastnow.SQLite.SQLiteHelper;
import com.example.hunghuc.forecastnow.Thread.GetDataOneDayFromApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView txtView;
    private SQLiteHelper mySql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getTempeType();

        Intent intent = new Intent(this, ForecastActivity.class);
        startActivity(intent);

    }

    private void getTempeType() {
        String type = checkExist();
        System.out.println("=================");
        System.out.println("Type " + type);
        if(type.equals("")){
            if (mySql == null) {
                mySql = new SQLiteHelper(this, "ForecastNow", 1);
            }
            SQLiteDatabase db = mySql.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", "Tempe_type");
            values.put("value", "C");
            db.insert("Configs", null, values);
            ((GlobalVariable) this.getApplication()).setTempeType(true);
            db.close();
        }else if(type.equals("C")){
            ((GlobalVariable) this.getApplication()).setTempeType(true);
        }else if(type.equals("F")){
            ((GlobalVariable) this.getApplication()).setTempeType(false);
        }
    }

    private String checkExist() {
        if (mySql == null) {
            mySql = new SQLiteHelper(this, "ForecastNow", 1);
        }
        String value = "";
        SQLiteDatabase db = mySql.getReadableDatabase();
        String sql = "SELECT * FROM Configs";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            if (name.equals("Tempe_type")) {
                value = cursor.getString(cursor.getColumnIndex("value"));
                System.out.println("===================>");
                System.out.println("Values" + value);
                break;
            }
        }
        cursor.close();
        db.close();
        return value;
    }
}
