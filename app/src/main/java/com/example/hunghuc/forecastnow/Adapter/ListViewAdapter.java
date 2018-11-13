package com.example.hunghuc.forecastnow.Adapter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hunghuc.forecastnow.BroadcastReceiver.MyReceiver;
import com.example.hunghuc.forecastnow.CityChosenActivity;
import com.example.hunghuc.forecastnow.Entity.City;
import com.example.hunghuc.forecastnow.Entity.Weather;
import com.example.hunghuc.forecastnow.Function.Function;
import com.example.hunghuc.forecastnow.MainActivity;
import com.example.hunghuc.forecastnow.R;
import com.example.hunghuc.forecastnow.SQLite.SQLiteHelper;
import com.example.hunghuc.forecastnow.Service.BackgroundService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListViewAdapter extends BaseAdapter {

    private CityChosenActivity cityChosenActivity;
    private ArrayList<City> listCity;
    private SQLiteOpenHelper mySql;

    public ListViewAdapter(CityChosenActivity cityChosenActivity, ArrayList<City> listCity) {
        this.cityChosenActivity = cityChosenActivity;
        this.listCity = listCity;
    }

    @Override
    public int getCount() {
        return listCity.size();
    }

    @Override
    public Object getItem(int position) {
        return listCity.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyHolder myholder = null;
        if (convertView == null) {
            convertView = cityChosenActivity.getLayoutInflater().inflate(R.layout.listcityform, null);
            myholder = new MyHolder();
            myholder.txtCityName = convertView.findViewById(R.id.txtCityName);
            myholder.txtNationName = convertView.findViewById(R.id.txtNationName);
            convertView.setTag(myholder);
        } else {
            myholder = (MyHolder) convertView.getTag();
        }
        myholder.txtNationName.setText(listCity.get(position).getNation_name());
        myholder.txtNationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mySql == null) {
                    mySql = new SQLiteHelper(cityChosenActivity, "ForecastNow", 1);
                }
                boolean checkExist = false;
                SQLiteDatabase db = mySql.getReadableDatabase();
                String sql = "SELECT * FROM City";
                Cursor cursor = db.rawQuery(sql, null);
                while (cursor.moveToNext()) {
                    String city_code = cursor.getString(cursor.getColumnIndex("city_code"));
                    String nation_code = cursor.getString(cursor.getColumnIndex("nation_code"));
                    if (city_code.equals(listCity.get(position).getCity_code()) && nation_code.equals(listCity.get(position).getNation_code())) {
                        checkExist = true;
                        break;
                    }
                }
                if (!checkExist) {
                    ContentValues values = new ContentValues();
                    values.put("city_code", listCity.get(position).getCity_code());
                    values.put("city_name", listCity.get(position).getCity_name());
                    values.put("keycode", listCity.get(position).getKeycode());
                    values.put("nation_code", listCity.get(position).getNation_code());
                    values.put("nation_name", listCity.get(position).getNation_name());
                    values.put("current_location_flag", "0");
                    long result = db.insert("City", null, values);
                    if (result != 0) {
                        Toast.makeText(cityChosenActivity, "Add new city successfully", Toast.LENGTH_LONG).show();
                        System.out.println(">>>>>>>>Return to Home");
                        Intent intent = new Intent();
                        intent.putExtra("addedCity",listCity.get(position));
                        cityChosenActivity.setResult(200, intent);
                        cityChosenActivity.finish();
                    }
                }else{
                    Toast.makeText(cityChosenActivity, "City Existed", Toast.LENGTH_LONG).show();
                }

            }
        });
        myholder.txtCityName.setText(listCity.get(position).getCity_name());
        myholder.txtCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mySql == null) {
                    mySql = new SQLiteHelper(cityChosenActivity, "ForecastNow", 1);
                }
                boolean checkExist = false;
                SQLiteDatabase db = mySql.getReadableDatabase();
                String sql = "SELECT * FROM City";
                Cursor cursor = db.rawQuery(sql, null);
                while (cursor.moveToNext()) {
                    String city_code = cursor.getString(cursor.getColumnIndex("city_code"));
                    String nation_code = cursor.getString(cursor.getColumnIndex("nation_code"));
                    if (city_code.equals(listCity.get(position).getCity_code()) && nation_code.equals(listCity.get(position).getNation_code())) {
                        checkExist = true;
                        break;
                    }
                }
                if (!checkExist) {
                    ContentValues values = new ContentValues();
                    values.put("city_code", listCity.get(position).getCity_code());
                    values.put("city_name", listCity.get(position).getCity_name());
                    values.put("keycode", listCity.get(position).getKeycode());
                    values.put("nation_code", listCity.get(position).getNation_code());
                    values.put("nation_name", listCity.get(position).getNation_name());
                    values.put("current_location_flag", "0");
                    long result = db.insert("City", null, values);
                    if (result != 0) {
                        Toast.makeText(cityChosenActivity, "Add new city successfully", Toast.LENGTH_LONG).show();
                        System.out.println(">>>>>>>>Return to Home");
                        Intent intent = new Intent();
                        intent.putExtra("addedCity",listCity.get(position));
                        cityChosenActivity.setResult(200, intent);
                        cityChosenActivity.finish();
                    }
                }else{
                    Toast.makeText(cityChosenActivity, "City Existed", Toast.LENGTH_LONG).show();
                }
            }
        });
        return convertView;
    }

    class MyHolder {
        public TextView txtCityName;
        public TextView txtNationName;
    }

}
