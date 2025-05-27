package com.example.lab5_20206331;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class MedicamentoNotificationWorker extends Worker {

    public static final String EXTRA_MEDICAMENTO_NOMBRE = "medicamento_nombre";
    public static final String EXTRA_MEDICAMENTO_TIPO = "medicamento_tipo";
    public static final String EXTRA_MEDICAMENTO_DOSIS = "medicamento_dosis";
    public static final String EXTRA_FRECUENCIA_HORAS = "frecuencia_horas";
    public static final String EXTRA_FECHA_INICIO = "fecha_inicio"; // Opcional para cálculo inicial


    public MedicamentoNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String nombre = getInputData().getString(EXTRA_MEDICAMENTO_NOMBRE);
        String tipo = getInputData().getString(EXTRA_MEDICAMENTO_TIPO);
        String dosis = getInputData().getString(EXTRA_MEDICAMENTO_DOSIS);
        int frecuenciaHoras = getInputData().getInt(EXTRA_FRECUENCIA_HORAS, 8);

        String channelId = getChannelIdByTipo(tipo);
        String contentText = "Tomar " + dosis;

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle(nombre)
                .setContentText(contentText)
                .setSmallIcon(getIconByTipo(tipo))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(nombre.hashCode(), notification);

        // Reprogramar siguiente notificación
        scheduleNextReminder(nombre, tipo, dosis, frecuenciaHoras);

        return Result.success();
    }

    private void scheduleNextReminder(String nombre, String tipo, String dosis, int frecuenciaHoras) {
        Data inputData = new Data.Builder()
                .putString(EXTRA_MEDICAMENTO_NOMBRE, nombre)
                .putString(EXTRA_MEDICAMENTO_TIPO, tipo)
                .putString(EXTRA_MEDICAMENTO_DOSIS, dosis)
                .putInt(EXTRA_FRECUENCIA_HORAS, frecuenciaHoras)
                .build();

        OneTimeWorkRequest nextRequest = new OneTimeWorkRequest.Builder(MedicamentoNotificationWorker.class)
                .setInitialDelay(frecuenciaHoras, TimeUnit.HOURS)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(getApplicationContext())
                .enqueueUniqueWork("reminder_" + nombre, ExistingWorkPolicy.REPLACE, nextRequest);
    }



    private String getChannelIdByTipo(String tipo) {
        switch (tipo.toLowerCase()) {
            case "pastilla": return MyApp.CHANNEL_PASTILLA;
            case "jarabe": return MyApp.CHANNEL_JARABE;
            case "ampolla": return MyApp.CHANNEL_AMPOLLA;
            case "cápsula": return MyApp.CHANNEL_CAPSULA;
            default: return MyApp.CHANNEL_PASTILLA;
        }
    }

    private int getIconByTipo(String tipo) {
        // Retorna diferentes íconos según el tipo (usa recursos drawable)
        switch (tipo.toLowerCase()) {
            case "pastilla": return R.drawable.ic_pastilla;
            case "jarabe": return R.drawable.ic_jarabe;
            case "ampolla": return R.drawable.ic_ampolla;
            case "cápsula": return R.drawable.ic_capsula;
            default: return R.drawable.ic_medicamento;
        }
    }
}
