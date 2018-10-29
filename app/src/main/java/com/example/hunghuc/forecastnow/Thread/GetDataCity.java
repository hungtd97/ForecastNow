package com.example.hunghuc.forecastnow.Thread;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hunghuc.forecastnow.Adapter.ListViewAdapter;
import com.example.hunghuc.forecastnow.CityChosenActivity;
import com.example.hunghuc.forecastnow.Entity.City;
import com.example.hunghuc.forecastnow.Entity.Weather;
import com.example.hunghuc.forecastnow.MainActivity;
import com.example.hunghuc.forecastnow.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GetDataCity extends AsyncTask<ArrayList<City>, Void, ArrayList<City>> {

    private String dataCity = "", key_search = "";
    private String result = "";
    private ArrayList<City> cityList = new ArrayList<>();
    private ArrayList<Weather> forecastList = new ArrayList<>();
    private ListView listView;
    private String api_key="";
    private CityChosenActivity activity;
    private boolean currentCity = false;

    public GetDataCity(CityChosenActivity activity, String api_key, ListView listView, String key_search) {
        this.key_search = key_search;
        this.listView = listView;
        this.api_key = api_key;
        this.activity = activity;
    }

    private final String API_LINK_CITY = "http://dataservice.accuweather.com/locations/v1/cities/search";
    private final String API_DETAIL = "true";


    //Result Data
    String city_name = "", city_code = "", city_key = "", nation_code = "", nation_name = "";

    @Override
    protected ArrayList<City> doInBackground(ArrayList<City>... arrayLists) {
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
            JSONArray temp = new JSONArray(dataCity);
            for (int i = 0; i < temp.length(); i++) {
                JSONObject json = temp.getJSONObject(i);
                if (!json.isNull("Code")&&json.getString("Code").equals("ServiceUnavailable")) {
                    Toast.makeText(activity, "API key expired", Toast.LENGTH_SHORT).show();
                    return null;
                }
                if(json.isNull("ParentCity")){
                    city_key = json.getString("Key");
                    city_name = json.getString("LocalizedName");
                }else{
                    city_key = json.getJSONObject("ParentCity").getString("Key");
                    city_name = json.getJSONObject("ParentCity").getString("LocalizedName");
                }
                city_code = json.getJSONObject("AdministrativeArea").getString("ID");
                nation_code = json.getJSONObject("Country").getString("ID");
                nation_name = json.getJSONObject("Country").getString("LocalizedName");
                cityList.add(new City(city_code, city_name, city_key, nation_code, nation_name, this.currentCity));
            }
            return cityList;

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
    protected void onPostExecute(ArrayList<City> cities) {
        ListViewAdapter adapter = new ListViewAdapter(activity, cityList);
        listView.setAdapter(adapter);
    }
}
