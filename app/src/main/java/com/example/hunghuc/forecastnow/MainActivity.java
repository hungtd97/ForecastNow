package com.example.hunghuc.forecastnow;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.hunghuc.forecastnow.Function.GlobalVariable;
import com.example.hunghuc.forecastnow.SQLite.SQLiteHelper;
import com.example.hunghuc.forecastnow.Thread.GetDataCityCurrentLocation;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private TextView txtView;
    private SQLiteHelper mySql;
    private LocationManager locationManager;
    private String provider;
    public final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.makeLocationPermission();
        } else {
            this.getTempeType();

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                System.out.println("=================");
                System.out.println("Provider " + provider + " has been selected.");
                onLocationChanged(location);
            } else {
                System.out.println("=================");
                System.out.println("Location not available");
                System.out.println("Location not available");
            }

            Intent intent = new Intent(this, ForecastActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void makeLocationPermission() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }


        } catch (Exception ex) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    this.getTempeType();
                    System.out.println("=====================");
                    System.out.println("Permission granted  ");

                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    provider = locationManager.getBestProvider(criteria, false);
                    Location location = locationManager.getLastKnownLocation(provider);

                    if (location != null) {
                        System.out.println("=================");
                        System.out.println("Provider " + provider + " has been selected.");
                        onLocationChanged(location);
                    } else {
                        System.out.println("=================");
                        System.out.println("Location not available");
                        System.out.println("Location not available");
                    }

                    Intent intent = new Intent(this, ForecastActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void getTempeType() {
        String type = checkExist();
        if (type.equals("")) {
            if (mySql == null) {
                mySql = new SQLiteHelper(this, "ForecastNow", 1);
            }
            SQLiteDatabase db = mySql.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", "Tempe_type");
            values.put("value", "C");
            db.insert("Configs", null, values);
            ((GlobalVariable) this.getApplication()).setTempeType(true);
            db.close();
        } else if (type.equals("C")) {
            ((GlobalVariable) this.getApplication()).setTempeType(true);
        } else if (type.equals("F")) {
            ((GlobalVariable) this.getApplication()).setTempeType(false);
        }
    }

    private String checkExist() {
        if (mySql == null) {
            mySql = new SQLiteHelper(this, "ForecastNow", 1);
        }
        String value = "";
        SQLiteDatabase db = mySql.getReadableDatabase();
        String sql = "SELECT * FROM Configs";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            if (name.equals("Tempe_type")) {
                value = cursor.getString(cursor.getColumnIndex("value"));
                break;
            }
        }
        cursor.close();
        db.close();
        return value;
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
            String result = address.get(0).getAdminArea() + " " + address.get(0).getCountryName();
            String fnialAddress = builder.toString(); //This is the complete address.
            System.out.println("=================");
            System.out.println(result);
            GetDataCityCurrentLocation process = new GetDataCityCurrentLocation(this, getResources().getString(R.string.api_key), result);
            Object wait = process.execute().get();
        } catch (IOException e) {
            System.out.println("======================");
            System.out.println(e.toString());
            // Handle IOException
        } catch (NullPointerException e) {
            System.out.println("======================");
            System.out.println(e.toString());
            // Handle NullPointerException
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
}
