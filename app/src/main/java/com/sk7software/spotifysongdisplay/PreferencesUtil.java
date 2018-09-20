package com.sk7software.spotifysongdisplay;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Andrew on 08/10/2017.
 */

public class PreferencesUtil {
    public static final String APP_PREFERENCES_KEY = "SK7_SPOTIFY_TRACK_DISPLAY_PREFS";
    public static final String PREFERNECE_ACTIVE = "PREF_ACTIVE";
    public static final String PREFERNECE_SHOW_TRACK = "PREF_SHOW_TRACK";
    public static final String PREFERNECE_SCREEN_ON = "PREF_SCREEN_ON";
    public static final String PREFERNECE_TTS = "PREF_TTS";
    public static final String PREFERNECE_LED = "PREF_LED";
    public static final String PREFERENCE_TEXT_SIZE = "PREF_TEXT_SIZE";
    public static final String PREFERENCE_DISPLAY_POSITION = "PREF_DISPLAY_POSITION";
    public static final String PREFERENCE_LAST_TRACK = "PREF_LAST_TRACK";

    private static PreferencesUtil instance;
    private final SharedPreferences prefs;

    private PreferencesUtil(Context context) {
        prefs = context.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public synchronized static void init(Context context) {
        if (instance == null) {
            instance = new PreferencesUtil(context);
        }
    }

    public static PreferencesUtil getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Preferences not initialised");
        } else {
            return instance;
        }
    }

    public void addPreference(String name, String value) {
        prefs.edit().putString(name, value).commit();
    }

    public void addPreference(String name, int value) {
        prefs.edit().putInt(name, value).commit();
    }

    public void addPreference(String name, boolean value) {
        prefs.edit().putBoolean(name, value).commit();
    }

    public String getStringPreference(String name) {
        return prefs.getString(name, "");
    }

    public int getIntPreference(String name) {
        return prefs.getInt(name, 0);
    }

    public void clearAllPreferences() {
        prefs.edit().clear().commit();
    }

    public static void reset() {
        instance = null;
    }

    public boolean getBooleanPreference(String name) {
        return prefs.getBoolean(name, false);
    }

    public void clearStringPreference(String name) {
        prefs.edit().putString(name, "").commit();
    }
}

