package com.example.lab5_20206331;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.lab5_20206331.model.Medicamento;
import com.example.lab5_20206331.repository.MedicamentoRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RegistroMedicamentoActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etDosis, etFrecuencia, etFechaInicio;
    private AutoCompleteTextView spinnerTipo;
    private Button btnGuardar;

    private MedicamentoRepository medicamentoRepository;

    private final Calendar calendar = Calendar.getInstance();

    private final String[] tiposMedicamento = {"Pastilla", "Jarabe", "Inyección", "Ampolla", "Cápsula"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_medicamento);

        medicamentoRepository = new MedicamentoRepository(this);

        etNombre = findViewById(R.id.et_medication_name);
        etDosis = findViewById(R.id.et_dose);
        etFrecuencia = findViewById(R.id.et_frequency);
        etFechaInicio = findViewById(R.id.et_start_date);
        spinnerTipo = findViewById(R.id.spinner_medication_type);
        btnGuardar = findViewById(R.id.btn_save_medication);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, tiposMedicamento);
        spinnerTipo.setAdapter(adapter);

        etFechaInicio.setFocusable(false);
        etFechaInicio.setClickable(true);
        etFechaInicio.setOnClickListener(v -> mostrarSelectorFechaHora());

        btnGuardar.setOnClickListener(v -> guardarMedicamento());
    }

    private void mostrarSelectorFechaHora() {
        DatePickerDialog.OnDateSetListener fechaListener = (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            TimePickerDialog.OnTimeSetListener horaListener = (timePicker, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                actualizarCampoFechaInicio();
            };
            new TimePickerDialog(RegistroMedicamentoActivity.this, horaListener,
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        };
        new DatePickerDialog(RegistroMedicamentoActivity.this, fechaListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void actualizarCampoFechaInicio() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        etFechaInicio.setText(sdf.format(calendar.getTime()));
    }

    private void guardarMedicamento() {
        String nombre = etNombre.getText().toString().trim();
        String tipo = spinnerTipo.getText().toString().trim();
        String dosis = etDosis.getText().toString().trim();
        String frecuenciaStr = etFrecuencia.getText().toString().trim();
        String fechaInicio = etFechaInicio.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Ingrese el nombre del medicamento");
            etNombre.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(tipo)) {
            Toast.makeText(this, "Seleccione el tipo de medicamento", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(dosis)) {
            etDosis.setError("Ingrese la dosis");
            etDosis.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(frecuenciaStr)) {
            etFrecuencia.setError("Ingrese la frecuencia en horas");
            etFrecuencia.requestFocus();
            return;
        }
        int frecuencia;
        try {
            frecuencia = Integer.parseInt(frecuenciaStr);
            if (frecuencia <= 0) {
                etFrecuencia.setError("La frecuencia debe ser mayor que 0");
                etFrecuencia.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etFrecuencia.setError("Ingrese un número válido");
            etFrecuencia.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(fechaInicio)) {
            Toast.makeText(this, "Seleccione la fecha y hora de inicio", Toast.LENGTH_SHORT).show();
            return;
        }

        Medicamento medicamento = new Medicamento(nombre, tipo, dosis, frecuencia, fechaInicio);
        medicamentoRepository.addMedicamento(medicamento);

        // Programa recordatorio usando WorkManager con tag único (nombre)
        scheduleMedicationNotification(medicamento);

        Toast.makeText(this, "Medicamento guardado", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void scheduleMedicationNotification(Medicamento medicamento) {
        Data inputData = new Data.Builder()
                .putString(MedicamentoNotificationWorker.EXTRA_MEDICAMENTO_NOMBRE, medicamento.getNombre())
                .putString(MedicamentoNotificationWorker.EXTRA_MEDICAMENTO_TIPO, medicamento.getTipo())
                .putString(MedicamentoNotificationWorker.EXTRA_MEDICAMENTO_DOSIS, medicamento.getDosis())
                .build();

        long frequencyMinutes = medicamento.getFrecuenciaHoras() * 60L; // frecuencia en minutos

        // Parsear la fecha de inicio del medicamento
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        long startMillis = 0L;
        try {
            startMillis = sdf.parse(medicamento.getFechaHoraInicio()).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            startMillis = System.currentTimeMillis(); // fallback
        }

        long nowMillis = System.currentTimeMillis();

        // Calcular retraso inicial: desde ahora hasta la siguiente dosis
        long delayMillis;
        if (nowMillis <= startMillis) {
            // Si aún no llegó la primera dosis, delay es desde ahora hasta la fecha inicio
            delayMillis = startMillis - nowMillis;
        } else {
            // Si ya pasó la fecha inicio, calcular múltiplos de frecuencia para próxima dosis
            long elapsedSinceStart = nowMillis - startMillis;
            long periodsPassed = elapsedSinceStart / (frequencyMinutes * 60_000);
            long nextDoseMillis = startMillis + (periodsPassed + 1) * frequencyMinutes * 60_000;
            delayMillis = nextDoseMillis - nowMillis;
        }

        long delayMinutes = Math.max(delayMillis / 60000, 0);

        String workTag = "medicamento_" + medicamento.getNombre();

        PeriodicWorkRequest periodicRequest = new PeriodicWorkRequest.Builder(
                MedicamentoNotificationWorker.class,
                frequencyMinutes, TimeUnit.MINUTES)
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .setInputData(inputData)
                .addTag(workTag)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                workTag,
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicRequest);
    }

}
