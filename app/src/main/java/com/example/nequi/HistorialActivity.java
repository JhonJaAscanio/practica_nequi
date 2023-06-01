package com.example.nequi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private String celLogeado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        //Recuperar Numero de telefono del Intent anterior
        Bundle bundle = getIntent().getExtras();
        celLogeado=bundle.getString("telefono");

        RecyclerView recyclerView = findViewById(R.id.rvHistorial);

        List<ModeloItem> datos = new ArrayList<>();

       //Extraer datos de la base de datos(Tabla transacciones ---------------
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select * from transacciones", null);
        if (fila.moveToFirst()) {
            do {
                // Obtén los valores de cada columna en el cursor
                String telOrigen = fila.getString(1);
                String telDestino = fila.getString(2);
                String monto = fila.getString(3);
                String metodo_envio = fila.getString(4);
                String mensaje =fila.getString(5);
                String fecha_creacion= fila.getString(6);
                String nombreOri = getName(telOrigen);
                String nombreDes = getName(telDestino);

                if(celLogeado.equals(telOrigen) || celLogeado.equals(telDestino)){
                    ModeloItem modelo = new ModeloItem(telOrigen, telDestino, monto, metodo_envio, mensaje, fecha_creacion,celLogeado,nombreOri,nombreDes);
                    datos.add(modelo);
                }

                } while (fila.moveToNext());
        }
        bd.close();
        //Fin extraer datos-----------------------------------------------------

        // Configura el adaptador con la lista de datos
        MyAdapter adaptador = new MyAdapter(HistorialActivity.this,datos);

        // Configura el LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // Establece el adaptador en el RecyclerView
        recyclerView.setAdapter(adaptador);

    }  // FIN onCreate

    public String getName(String num){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from usuarios where telefono='"+num+"'", null);
        if (fila.moveToFirst()) {
            return fila.getString(1);
        }
        bd.close();
        return "";
    }


    public void back(View v){
        finish();
    }


}