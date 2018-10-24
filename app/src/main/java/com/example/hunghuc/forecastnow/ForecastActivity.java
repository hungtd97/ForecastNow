package com.example.hunghuc.forecastnow;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.hunghuc.forecastnow.Entity.City;
import com.example.hunghuc.forecastnow.Entity.Weather;
import com.example.hunghuc.forecastnow.SQLite.SQLiteHelper;
import com.example.hunghuc.forecastnow.Thread.GetDataOneDayFromApi;

import java.util.ArrayList;

public class ForecastActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private SlideAdapter slideAdapter;
    public static ArrayList<Weather> forecastList;
    private SQLiteHelper mySql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        this.viewPager = findViewById(R.id.viewpager);
        ArrayList<City> temp = this.getUserCity();
        GetDataOneDayFromApi process = new GetDataOneDayFromApi(this, temp, getResources().getString(R.string.api_key), viewPager);
        process.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<City> temp = this.getUserCity();
        GetDataOneDayFromApi process = new GetDataOneDayFromApi(this, temp, getResources().getString(R.string.api_key), viewPager);
        process.execute();
    }

    public void openMenu(View v){
        Intent intent = new Intent(this, CityChosenActivity.class);
        startActivity(intent);
    }

    private ArrayList<City> getUserCity(){
        if (mySql == null) {
            mySql = new SQLiteHelper(getApplicationContext(), "ForecastNow", 1);
        }
        SQLiteDatabase db = mySql.getReadableDatabase();
        String sql = "SELECT * FROM City";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<City> cityList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String city_code = cursor.getString(cursor.getColumnIndex("city_code"));
            String city_name = cursor.getString(cursor.getColumnIndex("city_name"));
            String keycode = cursor.getString(cursor.getColumnIndex("keycode"));
            String nation_code = cursor.getString(cursor.getColumnIndex("nation_code"));
            String nation_name = cursor.getString(cursor.getColumnIndex("nation_code"));
            cityList.add(new City(city_code, city_name, keycode, nation_code, nation_name));
        }
        cursor.close();
        db.close();
        return cityList;
    }
}
