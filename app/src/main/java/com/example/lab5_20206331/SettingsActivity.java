package com.example.lab5_20206331;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {

    private EditText etMotivationalMessage;
    private TimePicker timePickerReminder;
    private Button btnSave;

    private UserPreferences userPreferences;

    private static final String WORK_NAME_MOTIVATIONAL = "motivational_notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userPreferences = new UserPreferences(this);

        etMotivationalMessage = findViewById(R.id.et_motivational_message);
        timePickerReminder = findViewById(R.id.time_picker_reminder);
        btnSave = findViewById(R.id.btn_save);

        etMotivationalMessage.setText(userPreferences.getMotivationalMessage());
        timePickerReminder.setIs24HourView(true);
        timePickerReminder.setHour(userPreferences.getReminderHour());
        timePickerReminder.setMinute(userPreferences.getReminderMinute());

        btnSave.setOnClickListener(v -> {
            String newMessage = etMotivationalMessage.getText().toString().trim();
            if (newMessage.isEmpty()) {
                Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            userPreferences.saveMotivationalMessage(newMessage);
            int hour = timePickerReminder.getHour();
            int minute = timePickerReminder.getMinute();
            userPreferences.saveReminderTime(hour, minute);

            scheduleMotivationalNotification(newMessage, hour, minute);

            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void scheduleMotivationalNotification(String message, int hour, int minute) {
        Data inputData = new Data.Builder()
                .putString("motivational_message", message)
                .build();

        long initialDelay = calculateInitialDelay(hour, minute);

        PeriodicWorkRequest periodicRequest = new PeriodicWorkRequest.Builder(
                MotivationalNotificationWorker.class,
                24, TimeUnit.HOURS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                WORK_NAME_MOTIVATIONAL,
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicRequest
        );
    }

    private long calculateInitialDelay(int targetHour, int targetMinute) {
        Calendar now = Calendar.getInstance();
        Calendar targetTime = (Calendar) now.clone();
        targetTime.set(Calendar.HOUR_OF_DAY, targetHour);
        targetTime.set(Calendar.MINUTE, targetMinute);
        targetTime.set(Calendar.SECOND, 0);
        targetTime.set(Calendar.MILLISECOND, 0);

        if (targetTime.before(now)) {
            // Si la hora objetivo ya pasó hoy, se programa para mañana
            targetTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        return targetTime.getTimeInMillis() - now.getTimeInMillis();
    }
}
