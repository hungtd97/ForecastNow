package com.example.hunghuc.forecastnow.Function;

import android.app.Application;

public class GlobalVariable extends Application {

    private boolean tempeType;

    public boolean isTempeType() {
        return tempeType;
    }

    public void setTempeType(boolean tempeType) {
        this.tempeType = tempeType;
    }
}
