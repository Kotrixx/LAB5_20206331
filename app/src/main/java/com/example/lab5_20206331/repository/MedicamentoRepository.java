package com.example.lab5_20206331.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lab5_20206331.model.Medicamento;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoRepository {

    private static final String PREFS_NAME = "medicamentos_prefs";
    private static final String KEY_MEDICAMENTOS = "medicamentos_list";

    private SharedPreferences prefs;
    private Gson gson;

    public MedicamentoRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Obtener lista completa de medicamentos guardados
    public List<Medicamento> getMedicamentos() {
        String json = prefs.getString(KEY_MEDICAMENTOS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<List<Medicamento>>() {}.getType();
        return gson.fromJson(json, listType);
    }

    // Guardar lista completa de medicamentos
    public void saveMedicamentos(List<Medicamento> medicamentos) {
        String json = gson.toJson(medicamentos);
        prefs.edit().putString(KEY_MEDICAMENTOS, json).apply();
    }

    // Agregar un medicamento nuevo (añade a la lista existente y guarda)
    public void addMedicamento(Medicamento medicamento) {
        List<Medicamento> medicamentos = getMedicamentos();
        medicamentos.add(medicamento);
        saveMedicamentos(medicamentos);
    }

    // Actualizar medicamento en una posición específica
    public void updateMedicamento(int position, Medicamento medicamentoActualizado) {
        List<Medicamento> medicamentos = getMedicamentos();
        if (position >= 0 && position < medicamentos.size()) {
            medicamentos.set(position, medicamentoActualizado);
            saveMedicamentos(medicamentos);
        }
    }

    // Eliminar medicamento en una posición específica
    public void removeMedicamento(int position) {
        List<Medicamento> medicamentos = getMedicamentos();
        if (position >= 0 && position < medicamentos.size()) {
            medicamentos.remove(position);
            saveMedicamentos(medicamentos);
        }
    }
}
