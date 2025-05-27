package com.example.lab5_20206331.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lab5_20206331.model.Medicamento;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoStorage {

    private static final String PREFS_NAME = "medicamento_prefs";
    private static final String KEY_MEDICAMENTOS = "medicamentos";

    public static void guardar(Context context, List<Medicamento> lista) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(lista);

        editor.putString(KEY_MEDICAMENTOS, json);
        editor.apply();
    }

    public static List<Medicamento> cargar(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_MEDICAMENTOS, null);

        if (json == null) {
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Type tipoLista = new TypeToken<List<Medicamento>>() {}.getType();

        return gson.fromJson(json, tipoLista);
    }
}
