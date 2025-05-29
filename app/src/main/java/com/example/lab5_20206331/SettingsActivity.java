package com.example.lab5_20206331;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    private EditText etFrequencyValue;
    private Spinner spinnerFrequencyUnit;
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
        etFrequencyValue = findViewById(R.id.et_frequency_value);
        spinnerFrequencyUnit = findViewById(R.id.spinner_frequency_unit);
        btnSave = findViewById(R.id.btn_save);

        // Asignar adaptador al Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.frequency_units,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrequencyUnit.setAdapter(adapter);

        // Cargar datos guardados
        etMotivationalMessage.setText(userPreferences.getMotivationalMessage());

        timePickerReminder.setIs24HourView(true);
        timePickerReminder.setHour(userPreferences.getReminderHour());
        timePickerReminder.setMinute(userPreferences.getReminderMinute());

        etFrequencyValue.setText(String.valueOf(userPreferences.getReminderFrequencyValue()));
        String[] frequencyOptions = getResources().getStringArray(R.array.frequency_units);
        String savedUnit = userPreferences.getReminderFrequencyUnit();
        for (int i = 0; i < frequencyOptions.length; i++) {
            if (frequencyOptions[i].equals(savedUnit)) {
                spinnerFrequencyUnit.setSelection(i);
                break;
            }
        }

        btnSave.setOnClickListener(v -> {
            String newMessage = etMotivationalMessage.getText().toString().trim();
            if (newMessage.isEmpty()) {
                Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            if (etFrequencyValue.getText().toString().isEmpty()) {
                Toast.makeText(this, "Indica cada cuánto se repetirá el mensaje", Toast.LENGTH_SHORT).show();
                return;
            }

            int frequencyValue = Integer.parseInt(etFrequencyValue.getText().toString());
            String frequencyUnit = spinnerFrequencyUnit.getSelectedItem().toString();

            // Validar frecuencia mínima de 20 minutos
            long frequencyInMinutes;
            switch (frequencyUnit) {
                case "minutos":
                    frequencyInMinutes = frequencyValue;
                    break;
                case "horas":
                    frequencyInMinutes = frequencyValue * 60L;
                    break;
                case "días":
                    frequencyInMinutes = frequencyValue * 24L * 60L;
                    break;
                default:
                    frequencyInMinutes = 0;
            }

            if (frequencyInMinutes < 20) {
                Toast.makeText(this, "La frecuencia mínima debe ser de 20 minutos", Toast.LENGTH_LONG).show();
                return;
            }

            int hour = timePickerReminder.getHour();
            int minute = timePickerReminder.getMinute();

            // Guardar en preferencias
            userPreferences.saveMotivationalMessage(newMessage);
            userPreferences.saveReminderTime(hour, minute);
            userPreferences.saveReminderFrequency(frequencyValue, frequencyUnit);

            // Programar notificación
            scheduleMotivationalNotification(newMessage, hour, minute, frequencyValue, frequencyUnit);

            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void scheduleMotivationalNotification(String message, int hour, int minute, int freqValue, String freqUnit) {
        Data inputData = new Data.Builder()
                .putString("motivational_message", message)
                .build();

        long initialDelay = calculateInitialDelay(hour, minute);
        long intervalInHours = getRepeatIntervalInHours(freqValue, freqUnit);

        PeriodicWorkRequest periodicRequest = new PeriodicWorkRequest.Builder(
                MotivationalNotificationWorker.class,
                intervalInHours, TimeUnit.HOURS)
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
            targetTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        return targetTime.getTimeInMillis() - now.getTimeInMillis();
    }

    private long getRepeatIntervalInHours(int value, String unit) {
        switch (unit) {
            case "minutos":
                return Math.max(1, TimeUnit.MINUTES.toHours(value));
            case "horas":
                return Math.max(1, value);
            case "días":
                return Math.max(1, value * 24);
            default:
                return 24;
        }
    }
}
