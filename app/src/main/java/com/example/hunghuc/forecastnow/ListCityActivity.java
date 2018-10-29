package com.example.hunghuc.forecastnow;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.example.hunghuc.forecastnow.Adapter.UserCityListAdapter;
import com.example.hunghuc.forecastnow.Entity.City;
import com.example.hunghuc.forecastnow.Function.GlobalVariable;
import com.example.hunghuc.forecastnow.SQLite.SQLiteHelper;

import java.util.ArrayList;

public class ListCityActivity extends AppCompatActivity {

    private ListView listCity;
    private ToggleButton toggleButton;
    private SQLiteHelper mySql;
    private boolean tempeType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_city);
        this.listCity = findViewById(R.id.listUserCity);
        this.toggleButton = findViewById(R.id.toggleBtn);
        this.tempeType = ((GlobalVariable) this.getApplication()).isTempeType();
        this.toggleButton.setChecked(tempeType);
        this.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int result = 0;
                if(isChecked){
                    result = updateTempeType("C");
                    ((GlobalVariable)getApplication()).setTempeType(true);
                }else{
                    result = updateTempeType("F");
                    ((GlobalVariable)getApplication()).setTempeType(false);
                }
                System.out.println(result);
            }
        });
        UserCityListAdapter adapter = new UserCityListAdapter(this, getUserCity());
        listCity.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserCityListAdapter adapter = new UserCityListAdapter(this, getUserCity());
        listCity.setAdapter(adapter);
    }

    private int updateTempeType(String value){
        if (mySql == null) {
            mySql = new SQLiteHelper(getApplicationContext(), "ForecastNow", 1);
        }
        SQLiteDatabase db = mySql.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", "Tempe_type");
        values.put("value", value);
        int result = db.update("Configs", values, "name='Tempe_type'", null);
        db.close();
        return result;
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
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String city_code = cursor.getString(cursor.getColumnIndex("city_code"));
            String city_name = cursor.getString(cursor.getColumnIndex("city_name"));
            String keycode = cursor.getString(cursor.getColumnIndex("keycode"));
            String nation_code = cursor.getString(cursor.getColumnIndex("nation_code"));
            String nation_name = cursor.getString(cursor.getColumnIndex("nation_code"));
            cityList.add(new City(id, city_code, city_name, keycode, nation_code, nation_name));
        }
        cursor.close();
        db.close();
        return cityList;
    }

    public void AddNewCity(View v){
        Intent intent = new Intent(this, CityChosenActivity.class);
        startActivity(intent);
    }
}
