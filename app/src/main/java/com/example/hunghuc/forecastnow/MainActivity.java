package com.example.hunghuc.forecastnow;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.txtView = findViewById(R.id.textView);
        MyThread process = new MyThread();
        process.execute();
    }

    class MyThread extends AsyncTask<String, Void, String> {

        String data = "", dataParsed = "", singleParsed = "";
        String result = "";

        @Override
        protected String doInBackground(String... strings) {
//            String url_select = "https://l.facebook.com/l.php?u=http%3A%2F%2Fdataservice.accuweather.com%2Fforecasts%2Fv1%2Fdaily%2F1day%2F353412%3Fapikey%3DalQ4E4AaveDrxbOXI5jaWGOT5M1UScAD%26fbclid%3DIwAR1k9ZMjKAUogkrj_uKwdUENizFHa7vF1rQJlXwhAAeGTzrfbQMGvkI2CVk&h=AT016eF2py1anAQZr2Kjqm9H3NVgvM9rGJoGFg-hDLU9nxmGtsLVUNV4CtrbLh9DvoxhRNBPUhwXsZHJR8HgLSisDA4dhV6a8sVcoFW2qEcsi_b9ukUmSoaUMHY7b2dbmMyAVWI8-kU";
//            String result = "";
//            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
//            try {
//                // Set up HTTP post
//
//                // HttpClient is more then less deprecated. Need to change to URLConnection
//                HttpClient httpClient = new DefaultHttpClient();
//
//                HttpPost httpPost = new HttpPost(url_select);
//                httpPost.setEntity(new UrlEncodedFormEntity(param));
//                HttpResponse httpResponse = httpClient.execute(httpPost);
//                HttpEntity httpEntity = httpResponse.getEntity();
//
//                // Read content & Log
//                inputStream = httpEntity.getContent();
//            } catch (UnsupportedEncodingException e1) {
//                e1.printStackTrace();
//            } catch (ClientProtocolException e2) {
//                e2.printStackTrace();
//            } catch (IllegalStateException e3) {
//                e3.printStackTrace();
//            } catch (IOException e4) {
//                e4.printStackTrace();
//            }
//            // Convert response to string using String Builder
//            try {
//                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
//                StringBuilder sBuilder = new StringBuilder();
//
//                String line = null;
//                while ((line = bReader.readLine()) != null) {
//                    sBuilder.append(line + "\n");
//                }
//
//                inputStream.close();
//                result = sBuilder.toString();
//                return result;
//            } catch (Exception e) {
//            }
//
//            return "No data";
            try {
                URL url = new URL("http://dataservice.accuweather.com/forecasts/v1/daily/1day/353412?apikey=alQ4E4AaveDrxbOXI5jaWGOT5M1UScAD");

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    if (line == null) break;
                    data += line;
                }
                JSONObject temp = new JSONObject(data);
                JSONObject ja = temp.getJSONObject("Headline");
                result = ja.getString("Category");
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            txtView.setText("Result: " + this.result);
//            txtView.setText(this.result);
//            txtView.setText(s);
//            try {
//                JSONArray jArray = new JSONArray(s);
//                for(int i=0; i < jArray.length(); i++) {
//
//                    JSONObject jObject = jArray.getJSONObject(i);
//
////                    String name = jObject.getString("name");
////                    String tab1_text = jObject.getString("tab1_text");
////                    int active = jObject.getInt("active");
//
//                } // End Loop
//            } catch (JSONException e) {
//                Log.e("JSONException", "Error: " + e.toString());
//            }
        }
    }
}
