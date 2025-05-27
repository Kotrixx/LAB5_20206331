package com.example.lab5_20206331;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lab5_20206331.model.Medicamento;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoStorage {
    private static final String PREFS_NAME = "medicamentos_data";

    public static void guardar(Context context, List<Medicamento> lista) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(lista);
        editor.putString("medicamentos", json);
        editor.apply();
    }

    public static List<Medicamento> cargar(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString("medicamentos", null);
        Type type = new TypeToken<List<Medicamento>>() {}.getType();
        return json != null ? new Gson().fromJson(json, type) : new ArrayList<>();
    }
}
