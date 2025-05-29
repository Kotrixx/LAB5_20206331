package com.example.lab5_20206331;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {

    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_MOTIVATIONAL_MESSAGE = "motivational_message";
    private static final String KEY_PROFILE_IMAGE_PATH = "profile_image";
    private static final String KEY_REMINDER_HOUR = "reminder_hour";
    private static final String KEY_REMINDER_MINUTE = "reminder_minute";

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
        return prefs.getString(KEY_MOTIVATIONAL_MESSAGE, "¡Bienvenido!");
    }

    public void saveProfileImagePath(String path) {
        prefs.edit().putString(KEY_PROFILE_IMAGE_PATH, path).apply();
    }

    public String getProfileImagePath() {
        return prefs.getString(KEY_PROFILE_IMAGE_PATH, null);
    }

    // NUEVAS FUNCIONES PARA HORA DE RECORDATORIO
    public void saveReminderTime(int hour, int minute) {
        prefs.edit()
                .putInt(KEY_REMINDER_HOUR, hour)
                .putInt(KEY_REMINDER_MINUTE, minute)
                .apply();
    }

    public int getReminderHour() {
        return prefs.getInt(KEY_REMINDER_HOUR, 8); // default 8 AM
    }

    public int getReminderMinute() {
        return prefs.getInt(KEY_REMINDER_MINUTE, 0); // default 0 min
    }

    // Guardar frecuencia
    public void saveReminderFrequency(int value, String unit) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("reminder_freq_value", value);
        editor.putString("reminder_freq_unit", unit);
        editor.apply();
    }

    // Obtener frecuencia
    public int getReminderFrequencyValue() {
        return prefs.getInt("reminder_freq_value", 24); // valor por defecto 24
    }

    public String getReminderFrequencyUnit() {
        return prefs.getString("reminder_freq_unit", "horas");
    }

}
