package com.example.nequi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FormaDePagoActivity extends AppCompatActivity {

    private TextView disp,tarj;
    private String numLogin,nom,cuanto,mensaje;
    private LinearLayout llTarjeta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forma_de_pago);
        disp=findViewById(R.id.saldoDisponible);
        tarj=findViewById(R.id.saldoTarjeta);
        llTarjeta=findViewById(R.id.llTar);

        //Recuperar datos del SharedPreference
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        numLogin = sessionManager.getPhone();
        consultaDisponible(numLogin);
        if(consultaTarjeta(numLogin)){
            llTarjeta.setVisibility(View.VISIBLE);
        }else{
            llTarjeta.setVisibility(View.INVISIBLE);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("nombre_contacto")) {
                nom=bundle.getString("nombre_contacto");
                cuanto=bundle.getString("numCuanto");
                mensaje=bundle.getString("mensaje");
            }
        } else {
                nom="";
        }
    }


    public void consultaDisponible(String telefono) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select * from usuarios where telefono='" + telefono +"'", null);
        if (fila.moveToFirst()) {
            disp.setText("$  "+fila.getString(5));
        }
        bd.close();

    }

    public boolean consultaTarjeta(String telefono) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select * from Tarjetas where telefono='" + telefono +"'", null);
        if (fila.moveToFirst()) {
            tarj.setText("$  "+fila.getString(3));
            bd.close();
            return true;
        }else{
            bd.close();
            return false;
        }
    }

    public void getTypeDis(View v){
        Intent intent=new Intent(this,EnviaPlataActivity.class);
        intent.putExtra("type","disponible");
        intent.putExtra("nombre_contacto",nom);
        intent.putExtra("numCuanto",cuanto);
        intent.putExtra("mensaje",mensaje);
        startActivity(intent);
    }

    public void getTypeTar(View v){
        Intent intent=new Intent(this,EnviaPlataActivity.class);
        intent.putExtra("type","tarjeta");
        intent.putExtra("nombre_contacto",nom);
        intent.putExtra("numCuanto",cuanto);
        intent.putExtra("mensaje",mensaje);
        startActivity(intent);
    }

    public void back(View v){
        finish();
    }
}