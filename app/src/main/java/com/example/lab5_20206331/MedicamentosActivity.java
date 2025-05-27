package com.example.lab5_20206331;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab5_20206331.model.Medicamento;
import com.example.lab5_20206331.repository.MedicamentoRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MedicamentosActivity extends AppCompatActivity {

    private RecyclerView rvMedicamentos;
    private MedicamentoAdapter adapter;
    private List<Medicamento> listaMedicamentos;
    private TextView tvEmpty;
    private MedicamentoRepository medicamentoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamentos);

        rvMedicamentos = findViewById(R.id.rv_medications);
        tvEmpty = findViewById(R.id.tv_empty);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_medication);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView btnBack = toolbar.findViewById(R.id.btn_back);

        setSupportActionBar(toolbar);
        btnBack.setOnClickListener(v -> finish());

        medicamentoRepository = new MedicamentoRepository(this);
        listaMedicamentos = medicamentoRepository.getMedicamentos();

        adapter = new MedicamentoAdapter(listaMedicamentos, this, position -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Quieres eliminar este medicamento?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        medicamentoRepository.removeMedicamento(position);
                        refreshMedicamentos();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        rvMedicamentos.setLayoutManager(new LinearLayoutManager(this));
        rvMedicamentos.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroMedicamentoActivity.class);
            startActivity(intent);
        });

        refreshMedicamentos();
    }

    private void refreshMedicamentos() {
        listaMedicamentos.clear();
        listaMedicamentos.addAll(medicamentoRepository.getMedicamentos());
        adapter.notifyDataSetChanged();

        if (listaMedicamentos.isEmpty()) {
            tvEmpty.setVisibility(TextView.VISIBLE);
            rvMedicamentos.setVisibility(RecyclerView.GONE);
        } else {
            tvEmpty.setVisibility(TextView.GONE);
            rvMedicamentos.setVisibility(RecyclerView.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMedicamentos();
    }
}
