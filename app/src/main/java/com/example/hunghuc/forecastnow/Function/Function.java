package com.example.hunghuc.forecastnow.Function;

import com.example.hunghuc.forecastnow.R;

public class Function {

    public Function() {
    }

    public int convertIntTempe(int input) {
        return (input - 32) * 5 / 9;
    }

    public int iconClassify(String category, int dayType) {
        int result = 0;
        if (category.equalsIgnoreCase("Sunny")) {
            result = R.raw.sunny;
        } else if (category.equalsIgnoreCase("Mostly Sunny")) {
            result = R.raw.mostly_sunny;
        } else if (category.equalsIgnoreCase("Partly Sunny")) {
            result = R.raw.partly_sunny;
        } else if (category.equalsIgnoreCase("Intermittent clouds")) {
            result = R.raw.intermittent_clouds;
        } else if (category.equalsIgnoreCase("Hazy Sunshine")) {
            result = R.raw.hazy_sunshine;
        } else if (category.equalsIgnoreCase("Mostly Cloudy")) {
            result = R.raw.mostly_cloudy;
        } else if (category.equalsIgnoreCase("Cloudy")) {
            result = R.raw.cloudy;
        } else if (category.contains("Dreary")) {
            result = R.raw.dreary;
        } else if (category.contains("Fog")) {
            result = R.raw.fog;
        } else if (category.equalsIgnoreCase("Showers")) {
            result = R.raw.shower;
        } else if (category.contains("Mostly cloudy") && category.contains("showers")) {
            result = R.raw.mostly_cloudy_shower;
        } else if (category.contains("Partly Sunny") && category.contains("showers")){
            result = R.raw.partly_sunny_shower;
        } else if ( category.equalsIgnoreCase("T-Storms")){
            result = R.raw.t_storms;
        } else if (category.contains("Mostly cloudy") && category.contains("t-Storms")){
            result = R.raw.mostly_cloudy_t_storms;
        } else if (category.contains("Partly sunny") && category.contains("t-Storms")){
            result = R.raw.partly_sunny_t_storms;
        } else if(category.equalsIgnoreCase("Rain")){
            result = R.raw.rain;
        } else if(category.equalsIgnoreCase("Flurries")){
            result = R.raw.flurries;
        } else if(category.contains("Mostly cloudy") && category.contains("flurries")){
            result = R.raw.mostly_cloudy_flurries;
        }else if(category.contains("Partly sunny") && category.contains("flurries")){
            result = R.raw.partly_sunny_flurries;
        }else if(category.equalsIgnoreCase("Snow")){
            result = R.raw.snow;
        }else if(category.contains("Mostly cloudy") && category.contains("snow")){
            result = R.raw.mostly_cloudy_snow;
        }else if(category.equalsIgnoreCase("Ice")){
            result = R.raw.ice;
        }else if(category.equalsIgnoreCase("Sleet")){
            result = R.raw.sleet;
        }else if(category.equalsIgnoreCase("Freezing Rain")){
            result = R.raw.freezing_rain;
        }else if(category.equalsIgnoreCase("Rain and Snow")){
            result = R.raw.rain_and_snow;
        }else if(category.equalsIgnoreCase("Hot")){
            result = R.raw.hot;
        }else if(category.equalsIgnoreCase("Cold")){
            result = R.raw.cold;
        }else if(category.equalsIgnoreCase("Windy")){
            result = R.raw.windy;
        }else if(category.equalsIgnoreCase("Clear")){
            result = R.raw.night_clear;
        }else if(category.equalsIgnoreCase("Mostly Clear")){
            result = R.raw.mostly_clear;
        }else if(category.equalsIgnoreCase("Partly Cloudy") && dayType == 2){
            result = R.raw.partly_cloudy;
        }else if(category.equalsIgnoreCase("Intermittent Clouds") && dayType == 2){
            result = R.raw.night_intermitten_clouds;
        }else if(category.equalsIgnoreCase("Hazy Moonlight") && dayType == 2){
            result = R.raw.night_hazy_moonlight;
        }else if(category.equalsIgnoreCase("Mostly Cloudy") && dayType == 2){
            result = R.raw.night_mostly_cloudy;
        }else if(category.contains("Partly cloudy") && category.contains("showers") && dayType == 2){
            result = R.raw.night_partly_cloudy_showers;
        }else if(category.contains("Mostly cloudy") && category.contains("showers") && dayType == 2){
            result = R.raw.night_mostly_cloudy_showers;
        }else if(category.contains("Partly cloudy") && category.contains("t-storms") && dayType == 2){
            result = R.raw.night_partly_cloudy_t_storms;
        }else if(category.contains("Mostly cloudy") && category.contains("t-storms") && dayType == 2){
            result = R.raw.night_mostly_cloudy_t_storms;
        }else if(category.contains("Mostly cloudy") && category.contains("flurries") && dayType == 2){
            result = R.raw.night_mostly_cloudy_flurries;
        }else if(category.contains("Mostly cloudy") && category.contains("snow") && dayType == 2){
            result = R.raw.night_mostly_cloudy_snow;
        }
        return result;
    }
}
