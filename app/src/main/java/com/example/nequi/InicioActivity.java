package com.example.nequi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InicioActivity extends AppCompatActivity {

    private String datoTelefono,telefono;
    private TextView tvUsuario,tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        if (DailyActionHelper.shouldPerformAction(this)) {
            updateMoney();
        }

        tvUsuario=(TextView) findViewById(R.id.tvUsuario);
        tvTotal=(TextView) findViewById(R.id.tvTotal);

        //Recuperaramos el numero al cual ingresaremos a SharedPreference
        Bundle bundle = getIntent().getExtras();
        telefono=bundle.getString("telefono");

        //Recuperar datos del SharedPreference
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        datoTelefono = sessionManager.getPhone();

        //Recuperar datos de la base de datos con el numero de telefono como clave
        consultaDatos(telefono);

    }


    public void consultaDatos(String datoTelefono) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String telefono = datoTelefono;
        Cursor fila = bd.rawQuery(
                "select * from usuarios where telefono='" + telefono +"'", null);
        if (fila.moveToFirst()) {
            tvUsuario.setText(fila.getString(1));
            tvTotal.setText("$ "+fila.getString(5));
        }
        bd.close();

    }

    public void salir(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cerrar sesión");
        builder.setMessage("¿Estás seguro de que deseas cerrar sesión?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cerrarSesion();
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public  void cerrarSesion(){
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.clearSession();
        Intent intent1=new Intent(this, MainActivity.class);
        startActivity(intent1);
    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cerrar sesión");
        builder.setMessage("¿Estás seguro que quieres salir de Nequi?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cerrarSesion();
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void enviar(View v){
        Intent inten1= new Intent(this,EnviaPlataActivity.class);
        startActivity(inten1);
    }
    public void historial(View v){
        Intent inten1= new Intent(this, HistorialActivity.class);
        inten1.putExtra("telefono",telefono);
        startActivity(inten1);
    }

    public void tarjeta(View view){
        Intent intent1 = new Intent(this,TarjetaActivity.class);
        intent1.putExtra("telefono",telefono);
        startActivity(intent1);
    }


    public void updateMoney(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        String saldoNuevoS= "1000000";
        ContentValues registro = new ContentValues();
        registro.put("saldo", saldoNuevoS);
        int cant = bd.update("usuarios", registro, "1=1" , null);
        if(cant==1){
            Toast.makeText(this, "Saldo actualizado", Toast.LENGTH_SHORT).show();
        }
        bd.close();
    }
}