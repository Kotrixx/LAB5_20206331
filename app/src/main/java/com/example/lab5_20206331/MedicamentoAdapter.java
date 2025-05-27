package com.example.lab5_20206331;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab5_20206331.model.Medicamento;

import java.util.List;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.ViewHolder> {

    private List<Medicamento> listaMedicamentos;
    private Context context;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDelete(int position);
    }

    public MedicamentoAdapter(List<Medicamento> lista, Context context, OnDeleteClickListener listener) {
        this.listaMedicamentos = lista;
        this.context = context;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public MedicamentoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medicamento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicamentoAdapter.ViewHolder holder, int position) {
        Medicamento med = listaMedicamentos.get(position);
        holder.tvName.setText(med.getNombre());
        String details = med.getDosis() + " - Cada " + med.getFrecuenciaHoras() + " horas";
        holder.tvDetails.setText(details);
        holder.tvTimeSince.setText("Desde: " + med.getFechaHoraInicio());

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaMedicamentos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvTimeSince;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_medication_name);
            tvDetails = itemView.findViewById(R.id.tv_medication_details);
            tvTimeSince = itemView.findViewById(R.id.tv_time_since);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}

