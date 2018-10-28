package com.example.hunghuc.forecastnow.Entity;

public class Weather {

    private String city_name;
    private String category;
    private String category_day;
    private String category_night;
    private int temperature_current;
    private int temperature_min;
    private int temperature_max;
    private int temperature_realfeel;
    private String message;
    private String temperature_type;
    private int chance_rain;


    public Weather() {
    }

    public Weather(String city_name, String category, String category_day, String category_night, int temperature_current, int temperature_min, int temperature_max, int temperature_realfeel, String message, String temperature_type, int chance_rain) {
        this.city_name = city_name;
        this.category = category;
        this.category_day = category_day;
        this.category_night = category_night;
        this.temperature_current = temperature_current;
        this.temperature_min = temperature_min;
        this.temperature_max = temperature_max;
        this.temperature_realfeel = temperature_realfeel;
        this.message = message;
        this.temperature_type = temperature_type;
        this.chance_rain = chance_rain;
    }
    public Weather(String city_name, String category, int temperature_current, int temperature_min, int temperature_max, int temperature_realfeel, String message, int chance_rain) {
        this.city_name = city_name;
        this.category = category;
        this.temperature_current = temperature_current;
        this.temperature_min = temperature_min;
        this.temperature_max = temperature_max;
        this.temperature_realfeel = temperature_realfeel;
        this.message = message;
        this.chance_rain = chance_rain;
    }

    public Weather(String city_name, String category, int temperature_current) {
        this.city_name = city_name;
        this.category = category;
        this.temperature_current = temperature_current;
    }

    public Weather(String city_name, String category, int temperature_min, int temperature_max, String message) {
        this.city_name = city_name;
        this.category = category;
        this.temperature_min = temperature_min;
        this.temperature_max = temperature_max;
        this.message = message;
    }

    public int getChance_rain() {
        return chance_rain;
    }

    public void setChance_rain(int chance_rain) {
        this.chance_rain = chance_rain;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory_day() {
        return category_day;
    }

    public void setCategory_day(String category_day) {
        this.category_day = category_day;
    }

    public String getCategory_night() {
        return category_night;
    }

    public void setCategory_night(String category_night) {
        this.category_night = category_night;
    }

    public int getTemperature_current() {
        return temperature_current;
    }

    public void setTemperature_current(int temperature_current) {
        this.temperature_current = temperature_current;
    }

    public int getTemperature_min() {
        return temperature_min;
    }

    public void setTemperature_min(int temperature_min) {
        this.temperature_min = temperature_min;
    }

    public int getTemperature_max() {
        return temperature_max;
    }

    public void setTemperature_max(int temperature_max) {
        this.temperature_max = temperature_max;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTemperature_realfeel() {
        return temperature_realfeel;
    }

    public void setTemperature_realfeel(int temperature_realfeel) {
        this.temperature_realfeel = temperature_realfeel;
    }

    public String getTemperature_type() {
        return temperature_type;
    }

    public void setTemperature_type(String temperature_type) {
        this.temperature_type = temperature_type;
    }
}
