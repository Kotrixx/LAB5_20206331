package com.example.lab5_20206331;

import android.content.Context;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.lab5_20206331.model.Medicamento;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public static void scheduleMedicamentoReminder(Context context, Medicamento medicamento) {
        long delay = calculateInitialDelay(medicamento.getFechaHoraInicio());
        if (delay < 0) delay = 0;  // Si la hora inicio ya pasó, dispara inmediato

        Data inputData = new Data.Builder()
                .putString(MedicamentoNotificationWorker.EXTRA_MEDICAMENTO_NOMBRE, medicamento.getNombre())
                .putString(MedicamentoNotificationWorker.EXTRA_MEDICAMENTO_TIPO, medicamento.getTipo())
                .putString(MedicamentoNotificationWorker.EXTRA_MEDICAMENTO_DOSIS, medicamento.getDosis())
                .putInt(MedicamentoNotificationWorker.EXTRA_FRECUENCIA_HORAS, medicamento.getFrecuenciaHoras())
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(MedicamentoNotificationWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build();

        // Usa el nombre único para evitar duplicados (uno por medicamento)
        WorkManager.getInstance(context)
                .enqueueUniqueWork("reminder_" + medicamento.getNombre(), ExistingWorkPolicy.REPLACE, request);
    }

    private static long calculateInitialDelay(String fechaHoraInicio) {
        try {
            long inicioMs = sdf.parse(fechaHoraInicio).getTime();
            long ahoraMs = System.currentTimeMillis();
            return inicioMs - ahoraMs;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
