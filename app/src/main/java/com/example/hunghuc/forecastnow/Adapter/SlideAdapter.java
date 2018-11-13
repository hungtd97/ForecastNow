package com.example.hunghuc.forecastnow.Adapter;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hunghuc.forecastnow.Entity.Weather;
import com.example.hunghuc.forecastnow.Function.Function;
import com.example.hunghuc.forecastnow.Function.GlobalVariable;
import com.example.hunghuc.forecastnow.MainActivity;
import com.example.hunghuc.forecastnow.R;

import java.util.ArrayList;

public class SlideAdapter extends PagerAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<Weather> cityList;
    Function function = new Function();
    private boolean typeTempe = false;
    private String tempe = "", minTempe = "", maxTempe = "", realTempe = "";

    public SlideAdapter(Application application, Context context, ArrayList<Weather> cityList) {
        this.context = context;
        this.cityList = cityList;
        this.typeTempe = ((GlobalVariable) application).isTempeType();
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
        ImageView imageView = view.findViewById(R.id.categoryImage);
        Weather currentCity = new Weather();
        currentCity = cityList.get(position);
        txtCity.setText(currentCity.getCity_name());
        txtCategory.setText(currentCity.getCategory());
        if (typeTempe) {
            tempe = function.convertIntTempe(currentCity.getTemperature_current()) + "°";
            minTempe = function.convertIntTempe(currentCity.getTemperature_min()) + "°";
            maxTempe = function.convertIntTempe(currentCity.getTemperature_max()) + "°";
            realTempe = function.convertIntTempe(currentCity.getTemperature_realfeel()) + "°";
        }else{
            tempe = currentCity.getTemperature_current() + "°";
            minTempe = currentCity.getTemperature_min() + "°";
            maxTempe = currentCity.getTemperature_max() + "°";
            realTempe = currentCity.getTemperature_realfeel() + "°";
        }
        String chance_rain = String.valueOf(currentCity.getChance_rain()) + "%";
        int image_path = function.iconClassify(cityList.get(position).getCategory(), cityList.get(position).getLocation_time());
        String color = function.colorClassify(cityList.get(position).getCategory(), cityList.get(position).getLocation_time());
        System.out.println("=============");
        System.out.println(color);
        if(color.equals("")) color = "#000000";
        layoutSlide.setBackgroundColor(Color.parseColor(color));
        imageView.setImageResource(image_path);
        txtTemparature.setText(tempe);
        txtMinTempe.setText(minTempe);
        txtMaxTempe.setText(maxTempe);
        txtRealTempe.setText(realTempe);
        txtChanceRain.setText(chance_rain);
        String mess="";
        if (currentCity.getChance_rain() <= 20) {
            if(currentCity.getTemperature_min() >15){
                mess = ". It's a beautiful day to hang out with friend !";
            } else {
                mess = ". It's a little cold out there. Remember to bring a jacket !";
            }
        } else if (currentCity.getChance_rain() > 20) {
            if(currentCity.getTemperature_min() <15){
                mess = " and there may be rain. It's really cold out side, best weather for staying home and sleep!";
            }else{
                mess = " and there may be rain. You should bring a umbrella when going out!";
            }

        }
        Function f = new Function();
        String notiMessage = "The temperature is from " + f.convertIntTempe(currentCity.getTemperature_min()) + "°C to " + f.convertIntTempe(currentCity.getTemperature_max()) + "°C in " + currentCity.getCity_name() + ". " +
                "The weather is " + currentCity.getMessage() +mess;
        txtText.setText(notiMessage);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }


}
