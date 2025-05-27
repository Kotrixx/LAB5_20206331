package com.example.lab5_20206331;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApp extends Application {

    public static final String CHANNEL_PASTILLA = "channel_pastilla";
    public static final String CHANNEL_JARABE = "channel_jarabe";
    public static final String CHANNEL_AMPOLLA = "channel_ampolla";
    public static final String CHANNEL_CAPSULA = "channel_capsula";
    public static final String CHANNEL_MOTIVACIONAL = "channel_motivacional";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            NotificationChannel pastilla = new NotificationChannel(CHANNEL_PASTILLA, "Pastilla", NotificationManager.IMPORTANCE_HIGH);
            pastilla.enableVibration(true);
            pastilla.setDescription("Notificaciones para medicamentos tipo Pastilla");

            NotificationChannel jarabe = new NotificationChannel(CHANNEL_JARABE, "Jarabe", NotificationManager.IMPORTANCE_HIGH);
            jarabe.enableVibration(true);
            jarabe.setDescription("Notificaciones para medicamentos tipo Jarabe");

            NotificationChannel ampolla = new NotificationChannel(CHANNEL_AMPOLLA, "Ampolla", NotificationManager.IMPORTANCE_HIGH);
            ampolla.enableVibration(true);
            ampolla.setDescription("Notificaciones para medicamentos tipo Ampolla");

            NotificationChannel capsula = new NotificationChannel(CHANNEL_CAPSULA, "Cápsula", NotificationManager.IMPORTANCE_HIGH);
            capsula.enableVibration(true);
            capsula.setDescription("Notificaciones para medicamentos tipo Cápsula");

            NotificationChannel motivacional = new NotificationChannel(CHANNEL_MOTIVACIONAL, "Motivacional", NotificationManager.IMPORTANCE_DEFAULT);
            motivacional.enableVibration(false);
            motivacional.setDescription("Notificaciones motivacionales personalizadas");

            manager.createNotificationChannel(pastilla);
            manager.createNotificationChannel(jarabe);
            manager.createNotificationChannel(ampolla);
            manager.createNotificationChannel(capsula);
            manager.createNotificationChannel(motivacional);
        }
    }
}
