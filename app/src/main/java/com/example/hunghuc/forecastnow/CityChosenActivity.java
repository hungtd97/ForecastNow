package com.example.hunghuc.forecastnow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hunghuc.forecastnow.Entity.City;
import com.example.hunghuc.forecastnow.Thread.GetDataCity;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CityChosenActivity extends AppCompatActivity {

    public static ArrayList<City> cityList;
    private ListView listView;
    private Button btnSearch;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_chosen);
        this.listView = findViewById(R.id.listCity);
        this.textView = findViewById(R.id.txtCitySearch);
        this.btnSearch = findViewById(R.id.btnCitySearch);
    }

    public void searchCity(View v){
        String key_search = textView.getText().toString();
        GetDataCity process = new GetDataCity(this, getResources().getString(R.string.api_key), listView, key_search);
        process.execute();
    }
}
