package com.example.lab5_20206331;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText etMotivationalMessage;
    private TimePicker timePickerReminder;
    private Button btnSave;

    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userPreferences = new UserPreferences(this);

        etMotivationalMessage = findViewById(R.id.et_motivational_message);
        timePickerReminder = findViewById(R.id.time_picker_reminder);
        btnSave = findViewById(R.id.btn_save);

        // Cargar mensaje guardado
        etMotivationalMessage.setText(userPreferences.getMotivationalMessage());

        // Configurar TimePicker en modo 24h
        timePickerReminder.setIs24HourView(true);

        // Cargar hora guardada
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

            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();

            finish();
        });
    }

}


