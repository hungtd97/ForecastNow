package com.example.hunghuc.forecastnow.Service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.hunghuc.forecastnow.BroadcastReceiver.MyReceiver;
import com.example.hunghuc.forecastnow.Entity.City;
import com.example.hunghuc.forecastnow.Entity.Weather;
import com.example.hunghuc.forecastnow.Function.Function;
import com.example.hunghuc.forecastnow.MainActivity;
import com.example.hunghuc.forecastnow.SQLite.SQLiteHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackgroundService extends IntentService {

    MainActivity activity;
    NotificationCompat.Builder mBuilder;
    private String channelID = "Notificate";
    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    private SQLiteHelper mySql;
    private boolean checkValidCity = false;
    private boolean checkValidWeather = false;
    private String api_key = "gYmeORAzigzRmC6cnBfQ2W2HQrp1dXf2";
    private final String API_LINK_ONE_DAY = "http://dataservice.accuweather.com/forecasts/v1/daily/1day/";
    private final String API_LINK_ONE_HOUR = "http://dataservice.accuweather.com/forecasts/v1/hourly/1hour/";
    private final String API_DETAIL = "true";
    private String notiTitle="";
    private String notiMessage="";
    private String notiSmallMess="New Weather Forecast for you";


    public BackgroundService(MainActivity mainActivity) {
        super("Schedule");
        this.activity = mainActivity;
        Log.i("HERE", "here I am!");
    }

    public BackgroundService() {
        super("Schedule");
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, BackgroundService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, BackgroundService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    private City getUserCity() {
        System.out.println(">>Geting ");
        checkValidCity = false;
        if (mySql == null) {
            mySql = new SQLiteHelper(getApplicationContext(), "ForecastNow", 1);
        }
        SQLiteDatabase db = mySql.getReadableDatabase();
        String sql = "SELECT * FROM City where current_location_flag =1";
        Cursor cursor = db.rawQuery(sql, null);
        City city = null;
        if (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String city_code = cursor.getString(cursor.getColumnIndex("city_code"));
            String city_name = cursor.getString(cursor.getColumnIndex("city_name"));
            String keycode = cursor.getString(cursor.getColumnIndex("keycode"));
            String nation_code = cursor.getString(cursor.getColumnIndex("nation_code"));
            String nation_name = cursor.getString(cursor.getColumnIndex("nation_code"));
            city = new City(id, city_code, city_name, keycode, nation_code, nation_name,true);
            checkValidCity = true;
        }

        cursor.close();
        db.close();
        return city;
    }

    private void getWeatherForNotification() {
        System.out.println(">>Geting Weather");
        City city = getUserCity();
        notiTitle="";
        notiMessage="";
        checkValidWeather = false;
        if (checkValidCity == false) {
            return;
        }
        if (mySql == null) {
            mySql = new SQLiteHelper(this, "ForecastNow", 1);
        }
        SQLiteDatabase db = mySql.getReadableDatabase();
        Weather weather = new Weather();
        //Check data in DB
        String sql = "SELECT * FROM Weather";
        Cursor cursor = db.rawQuery(sql, null);

        Date date = new Date();
        String strDateFormat = "yyyyMMddHHmm";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        double formattedDate = Double.parseDouble(dateFormat.format(date));

        if (cursor.moveToNext()) {
            String city_code = cursor.getString(cursor.getColumnIndex("city_code"));
            if (city_code.equals(city.getKeycode())) {
                double weather_time = cursor.getDouble(cursor.getColumnIndex("time"));
                if ((formattedDate - weather_time) < 60) {
                    String category = cursor.getString(cursor.getColumnIndex("category"));
                    String message = cursor.getString(cursor.getColumnIndex("message"));
                    int current_temperature = cursor.getInt(cursor.getColumnIndex("current_tempe"));
                    int min_temperature = cursor.getInt(cursor.getColumnIndex("min_tempe"));
                    int max_temperature = cursor.getInt(cursor.getColumnIndex("max_tempe"));
                    int realfeel_temperature = cursor.getInt(cursor.getColumnIndex("real_tempe"));
                    int chance_rain = cursor.getInt(cursor.getColumnIndex("chance_rain"));
                    String messRain=".";
                    if(chance_rain==0){
                        messRain=". It's a beautiful day to hang out with friend!";
                    }else if(chance_rain>30){
                        messRain=" and there may be rain.";
                    }
                    weather = new Weather(city.getCity_name(), category, current_temperature, min_temperature, max_temperature, realfeel_temperature, message, chance_rain);
                    Function f = new Function();
                    notiMessage="The temperature is from "+f.convertIntTempe(min_temperature)+"°C to "+f.convertIntTempe(max_temperature)+"°C in "+city.getCity_name()+". " +
                            "The weather is "+message.toLowerCase()+" in the day"+messRain;
                    checkValidWeather=true;
                } else {
                    try {
                        weather = getWeatherByAPI(city);
                        checkValidWeather=true;
                    } catch (Exception e) {
                        weather = new Weather();
                    }
                }

            }
        }

        db.close();
        if(checkValidWeather){
            String today= new SimpleDateFormat("dd/MM").format(new Date());
            notiTitle="Weather Forecast "+today;

        }
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                Date d = new Date();
                System.out.println(d);
                if (d.getHours() == 23 && d.getMinutes() == 17) {
                    getWeatherForNotification();
                    if(checkValidWeather){
                        System.out.println("It's Time");
                        createNotification(notiTitle, notiMessage,notiSmallMess);
                    }else {
                        System.out.println(">>Error while get API");
                    }
                }

            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }

    }


    private Weather getWeatherByAPI(City city) throws Exception{
        String type_day="", category="",message="";
        String currentTime = new SimpleDateFormat("HHmm").format(new Date());
        String dataOneDay = "";
        String dataOneHour = "";
        int chance_rain=-1, current_temperature,realfeel_temperature;
        //Get data for 1 day
        String tempURL = API_LINK_ONE_DAY + city.getKeycode() + "?apikey=" + api_key + "&details=" + API_DETAIL;
        URL url = new URL(tempURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        while (line != null) {
            line = bufferedReader.readLine();
            if (line == null) break;
            dataOneDay += line;
        }
        System.out.println(tempURL);
        JSONObject temp = new JSONObject(dataOneDay);
        JSONArray tempJsonarray = temp.getJSONArray("DailyForecasts");
        int min_temperature = tempJsonarray.getJSONObject(0).getJSONObject("Temperature").getJSONObject("Minimum").getInt("Value");
        int max_temperature = tempJsonarray.getJSONObject(0).getJSONObject("Temperature").getJSONObject("Maximum").getInt("Value");
        if ((Integer.parseInt(currentTime) - Integer.parseInt("1600")) > 0) {
            type_day = "Night";
        } else {
            type_day = "Day";
        }
        category = tempJsonarray.getJSONObject(0).getJSONObject(type_day).getString("IconPhrase");
        message = tempJsonarray.getJSONObject(0).getJSONObject(type_day).getString("LongPhrase");
        double temp_chance_rain = tempJsonarray.getJSONObject(0).getJSONObject(type_day).getJSONObject("Rain").getDouble("Value");
        temp_chance_rain *= 100;
        chance_rain = (int) temp_chance_rain;
        String messRain=".";
        if(chance_rain==0){
            messRain=". It's a beautiful day to hang out with friend!";
        }else if(chance_rain>30){
            messRain=" and there may be rain.";
        }
        Function f = new Function();
        notiMessage="The temperature is from "+f.convertIntTempe(min_temperature)+"°C to "+f.convertIntTempe(max_temperature)+"°C in "+city.getCity_name()+". " +
                "The weather is "+message.toLowerCase()+" in the day"+messRain;

        //Get data for 1 Hour
        tempURL = API_LINK_ONE_HOUR + city.getKeycode() + "?apikey=" + api_key + "&details=" + API_DETAIL;
        url = new URL(tempURL);
        httpURLConnection = (HttpURLConnection) url.openConnection();
        inputStream = httpURLConnection.getInputStream();
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        line = "";
        while (line != null) {
            line = bufferedReader.readLine();
            if (line == null) break;
            dataOneHour += line;
        }
        System.out.println("========");
        System.out.println("City Code: " + city.getKeycode());
        System.out.println("Type day" + type_day);
        System.out.println("Chance of rain" + chance_rain);
        System.out.println("Category: " + category);
        System.out.println("message: " + message);
        System.out.println(tempURL);
        tempJsonarray = new JSONArray(dataOneHour);
        current_temperature = tempJsonarray.getJSONObject(0).getJSONObject("Temperature").getInt("Value");
        realfeel_temperature = tempJsonarray.getJSONObject(0).getJSONObject("RealFeelTemperature").getInt("Value");
        Weather weather = new Weather(city.getCity_name(), category, current_temperature, min_temperature, max_temperature, realfeel_temperature, message, chance_rain);
        return weather;
    }


    public void createNotification(String title, String content,String smallText) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        createNotificationChannel();
        mBuilder = new NotificationCompat.Builder(this, channelID);
        mBuilder.setSmallIcon(android.support.v4.R.drawable.notification_bg);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(smallText);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));

        mBuilder.setDeleteIntent(MyReceiver.getDeleteIntent(this));
        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
