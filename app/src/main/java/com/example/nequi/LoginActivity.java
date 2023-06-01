package com.example.nequi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText etTelefono;
    private TextView tvError;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvError= findViewById(R.id.tvError);
        btn=(Button) findViewById(R.id.boton);
        etTelefono=(EditText) findViewById(R.id.etTelefono);
        btn.getBackground().setAlpha(128);
        etTelefono.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    // Se ha soltado una tecla


                    String input = etTelefono.getText().toString();
                    if (input.length() == 10) {
                        tvError.setVisibility(View.INVISIBLE);
                        btn.getBackground().setAlpha(255);
                        btn.setClickable(true);
                    } else {
                        tvError.setVisibility(View.VISIBLE);
                        btn.getBackground().setAlpha(80);
                        btn.setClickable(false);
                    }
                    return true; // Indica que el evento ha sido manejado
                }
                return false; // Indica que el evento no ha sido manejado
            }
        });
    }

    public void onBackPressed() {
        Intent intent1= new Intent(this,MainActivity.class);
        startActivity(intent1);
    }

    public void verificarTelefono(View v) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String telefono = etTelefono.getText().toString();
        Cursor fila = bd.rawQuery(
                "select * from usuarios where telefono='" + telefono +"'", null);
        if (fila.moveToFirst()) {
            Intent intent1= new Intent(this, PinAccessActivity.class);
            intent1.putExtra("telefono", telefono);
            startActivity(intent1);
        } else
            Toast.makeText(this, "No se encuentra registrado.. Registrese!!",
                    Toast.LENGTH_SHORT).show();
        bd.close();

    }


    public void back(View v){
        Intent intent1= new Intent(this,MainActivity.class);
        startActivity(intent1);
    }
}