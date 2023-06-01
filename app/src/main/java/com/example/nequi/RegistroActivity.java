package com.example.nequi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etCorreo, etTelefono, etPin, etRepPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);


        etNombre=(EditText) findViewById(R.id.etNombre);
        etCorreo=(EditText) findViewById(R.id.etCorreo);
        etTelefono=(EditText) findViewById(R.id.etTelefono);
        etPin=(EditText) findViewById(R.id.etPin);
        etRepPin=(EditText) findViewById(R.id.etRepPin);
    }

    public void salvarDatos(View view){
        String nombre = etNombre.getText().toString();
        String correo = etCorreo.getText().toString();
        String telefono = etTelefono.getText().toString();
        String pin = etPin.getText().toString();
        String repPin = etRepPin.getText().toString();
        int saldo=1000000;

        if(verificarCampos()){
            if(isValidEmail(correo)) {
                if (verificarTelefono(telefono)) {
                    if(camposVacio()) {
                        if (pin.length() == 4) {
                            if (pin.equals(repPin)) {
                                if (telefono.length() == 10) {
                                    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "bd_nequi", null, 1);
                                    SQLiteDatabase bd = admin.getWritableDatabase();
                                    // Obtener la fecha y hora actual de Colombia
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                                    String fecha = dateFormat.format(new Date());
                                    ContentValues registro = new ContentValues();
                                    registro.put("nombre", nombre);
                                    registro.put("correo", correo);
                                    registro.put("telefono", telefono);
                                    registro.put("pin", pin);
                                    registro.put("saldo", saldo);
                                    registro.put("fecha_creacion", fecha);
                                    bd.insert("usuarios", null, registro);
                                    bd.close();
                                    Toast.makeText(this, "¡Usuario registrado Exitosamente!", Toast.LENGTH_SHORT).show();

                                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                                    sessionManager.saveSession(telefono);

                                    Intent intent1 = new Intent(this, InicioActivity.class);
                                    intent1.putExtra("telefono", telefono);
                                    startActivity(intent1);
                                } else {
                                    Toast.makeText(this, "Digite un numero de celular correcto", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(this, "Digite bien el PIN de seguridad", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Deben ser de 4 digitos el PIN", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "Numero de celular incorrecto, intente con otro!", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, "Correo Incorrecto", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Debe llenar los campos",Toast.LENGTH_LONG).show();
        }

    }



    public boolean verificarTelefono(String telefono){
        char c = telefono.charAt(0);
        if(c!='3'){
            return false;
        }
        if(telefono.length()!=10 ||  !(telefono.matches("[0-9]+"))){
            return false;
        }
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from usuarios where telefono='" + telefono +"'", null);
        if (fila.moveToFirst()) {
            return false;
        }
        bd.close();
        return true;
    }

    public boolean verificarCampos(){
        if (etNombre.getText().toString().length() == 0 || etCorreo.getText().toString().length() == 0 || etPin.getText().toString().length() == 0){
            return false;
        }
     return true;
    }

    public boolean camposVacio(){
        String nombre = etNombre.getText().toString();
        String mail = etCorreo.getText().toString();
        if (nombre.startsWith(" ")) {
            Toast.makeText(this, "Contiene espacios en blanco el Nombre", Toast.LENGTH_SHORT).show();
            return false;
        } else if(mail.startsWith(" ")){
            Toast.makeText(this, "Contiene espacios en blanco el Correo", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }

    public static boolean isValidEmail(String email) {
        // Expresión regular para validar el formato del correo electrónico
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public void back(View v){
        finish();
    }
}