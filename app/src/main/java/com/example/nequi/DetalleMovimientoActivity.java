package com.example.nequi;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetalleMovimientoActivity extends AppCompatActivity {

    private String numDestino,monto,fecha_creacion,numOrigen,metodo_envio,mensaje,numLogeado;
    private TextView tvTipo,tvNombreDetalle,tvMensajeDetalle,tvMontoDetalle,tvNumNequi,tvFechaDetalle,tvFormaPago,tvDispoDetalle,tvPara;
    private ImageView icono;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_movimiento);

        tvTipo = findViewById(R.id.tvTipo);
        tvNombreDetalle = findViewById(R.id.tvNombreDetalle);
        tvMensajeDetalle = findViewById(R.id.tvMensajeDetalle);
        tvMontoDetalle = findViewById(R.id.tvMontoDetalle);
        tvNumNequi = findViewById(R.id.tvNumNequi);
        tvFechaDetalle = findViewById(R.id.tvFechaDetalle);
        tvFormaPago = findViewById(R.id.tvFormaPago);
        tvDispoDetalle = findViewById(R.id.tvDispoDetalle);
        tvPara= findViewById(R.id.tvPara);
        icono = findViewById(R.id.icono);

        Bundle bundle = getIntent().getExtras();
        numDestino=bundle.getString("numDestino");
        numOrigen=bundle.getString("numOrigen");
        monto=bundle.getString("monto");
        metodo_envio=bundle.getString("metodo_envio");
        mensaje=bundle.getString("mensaje");
        fecha_creacion=bundle.getString("fecha_creacion");
        numLogeado=bundle.getString("numLogeado");

        tvMontoDetalle.setText(monto);

        tvFormaPago.setText(metodo_envio);
        tvMensajeDetalle.setText(mensaje);
        tvFechaDetalle.setText(fecha_creacion);

        getMoneyDispo(numLogeado);
        recibidoEnviado(numOrigen,numDestino,numLogeado);
    }

    public void getNameDes(String num){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select * from usuarios where telefono='" + num +"'", null);
        if (fila.moveToFirst()) {
            tvNombreDetalle.setText(fila.getString(1));
        }
        bd.close();
    }

    public void getMoneyDispo(String num){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select * from usuarios where telefono='" + num +"'", null);
        if (fila.moveToFirst()) {
            tvDispoDetalle.setText(fila.getString(5));
        }
        bd.close();
    }

    public void recibidoEnviado(String numO,String numD,  String numLog){

        if(numO.equals(numLog)){
            tvNumNequi.setText(numD);
            tvTipo.setText("Envio Real");
            tvTipo.setTextColor(Color.RED);
            tvPara.setText("Para");
            getNameDes(numD);
            icono.setBackgroundResource(R.drawable.icono_cash_out);
        }else{
            tvNumNequi.setText(numO);
            tvTipo.setText("Recibido Real");
            tvTipo.setTextColor(getResources().getColor(R.color.green_icono));
            tvPara.setText("De");
            getNameDes(numO);
            icono.setBackgroundResource(R.drawable.icono_cash_up);
            icono.setRotation(90f);
        }
    }


    public void back(View v){
        finish();
    }
}