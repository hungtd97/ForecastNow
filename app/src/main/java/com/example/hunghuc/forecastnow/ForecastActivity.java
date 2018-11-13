package com.example.hunghuc.forecastnow;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.hunghuc.forecastnow.Adapter.SlideAdapter;
import com.example.hunghuc.forecastnow.BroadcastReceiver.MyReceiver;
import com.example.hunghuc.forecastnow.Entity.City;
import com.example.hunghuc.forecastnow.Entity.Weather;
import com.example.hunghuc.forecastnow.Function.Function;
import com.example.hunghuc.forecastnow.SQLite.SQLiteHelper;
import com.example.hunghuc.forecastnow.Thread.GetDataOneDayFromApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class ForecastActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout sliderlayout;
    public static ArrayList<Weather> forecastList;
    private SQLiteHelper mySql;
    private boolean getApi = false;
    private TabLayout tabLayout;
    private final int TIME_LIMIT = 60;
    private City addedCity;
    private boolean checkValidWeather = false;
    private static final int NOTIFICATION_ID = 1;
    private String channelID = "Notificate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        this.viewPager = findViewById(R.id.viewpager);
        this.tabLayout = findViewById(R.id.tabDots);
        ArrayList<City> temp = this.getUserCity();
        addedCity = new City();
        this.firstLoad(temp);
        if (getApi) {
            GetDataOneDayFromApi process = new GetDataOneDayFromApi(this.getApplication(), this, temp, getResources().getString(R.string.api_key), viewPager);
            process.execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void openMenu(View v) {
        Intent intent = new Intent(this, ListCityActivity.class);
        startActivityForResult(intent, 100);
    }

    private ArrayList<City> getUserCity() {
        if (mySql == null) {
            mySql = new SQLiteHelper(getApplicationContext(), "ForecastNow", 1);
        }
        SQLiteDatabase db = mySql.getReadableDatabase();
        String sql = "SELECT * FROM City";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<City> cityList = new ArrayList<>();
        int count = 0, position = 0;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String city_code = cursor.getString(cursor.getColumnIndex("city_code"));
            String city_name = cursor.getString(cursor.getColumnIndex("city_name"));
            String keycode = cursor.getString(cursor.getColumnIndex("keycode"));
            String nation_code = cursor.getString(cursor.getColumnIndex("nation_code"));
            String nation_name = cursor.getString(cursor.getColumnIndex("nation_code"));
            int flag = cursor.getInt(cursor.getColumnIndex("current_location_flag"));

            if (flag == 1) {
                position = count;
            }
            cityList.add(new City(id, city_code, city_name, keycode, nation_code, nation_name, (flag == 1 ? true : false)));
            count++;
        }
        if (count > 0 && position != 0) {
            swap(cityList, 0, position);
        }
        cursor.close();
        db.close();
        return cityList;
    }

    public static final <T> void swap(T[] a, int i, int j) {
        T t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    public static final <T> void swap(ArrayList<T> l, int i, int j) {
        Collections.<T>swap(l, i, j);
    }

    public void reload(View v) {
        ArrayList<City> temp = this.getUserCity();
        this.firstLoad(temp);
        if (getApi) {
            System.out.println("=========");
            System.out.println("Reload API");
            GetDataOneDayFromApi process = new GetDataOneDayFromApi(this.getApplication(), this, temp, getResources().getString(R.string.api_key), viewPager);
            process.execute();
        }
    }

    private void firstLoad(ArrayList<City> cityList) {
        ArrayList<Weather> weathers = new ArrayList<>();
        if (mySql == null) {
            mySql = new SQLiteHelper(this, "ForecastNow", 1);
        }
        SQLiteDatabase db = mySql.getReadableDatabase();
        this.getApi = false;
        for (City x : cityList) {
            Weather template = new Weather();
            //Check data in DB
            String sql = "SELECT * FROM Weather";
            Cursor cursor = db.rawQuery(sql, null);
            int count = 0;
            boolean checkExist = false;
            while (cursor.moveToNext()) {
                count++;
                String city_code = cursor.getString(cursor.getColumnIndex("city_code"));
                if (city_code.equals(x.getKeycode())) {
                    Date date = new Date();
                    String strDateFormat = "yyyyMMddHHmm";
                    DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
                    double formattedDate = Double.parseDouble(dateFormat.format(date));
                    double weather_time = cursor.getDouble(cursor.getColumnIndex("time"));
                    System.out.println("================");
                    System.out.println("weather time: " + weather_time);
                    System.out.println("current time: " + formattedDate);
                    if ((formattedDate - weather_time) < TIME_LIMIT) {
                        String category = cursor.getString(cursor.getColumnIndex("category"));
                        String message = cursor.getString(cursor.getColumnIndex("message"));
                        int current_temperature = cursor.getInt(cursor.getColumnIndex("current_tempe"));
                        int min_temperature = cursor.getInt(cursor.getColumnIndex("min_tempe"));
                        int max_temperature = cursor.getInt(cursor.getColumnIndex("max_tempe"));
                        int realfeel_temperature = cursor.getInt(cursor.getColumnIndex("real_tempe"));
                        int chance_rain = cursor.getInt(cursor.getColumnIndex("chance_rain"));
                        int location_time = cursor.getInt(cursor.getColumnIndex("location_time"));
                        Weather weather = new Weather(x.getCity_name(), category, current_temperature, min_temperature, max_temperature, realfeel_temperature, message, chance_rain, location_time);
                        template = weather;
                        weathers.add(weather);

                        checkExist = true;
                    } else {
                        this.getApi = true;
                        weathers.add(new Weather(x.getCity_name(), "--", 0, 0, 0, 0, "--", 0, 0));
                    }
                }
            }
            if (!checkExist) {
                this.getApi = true;
                weathers.add(new Weather(x.getCity_name(), "--", 0, 0, 0, 0, "--", 0, 0));
            } else {
                if (addedCity != null && x.getCity_code().equals(addedCity.getCity_code())) {
                    getWeatherForNotification(addedCity, template);
                }
            }

            if (count == 0) {
                this.getApi = true;
                weathers.add(new Weather(x.getCity_name(), "--", 0, 0, 0, 0, "--", 0, 0));
            }
        }

        db.close();

        SlideAdapter slideAdapter = new SlideAdapter(this.getApplication(), this, weathers);
        viewPager.setAdapter(slideAdapter);
        tabLayout.setupWithViewPager(viewPager, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == resultCode) {
            addedCity = (City) data.getSerializableExtra("addedCity");
        }
        ArrayList<City> temp = this.getUserCity();
        this.firstLoad(temp);
        if (getApi) {
            GetDataOneDayFromApi process = new GetDataOneDayFromApi(this.getApplication(), this, temp, getResources().getString(R.string.api_key), viewPager);

            process.setAddedCity(addedCity);
            process.execute();
        }

    }


    public void createNotification(String title, String content, String smallText) {
        NotificationCompat.Builder mBuilder;
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        createNotificationChannel();
        mBuilder = new NotificationCompat.Builder(this, channelID);
        mBuilder.setSmallIcon(R.mipmap.icon);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(smallText);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));

        mBuilder.setDeleteIntent(MyReceiver.getDeleteIntent(this));
        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, mBuilder.build());
        addedCity = null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void getWeatherForNotification(City input, Weather template) {
        City city = input;
        String notiTitle = "";
        String notiMessage = "";
        String notiSmallMess = "Weather Forecast for chosen City";

        String messRain = ".";
        if (template.getChance_rain() <= 20) {
            if (template.getTemperature_min() > 15) {
                messRain = ". It's a beautiful day to hang out with friend !";
            } else {
                messRain = ". It's a little cold out there. Remember to bring a jacket !";
            }
        } else if (template.getChance_rain() > 20) {
            if (template.getTemperature_min() < 15) {
                messRain = " and there may be rain. It's really cold out side, best weather for staying home and sleep!";
            } else {
                messRain = " and there may be rain. You should bring a umbrella when going out!";
            }

        }
        Function f = new Function();
        notiMessage = "The temperature is from " + f.convertIntTempe(template.getTemperature_min()) + "°C to " + f.convertIntTempe(template.getTemperature_max()) + "°C in " + city.getCity_name() + ". " +
                "The weather is " + template.getMessage().toLowerCase() + " in the day" + messRain;


        String today = new SimpleDateFormat("dd/MM").format(new Date());
        notiTitle = "Weather Forecast " + today;


        createNotification(notiTitle, notiMessage, notiSmallMess);
    }


}
