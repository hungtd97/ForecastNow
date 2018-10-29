package com.example.hunghuc.forecastnow.Thread;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.hunghuc.forecastnow.Entity.City;
import com.example.hunghuc.forecastnow.MainActivity;
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
import java.net.URLEncoder;

public class GetDataCityCurrentLocation extends AsyncTask {

    private SQLiteHelper mySql;
    private MainActivity mainActivity;
    private String api_key;
    private String key_search;

    private final String API_LINK_CITY = "http://dataservice.accuweather.com/locations/v1/cities/search";
    private final String API_DETAIL = "true";

    //Result Data
    String dataCity = "", city_name = "", city_code = "", city_key = "", nation_code = "", nation_name = "";

    public GetDataCityCurrentLocation(MainActivity mainActivity, String api_key, String key_search) {
        this.mainActivity = mainActivity;
        this.api_key = api_key;
        this.key_search = key_search;
    }

    private int checkExist() {
        if (mySql == null) {
            mySql = new SQLiteHelper(mainActivity, "ForecastNow", 1);
        }
        SQLiteDatabase db = mySql.getReadableDatabase();
        String sql = "SELECT * FROM City";
        Cursor cursor = db.rawQuery(sql, null);
        int id = 0;
        while (cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex("current_location_flag")) == 1) {
                id = cursor.getInt(cursor.getColumnIndex("id"));
                break;
            }
        }
        cursor.close();
        db.close();
        return id;
    }

    private long addCity(City c, int id) {
        if (mySql == null) {
            mySql = new SQLiteHelper(mainActivity, "ForecastNow", 1);
        }
        SQLiteDatabase db = mySql.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("city_code", c.getCity_code());
        values.put("city_name", c.getCity_name());
        values.put("keycode", c.getKeycode());
        values.put("nation_code", c.getNation_code());
        values.put("nation_name", c.getNation_name());
        values.put("current_location_flag", "1");
        long result = 0;
        System.out.println("ID: " +id);
        if (id == 0) {
            result = db.insert("City", null, values);
        } else {
            result = db.update("City", values, "id=" + id, null);
        }
        return result;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            //Get Data of City from key search
            key_search = URLEncoder.encode(key_search, "UTF-8");
            String tempURL = API_LINK_CITY + "?apikey=" + api_key + "&q=" + key_search;
            URL url = new URL(tempURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                if (line == null) break;
                dataCity += line;
            }
            System.out.println(tempURL);
            JSONArray temp = new JSONArray(dataCity);
            for (int i = 0; i < temp.length(); i++) {
                JSONObject json = temp.getJSONObject(i);
                if (!json.isNull("Code") && json.getString("Code").equals("ServiceUnavailable")) {
                    Toast.makeText(mainActivity, "API key expired", Toast.LENGTH_SHORT).show();
                    return null;
                }
                if (json.isNull("ParentCity")) {
                    city_key = json.getString("Key");
                    city_name = json.getString("LocalizedName");
                } else {
                    city_key = json.getJSONObject("ParentCity").getString("Key");
                    city_name = json.getJSONObject("ParentCity").getString("LocalizedName");
                }
                city_code = json.getJSONObject("AdministrativeArea").getString("ID");
                nation_code = json.getJSONObject("Country").getString("ID");
                nation_name = json.getJSONObject("Country").getString("LocalizedName");
                City tempCity = new City(city_code, city_name, city_key, nation_code, nation_name, true);
                System.out.println(city_code);
                System.out.println(city_key);
                long result = this.addCity(tempCity, this.checkExist());
                System.out.println("==================");
                System.out.println("Result:"+result);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
