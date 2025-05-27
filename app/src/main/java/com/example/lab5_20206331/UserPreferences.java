package com.example.lab5_20206331;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {

    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_MOTIVATIONAL_MESSAGE = "motivational_message";
    private static final String KEY_PROFILE_IMAGE_PATH = "profile_image";

    private SharedPreferences prefs;

    public UserPreferences(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserName(String name) {
        prefs.edit().putString(KEY_USER_NAME, name).apply();
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "Ricardo");
    }

    public void saveMotivationalMessage(String message) {
        prefs.edit().putString(KEY_MOTIVATIONAL_MESSAGE, message).apply();
    }

    public String getMotivationalMessage() {
        return prefs.getString(KEY_MOTIVATIONAL_MESSAGE, "¡Bienvenido Estimados, como se encuenran!");
    }

    public void saveProfileImagePath(String path) {
        prefs.edit().putString(KEY_PROFILE_IMAGE_PATH, path).apply();
    }

    public String getProfileImagePath() {
        return prefs.getString(KEY_PROFILE_IMAGE_PATH, null);
    }
}
