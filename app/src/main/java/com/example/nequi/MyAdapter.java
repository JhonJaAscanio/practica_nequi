package com.example.nequi;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ModeloItem> datos;
    private Context context;
    private static final int TIPO_LLEGADA = 1;
    private static final int TIPO_ENVIO = 2;

    public MyAdapter(Context context,List<ModeloItem> datos) {
        this.context = context;
        this.datos = datos;
    }

    private boolean p=true;
    @Override
    public int getItemViewType(int position) {
        ModeloItem modeloItem = datos.get(position);

        if (modeloItem.getTexto1().equals(modeloItem.getTexto7())) {
            return TIPO_LLEGADA;
        } else {
            return TIPO_ENVIO;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TIPO_ENVIO) {
            View vista = inflater.inflate(R.layout.item_historial, parent, false);
            return new ViewHolderTipo1(vista);
        } else {
            View vista = inflater.inflate(R.layout.item_historial_envio, parent, false);
            return new ViewHolderTipo2(vista);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModeloItem modeloItem = datos.get(position);

        if (holder instanceof ViewHolderTipo1) {
            ViewHolderTipo1 viewHolderTipo1 = (ViewHolderTipo1) holder;
            viewHolderTipo1.tvNumDes.setText(modeloItem.getTexto8());
            viewHolderTipo1.tvMonto.setText("$ "+modeloItem.getTexto3());
            viewHolderTipo1.tvFechaCreacion.setText(".. "+modeloItem.getTexto6()+" ..");

            viewHolderTipo1.rlLlegada.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    abrirDetalleMovimiento(modeloItem);
                }
            });
        } else if (holder instanceof ViewHolderTipo2) {
            ViewHolderTipo2 viewHolderTipo2 = (ViewHolderTipo2) holder;
            viewHolderTipo2.tvNumDes.setText(modeloItem.getTexto9());
            viewHolderTipo2.tvMonto.setText("$ "+modeloItem.getTexto3());
            viewHolderTipo2.tvFechaCreacion.setText(".. "+modeloItem.getTexto6()+" ..");

            viewHolderTipo2.rlEnvio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    abrirDetalleMovimiento(modeloItem);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    private static class ViewHolderTipo1 extends RecyclerView.ViewHolder {
        public TextView tvNumDes, tvMonto, tvFechaCreacion;
        public RelativeLayout rlLlegada;

        public ViewHolderTipo1(View vista) {
            super(vista);
            tvNumDes = vista.findViewById(R.id.tvNumDes);
            tvFechaCreacion = vista.findViewById(R.id.tvFechaCreacion);
            tvMonto = vista.findViewById(R.id.tvMonto);
            rlLlegada = vista.findViewById(R.id.rlLlegada);
        }

    }

    private static class ViewHolderTipo2 extends RecyclerView.ViewHolder {
        public TextView tvNumDes, tvMonto, tvFechaCreacion;
        public RelativeLayout rlEnvio;
        public ViewHolderTipo2(View vista) {
            super(vista);
            tvNumDes = vista.findViewById(R.id.tvNumDes);
            tvFechaCreacion = vista.findViewById(R.id.tvFechaCreacion);
            tvMonto = vista.findViewById(R.id.tvMonto);
            rlEnvio = vista.findViewById(R.id.rlEnvio);
        }
    }

    private void abrirDetalleMovimiento(ModeloItem modeloItem) {
        Intent intent = new Intent(context, DetalleMovimientoActivity.class);
        intent.putExtra("numOrigen", modeloItem.getTexto1());
        intent.putExtra("numDestino", modeloItem.getTexto2());
        intent.putExtra("monto", modeloItem.getTexto3());
        intent.putExtra("metodo_envio", modeloItem.getTexto4());
        intent.putExtra("mensaje", modeloItem.getTexto5());
        intent.putExtra("fecha_creacion", modeloItem.getTexto6());
        intent.putExtra("numLogeado", modeloItem.getTexto7());

        context.startActivity(intent);
    }


}