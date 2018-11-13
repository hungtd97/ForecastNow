package com.example.hunghuc.forecastnow.Entity;

import java.io.Serializable;

public class City implements Serializable {
    private int id;
    private String city_code;
    private String city_name;
    private String keycode;
    private String nation_code;
    private String nation_name;
    private boolean current_location_flag;

    public City() {
    }

    public City(int id, String city_code, String city_name, String keycode, String nation_code, String nation_name, boolean current_location_flag) {
        this.id = id;
        this.city_code = city_code;
        this.city_name = city_name;
        this.keycode = keycode;
        this.nation_code = nation_code;
        this.nation_name = nation_name;
        this.current_location_flag = current_location_flag;
    }
    public City(String city_code, String city_name, String keycode, String nation_code, String nation_name, boolean current_location_flag) {
        this.city_code = city_code;
        this.city_name = city_name;
        this.keycode = keycode;
        this.nation_code = nation_code;
        this.nation_name = nation_name;
        this.current_location_flag = current_location_flag;
    }

    public int getId() {
        return id;
    }

    public boolean getCurrent_location_flag() {
        return current_location_flag;
    }

    public void setCurrent_location_flag(boolean current_location_flag) {
        this.current_location_flag = current_location_flag;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getKeycode() {
        return keycode;
    }

    public void setKeycode(String keycode) {
        this.keycode = keycode;
    }

    public String getNation_code() {
        return nation_code;
    }

    public void setNation_code(String nation_code) {
        this.nation_code = nation_code;
    }

    public String getNation_name() {
        return nation_name;
    }

    public void setNation_name(String nation_name) {
        this.nation_name = nation_name;
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", city_code='" + city_code + '\'' +
                ", city_name='" + city_name + '\'' +
                ", keycode='" + keycode + '\'' +
                ", nation_code='" + nation_code + '\'' +
                ", nation_name='" + nation_name + '\'' +
                ", current_location_flag=" + current_location_flag +
                '}';
    }
}
