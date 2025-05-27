package com.example.lab5_20206331;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.lab5_20206331.model.Medicamento;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RegistroMedicamentoActivity extends AppCompatActivity {

    private TextInputEditText etName, etFrequency, etDose, etStartDate;
    private AutoCompleteTextView spinnerType;
    private Button btnSave;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    private final String[] medicationTypes = {"Pastilla", "Jarabe", "Inyección", "Ampolla", "Cápsula"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_medicamento);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        etName = findViewById(R.id.et_medication_name);
        spinnerType = findViewById(R.id.spinner_medication_type);
        etFrequency = findViewById(R.id.et_frequency);
        etDose = findViewById(R.id.et_dose);
        etStartDate = findViewById(R.id.et_start_date);
        btnSave = findViewById(R.id.btn_save_medication);

        // Configurar dropdown con tipos
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, medicationTypes);
        spinnerType.setAdapter(adapter);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        etStartDate.setText(dateFormat.format(calendar.getTime()));

        etStartDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveMedication());
    }

    private void showDatePicker() {
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            etStartDate.setText(dateFormat.format(calendar.getTime()));
        }, y, m, d);

        dpd.show();
    }

    private void saveMedication() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String type = spinnerType.getText() != null ? spinnerType.getText().toString().trim() : "";
        String freqStr = etFrequency.getText() != null ? etFrequency.getText().toString().trim() : "";
        String dose = etDose.getText() != null ? etDose.getText().toString().trim() : "";
        String startDate = etStartDate.getText() != null ? etStartDate.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            etName.setError("Ingrese nombre");
            return;
        }
        if (TextUtils.isEmpty(type)) {
            spinnerType.setError("Seleccione tipo");
            return;
        }
        if (TextUtils.isEmpty(freqStr)) {
            etFrequency.setError("Ingrese frecuencia");
            return;
        }
        if (TextUtils.isEmpty(dose)) {
            etDose.setError("Ingrese dosis");
            return;
        }
        int freq;
        try {
            freq = Integer.parseInt(freqStr);
            if (freq <= 0) {
                etFrequency.setError("Frecuencia debe ser mayor que 0");
                return;
            }
        } catch (NumberFormatException e) {
            etFrequency.setError("Frecuencia inválida");
            return;
        }

        // Guardar medicamento (aquí usarías tu lógica de almacenamiento)
        // Por ejemplo:
        Medicamento med = new Medicamento(name, type, dose, freq, startDate);
        List<Medicamento> lista = MedicamentoStorage.cargar(this);
        lista.add(med);
        MedicamentoStorage.guardar(this, lista);

        Toast.makeText(this, "Medicamento guardado", Toast.LENGTH_SHORT).show();
        finish();
    }
}
