package com.example.hunghuc.forecastnow.Thread;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hunghuc.forecastnow.BroadcastReceiver.MyReceiver;
import com.example.hunghuc.forecastnow.Entity.City;
import com.example.hunghuc.forecastnow.Entity.Weather;
import com.example.hunghuc.forecastnow.ForecastActivity;
import com.example.hunghuc.forecastnow.Adapter.SlideAdapter;
import com.example.hunghuc.forecastnow.Function.Function;
import com.example.hunghuc.forecastnow.MainActivity;
import com.example.hunghuc.forecastnow.R;
import com.example.hunghuc.forecastnow.SQLite.SQLiteHelper;

import org.json.JSONArray;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GetDataOneDayFromApi extends AsyncTask<ArrayList<Weather>, Void, ArrayList<Weather>> {
    private String dataOneDay = "", dataOneHour = "";
    private ArrayList<City> cityList = new ArrayList<>();
    private ArrayList<Weather> forecastList = new ArrayList<>();
    private ViewPager viewPager;
    private ForecastActivity activity;
    private SQLiteHelper mySql;
    private Application application;
    private boolean checkValidWeather = false;
    private static final int NOTIFICATION_ID = 1;
    private String channelID = "Notificate";
    private City addedCity=null;

    public void setAddedCity(City addedCity) {
        System.out.println(">>>>>>> added city"+addedCity.toString());
        this.addedCity = addedCity;
    }

    //Result Data
    String category = "", message = "", day_category = "", night_category = "", type_day = "", date_time = "";
    int min_temperature = 0, max_temperature = 0, current_temperature = 0, realfeel_temperature = 0, chance_rain = 0;
    private String api_key = "";
    private final String DAY_END = "16:00:00";
    private final String DAY_START = "05:00:00";
    private final String SHORT_DAY = "1600";
    private final String API_LINK_ONE_DAY = "http://dataservice.accuweather.com/forecasts/v1/daily/1day/";
    private final String API_LINK_ONE_HOUR = "http://dataservice.accuweather.com/forecasts/v1/hourly/1hour/";
    private final String API_DETAIL = "true";
    private boolean notiCall = false;

    public GetDataOneDayFromApi(Application application, ForecastActivity activity, ArrayList<City> cityList, String api_key, ViewPager viewPager) {
        this.cityList = cityList;
        this.activity = activity;
        this.api_key = api_key;
        this.application = application;
        this.viewPager = viewPager;
    }

    private void saveWeather(Weather e, City c, String type, int id) {
        if (mySql == null) {
            mySql = new SQLiteHelper(activity, "ForecastNow", 1);
        }
        Date date = new Date();
        String strDateFormat = "yyyyMMddHHmm";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate = dateFormat.format(date);
        SQLiteDatabase db = mySql.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("city_code", c.getKeycode());
        values.put("category", e.getCategory());
        values.put("message", e.getMessage());
        values.put("min_tempe", e.getTemperature_min());
        values.put("max_tempe", e.getTemperature_max());
        values.put("current_tempe", e.getTemperature_current());
        values.put("real_tempe", e.getTemperature_realfeel());
        values.put("chance_rain", e.getChance_rain());
        values.put("time", formattedDate);
        values.put("location_time", e.getLocation_time());
        if (type.equals("add")) {
            long result = db.insert("Weather", null, values);
            if (result == 0) {
                System.out.println("===========");
                System.out.println("Insert weather to DB failed");
            } else {
                System.out.println("===========");
                System.out.println("Insert weather to DB successfully");
            }
        } else if (type.equals("update") && id != 0) {
            long result = db.update("Weather", values, "id=" + id, null);
            if (result == 0) {
                System.out.println("===========");
                System.out.println("Update weather to DB failed");
            } else {
                System.out.println("===========");
                System.out.println("Update weather to DB successfully");
            }
        }
        if (addedCity != null &&c.getCity_code().equals(addedCity.getCity_code())) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!Created Noti");
            getWeatherForNotification(addedCity,e);
        } else {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!Created Noti false");
        }
        db.close();
    }

    private int checkExist(String code) {
        if (mySql == null) {
            mySql = new SQLiteHelper(activity, "ForecastNow", 1);
        }
        SQLiteDatabase db = mySql.getReadableDatabase();
        String sql = "SELECT * FROM Weather";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String city_code = cursor.getString(cursor.getColumnIndex("city_code"));
            if (city_code.equals(code)) {
                int result = cursor.getInt(cursor.getColumnIndex("id"));
                cursor.close();
                db.close();
                return result;
            }
        }
        cursor.close();
        db.close();
        return 0;
    }

    private int nightCheck(String timer) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            Date d1 = sdf.parse(timer);
            Date d2 = sdf.parse(DAY_START);
            Date d3 = sdf.parse(DAY_END);
            System.out.println("Timer: " + timer);
            if ((d1.getTime() - d2.getTime()) > 0 && (d1.getTime() - d3.getTime()) < 0) {
                System.out.println("Day");
                return 1;
            } else {
                System.out.println("Night");
                return 2;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected ArrayList<Weather> doInBackground(ArrayList<Weather>... arrayLists) {
        Date date = new Date();
        String strDateFormat = "yyyyMMddHHmm";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate = dateFormat.format(date);
        String currentTime = new SimpleDateFormat("HHmm").format(date);
        try {
            if (cityList.isEmpty()) return null;
            for (City c : cityList) {
                dataOneDay = "";
                dataOneHour = "";

                //Get data for 1 Hour
                String tempURL = API_LINK_ONE_HOUR + c.getKeycode() + "?apikey=" + api_key + "&details=" + API_DETAIL;
                URL url = new URL(tempURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    if (line == null) break;
                    dataOneHour += line;
                }
                System.out.println(tempURL);
                JSONArray tempJsonarray = new JSONArray(dataOneHour);
                date_time = tempJsonarray.getJSONObject(0).getString("DateTime");
                current_temperature = tempJsonarray.getJSONObject(0).getJSONObject("Temperature").getInt("Value");
                realfeel_temperature = tempJsonarray.getJSONObject(0).getJSONObject("RealFeelTemperature").getInt("Value");
                date_time = date_time.substring(11, 19);
                int temp_date_time = this.nightCheck(date_time);


                //Get data for 1 Day
                tempURL = API_LINK_ONE_DAY + c.getKeycode() + "?apikey=" + api_key + "&details=" + API_DETAIL;
                url = new URL(tempURL);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    if (line == null) break;
                    dataOneDay += line;
                }
                System.out.println(tempURL);
                JSONObject temp = new JSONObject(dataOneDay);
                tempJsonarray = temp.getJSONArray("DailyForecasts");
                min_temperature = tempJsonarray.getJSONObject(0).getJSONObject("Temperature").getJSONObject("Minimum").getInt("Value");
                max_temperature = tempJsonarray.getJSONObject(0).getJSONObject("Temperature").getJSONObject("Maximum").getInt("Value");
                if (temp_date_time == 2) {
                    type_day = "Night";
                } else {
                    type_day = "Day";
                }
                category = tempJsonarray.getJSONObject(0).getJSONObject(type_day).getString("IconPhrase");
                message = tempJsonarray.getJSONObject(0).getJSONObject(type_day).getString("LongPhrase");
                double temp_chance_rain = tempJsonarray.getJSONObject(0).getJSONObject(type_day).getDouble("RainProbability");
                chance_rain = (int) temp_chance_rain;
                System.out.println("========");
                System.out.println("City Code: " + c.getKeycode());
                System.out.println("Type day" + type_day);

                Weather weather = new Weather(c.getCity_name(), category, current_temperature, min_temperature, max_temperature, realfeel_temperature, message, chance_rain, temp_date_time);
                int id = this.checkExist(c.getKeycode());
                if (id != 0) {
                    this.saveWeather(weather, c, "update", id);
                } else {
                    this.saveWeather(weather, c, "add", 0);
                }
                forecastList.add(weather);

            }
            return forecastList;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArrayList<Weather> weathers) {
        if (weathers != null && !weathers.isEmpty()) {
            SlideAdapter slideAdapter = new SlideAdapter(application, activity, weathers);
            viewPager.setAdapter(slideAdapter);

        }
    }

    public void createNotification(String title, String content, String smallText) {
        NotificationCompat.Builder mBuilder;
        Intent intent = new Intent(activity, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        createNotificationChannel();
        mBuilder = new NotificationCompat.Builder(activity, channelID);
        mBuilder.setSmallIcon(R.mipmap.icon);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(smallText);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));

        mBuilder.setDeleteIntent(MyReceiver.getDeleteIntent(activity));
        final NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = activity.getSystemService(NotificationManager.class);
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
