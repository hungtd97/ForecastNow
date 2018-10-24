package com.example.hunghuc.forecastnow;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;

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

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = "alQ4E4AaveDrxbOXI5jaWGOT5M1UScAD";
    public static final String API_LINK_1DAY = "http://dataservice.accuweather.com/forecasts/v1/daily/1day/";
    private TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        MyThread process = new MyThread();
//        GetDataOneDayFromApi process = new GetDataOneDayFromApi();
//        process.execute();
        Intent intent = new Intent(this, ForecastActivity.class);
        startActivity(intent);

    }
}
