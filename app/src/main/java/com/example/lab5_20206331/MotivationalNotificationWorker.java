package com.example.lab5_20206331;

import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MotivationalNotificationWorker extends Worker {

    public static final String CHANNEL_ID = MyApp.CHANNEL_MOTIVACIONAL;

    public MotivationalNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String message = getInputData().getString("motivational_message");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_motivacional)  // Asegúrate de tener este icono
                .setContentTitle("Mensaje motivacional")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(9999, builder.build());

        return Result.success();
    }
}
