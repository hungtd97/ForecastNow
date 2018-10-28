package com.example.hunghuc.forecastnow.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hunghuc.forecastnow.Entity.Weather;
import com.example.hunghuc.forecastnow.Function.Function;
import com.example.hunghuc.forecastnow.R;

import java.util.ArrayList;

public class SlideAdapter extends PagerAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<Weather> cityList;
    Function function = new Function();

    public SlideAdapter(Context context, ArrayList<Weather> cityList) {
        this.context = context;
        this.cityList = cityList;
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == (LinearLayout) o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.mainslider, container, false);
        LinearLayout layoutSlide = (LinearLayout) view.findViewById(R.id.sliderlayout);
        TextView txtCity = view.findViewById(R.id.txtCity);
        TextView txtCategory = view.findViewById(R.id.txtCategory);
        TextView txtTemparature = view.findViewById(R.id.txtTemparature);
        TextView txtMinTempe = view.findViewById(R.id.txtMinTempe);
        TextView txtMaxTempe = view.findViewById(R.id.txtMaxTempe);
        TextView txtRealTempe = view.findViewById(R.id.txtRealTempe);
        TextView txtChanceRain = view.findViewById(R.id.txtChanceRain);
        TextView txtText = view.findViewById(R.id.txtText);
        Weather currentCity = new Weather();
        currentCity = cityList.get(position);
        txtCity.setText(currentCity.getCity_name());
        txtCategory.setText(currentCity.getCategory());
        String tempe = function.convertIntTempe(currentCity.getTemperature_current()) + "°";
        String minTempe = function.convertIntTempe(currentCity.getTemperature_min()) + "°";
        String maxTempe = function.convertIntTempe(currentCity.getTemperature_max()) +  "°" ;
        String realTempe = function.convertIntTempe(currentCity.getTemperature_realfeel()) + "°" ;
        String chance_rain = String.valueOf(currentCity.getChance_rain()) + "%";
        txtTemparature.setText(tempe);
        txtMinTempe.setText(minTempe);
        txtMaxTempe.setText(maxTempe);
        txtRealTempe.setText(realTempe);
        txtChanceRain.setText(chance_rain);
        txtText.setText(currentCity.getMessage());
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
