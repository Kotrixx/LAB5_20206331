package com.example.lab5_20206331;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MedicamentoNotificationWorker extends Worker {

    public static final String EXTRA_MEDICAMENTO_NOMBRE = "medicamento_nombre";
    public static final String EXTRA_MEDICAMENTO_TIPO = "medicamento_tipo";
    public static final String EXTRA_MEDICAMENTO_DOSIS = "medicamento_dosis";

    public MedicamentoNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String nombre = getInputData().getString(EXTRA_MEDICAMENTO_NOMBRE);
        String tipo = getInputData().getString(EXTRA_MEDICAMENTO_TIPO);
        String dosis = getInputData().getString(EXTRA_MEDICAMENTO_DOSIS);

        String channelId = getChannelIdByTipo(tipo);
        String contentText = "Tomar " + dosis;

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle(nombre)
                .setContentText(contentText)
                .setSmallIcon(getIconByTipo(tipo))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(nombre.hashCode(), notification);

        return Result.success();
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
