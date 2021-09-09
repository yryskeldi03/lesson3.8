package com.geek.lesson37;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    private SharedPreferences preferences;
    private static Prefs instance;

    public Prefs(Context context) {
        instance = this;
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public void deleteAll(Context context) {
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }

    public static Prefs getInstance() {
        return instance;
    }

    public void setMarker(Double latitude, Double longitude) {
        preferences.edit().putFloat("latitude", latitude.floatValue()).apply();
        preferences.edit().putFloat("longitude", longitude.floatValue()).apply();
    }

    public void setMarker2(Double latitude, Double longitude) {
        preferences.edit().putFloat("latitude2", latitude.floatValue()).apply();
        preferences.edit().putFloat("longitude2", longitude.floatValue()).apply();
    }

    public Coord getMarker() {
        return new Coord(preferences.getFloat("latitude", 0f), preferences.getFloat("longitude", 0f));
    }

    public Coord getMarker2() {
        return new Coord(preferences.getFloat("latitude2", 0f), preferences.getFloat("longitude2", 0f));
    }
}
