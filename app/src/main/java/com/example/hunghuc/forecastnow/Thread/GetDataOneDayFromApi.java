package com.example.hunghuc.forecastnow.Thread;

import android.os.AsyncTask;
import android.support.v4.view.ViewPager;

import com.example.hunghuc.forecastnow.Entity.City;
import com.example.hunghuc.forecastnow.Entity.Weather;
import com.example.hunghuc.forecastnow.ForecastActivity;
import com.example.hunghuc.forecastnow.Adapter.SlideAdapter;

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
import java.util.ArrayList;

public class GetDataOneDayFromApi extends AsyncTask<ArrayList<Weather>, Void, ArrayList<Weather>> {
    private String dataOneDay = "", dataOneHour = "";
    private ArrayList<City> cityList = new ArrayList<>();
    private ArrayList<Weather> forecastList = new ArrayList<>();
    private ViewPager viewPager;
    private ForecastActivity activity;

    //Result Data
    String category = "", message = "", day_category = "", night_category = "";
    int min_temperature = 0, max_temperature = 0, current_temperature = 0, realfeel_temperature = 0;

    private String api_key = "";
    private final String API_LINK_ONE_DAY = "http://dataservice.accuweather.com/forecasts/v1/daily/1day/";
    private final String API_LINK_ONE_HOUR = "http://dataservice.accuweather.com/forecasts/v1/hourly/1hour/";
    private final String API_DETAIL = "true";

    public GetDataOneDayFromApi(ForecastActivity activity, ArrayList<City> cityList, String api_key, ViewPager viewPager) {
        this.cityList = cityList;
        this.activity = activity;
        this.api_key = api_key;
        this.viewPager = viewPager;
    }

    @Override
    protected ArrayList<Weather> doInBackground(ArrayList<Weather>... arrayLists) {
        try {
            if (cityList.isEmpty()) return null;
            for (City c : cityList) {
                dataOneDay = "";dataOneHour="";
                System.out.println("================");
                System.out.println("City " +c.getCity_name());
                System.out.println(c.getKeycode());
                //Get data for 1 day
                String tempURL = API_LINK_ONE_DAY + c.getKeycode() + "?apikey=" + api_key;
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
                JSONObject ja = temp.getJSONObject("Headline");
                category = ja.getString("Category");
                message = ja.getString("Text");
                JSONArray tempJsonarray = temp.getJSONArray("DailyForecasts");
                min_temperature = tempJsonarray.getJSONObject(0).getJSONObject("Temperature").getJSONObject("Minimum").getInt("Value");
                max_temperature = tempJsonarray.getJSONObject(0).getJSONObject("Temperature").getJSONObject("Maximum").getInt("Value");

                //Get data for 1 Hour
                tempURL = API_LINK_ONE_HOUR + c.getKeycode() + "?apikey=" + api_key + "&details=" + API_DETAIL;
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
                System.out.println(tempURL);
                tempJsonarray = new JSONArray(dataOneHour);
                current_temperature = tempJsonarray.getJSONObject(0).getJSONObject("Temperature").getInt("Value");
                realfeel_temperature = tempJsonarray.getJSONObject(0).getJSONObject("RealFeelTemperature").getInt("Value");
                System.out.println("Detail Information");
                System.out.println("Category " + category);
                System.out.println("message " + message);
                System.out.println("min_temperature " + min_temperature);
                System.out.println("max_temperature " + max_temperature);
                System.out.println("current_temperature " + current_temperature);
                System.out.println("realfeel_temperature " + realfeel_temperature);
                Weather weather = new Weather(c.getCity_name(), category, current_temperature, min_temperature, max_temperature, realfeel_temperature, message, "F");
                forecastList.add(weather);
            }
            System.out.println(forecastList.size());
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
            System.out.printf("============");
            System.out.println("Message: " + weathers.get(0).getMessage());
            SlideAdapter slideAdapter = new SlideAdapter(activity, weathers);
            viewPager.setAdapter(slideAdapter);
        }
    }
}
