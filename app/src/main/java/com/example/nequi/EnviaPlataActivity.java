package com.example.nequi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EnviaPlataActivity extends AppCompatActivity {

    private EditText etCel, etCuanto, etMensaje;
    private TextView disponible,tvDispo1;
    private ImageView icono_dis,icono_tar;
    private String datoTelefono;
    private String tipo;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envia_plata);

        //Recuperaramos el tipo (Disponible==True y Tarjeta == False
        Bundle bundle = getIntent().getExtras();
        etCel=(EditText) findViewById(R.id.etCel);
        etCuanto=(EditText) findViewById(R.id.etCuanto);
        etMensaje=(EditText) findViewById(R.id.etMensaje);
        disponible=(TextView) findViewById(R.id.tvDispo2);
        tvDispo1=(TextView) findViewById(R.id.tvDispo1);
        icono_dis=findViewById(R.id.icon1);
        icono_tar=findViewById(R.id.icon3);
        btn=(Button) findViewById(R.id.boton);
        btn.getBackground().setAlpha(128);
        btn.setClickable(false);
        if (bundle != null) {
            if (bundle.containsKey("type")) {
                tipo=bundle.getString("type");
            }

            if (bundle.containsKey("nombre_contacto")) {
                String nom=bundle.getString("nombre_contacto");
                String cuan=bundle.getString("numCuanto");
                String mens=bundle.getString("mensaje");
                etCel.setText(nom);
                etCuanto.setText(cuan);
                etMensaje.setText(mens);
                btn.getBackground().setAlpha(255);
                btn.setClickable(true);
            }
        } else {
            tipo="disponible";
        }


        //Recuperar datos del SharedPreference
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        datoTelefono = sessionManager.getPhone();

        consultaDisponible(datoTelefono,tipo);

        etCel.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    // Se ha soltado una tecla

                    String input = etCel.getText().toString();
                    if (input.length() >= 10 || !(input.matches("[0-9]+"))) {
                        btn.getBackground().setAlpha(255);
                        btn.setClickable(true);
                    } else {
                        btn.getBackground().setAlpha(80);
                        btn.setClickable(false);
                    }
                    return true; // Indica que el evento ha sido manejado
                }
                return false; // Indica que el evento no ha sido manejado
            }
        });
    }

    public void Enviar(View v){
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        String celOrigen = sessionManager.getPhone();
        String celDestino= etCel.getText().toString().trim();
        if(!(celDestino.matches("[0-9]+"))) {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "bd_nequi", null, 1);
            SQLiteDatabase bd = admin.getWritableDatabase();
            Cursor fila = bd.rawQuery("select * from usuarios where nombre='" +celDestino+ "'", null);
            if (fila.moveToFirst()) {
                celDestino = fila.getString(3);
                bd.close();
            }else{
                celDestino =celDestino;
                bd.close();

            }
        }
        if(tvDispo1.getText().toString().equals("Disponible")){
            String cuanto= etCuanto.getText().toString();
            String mensaje = etMensaje.getText().toString();

            if(verificarTelefono(celOrigen,celDestino,"disponible")){
                if(verificarMonto(cuanto,celOrigen,"disponible")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Enviar plata");
                    builder.setMessage("¿Estás seguro que desea realizar la transacción?");
                    String finalCelDestino = celDestino;
                    builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            realizarTransaccion(celOrigen,finalCelDestino,cuanto,mensaje,"disponible");
                        }
                    });
                    builder.setNegativeButton("No", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    Toast.makeText(this, "El monto es incorrecto, vuelva a intentarlo!!", Toast.LENGTH_LONG).show();
                }
            }else{
            Toast.makeText(this, "Numero de celular incorrecto!!", Toast.LENGTH_LONG).show();
            }
        }else{  // Seleccion de pago con Tarjeta

            String cuanto= etCuanto.getText().toString();
            String mensaje = etMensaje.getText().toString();

            if(verificarTarjeta(celDestino)) {
                if (verificarTelefono(celOrigen, celDestino, "tarjeta")) {
                    if (verificarMonto(cuanto, celOrigen, "tarjeta")) {
                        String finalCelDestino = celDestino;
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Enviar plata");
                        builder.setMessage("¿Estás seguro que desea realizar la transacción?");
                        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                realizarTransaccion(celOrigen, finalCelDestino, cuanto, mensaje, "tarjeta");
                            }
                        });
                        builder.setNegativeButton("No", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Toast.makeText(this, "El monto es incorrecto, vuelva a intentarlo!!", Toast.LENGTH_LONG).show();
                    }
                }
            }else{
                Toast.makeText(this, "No cuenta con tarjeta!!", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public void realizarTransaccion(String celOrigen,String celDestino,String cuanto,String mensaje,String tipo){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fecha = dateFormat.format(new Date());
        ContentValues registro = new ContentValues();
        registro.put("num_origen", celOrigen);
        registro.put("num_destino", celDestino);
        registro.put("monto", cuanto);
        registro.put("mensaje",mensaje);
        registro.put("fecha_creacion",fecha);
        if(tipo.equals("disponible")) {
            registro.put("metodo_envio", "Celular");
        }else{
            registro.put("metodo_envio", "Tarjeta");
        }
        bd.insert("transacciones", null, registro);
        bd.close();

        if(actualizarSaldo(celOrigen,cuanto,"Origen",tipo)){
            actualizarSaldo(celDestino,cuanto, "Destino",tipo);
            Toast.makeText(this, "¡Transacción Exitosa!",Toast.LENGTH_SHORT).show();
            Intent intent1=new Intent(this, InicioActivity.class);
            intent1.putExtra("telefono", celOrigen);
            startActivity(intent1);
        }else{
            Toast.makeText(this, "¡Error al actualizar saldo!",Toast.LENGTH_SHORT).show();
        }
    }


    public boolean actualizarSaldo(String celular, String cuanto,String desOrOri,String tipo){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila;
        if(tipo.equals("disponible")) {
             fila = bd.rawQuery("select * from usuarios where telefono='" + celular + "'", null);
            if (fila.moveToFirst()) {
                BigDecimal saldoViejo = new BigDecimal(fila.getString(5));
                BigDecimal monto = new BigDecimal(cuanto);
                BigDecimal saldoNuevo;

                if (desOrOri.equals("Origen")) {
                    if (saldoViejo.compareTo(monto) < 0) {    // Validar que el monto se ajuste al presupuesto actual
                        return false;
                    }
                    saldoNuevo = saldoViejo.subtract(monto);
                } else {
                    saldoNuevo = saldoViejo.add(monto);
                }
                String saldoNuevoS = saldoNuevo.toString();
                ContentValues registro = new ContentValues();
                registro.put("saldo", saldoNuevoS);
                int cant = bd.update("usuarios", registro, "telefono=" + celular, null);

            }
        }else {  // Tarjetaaaa

            fila = bd.rawQuery("select * from tarjetas where telefono='" + celular + "'", null);
            if (fila.moveToFirst()) {
                BigDecimal saldoViejo = new BigDecimal(fila.getString(3));
                BigDecimal monto = new BigDecimal(cuanto);
                BigDecimal saldoNuevo;

                if (desOrOri.equals("Origen")) {
                    if (saldoViejo.compareTo(monto) < 0) {    // Validar que el monto se ajuste al presupuesto actual
                        return false;
                    }
                    saldoNuevo = saldoViejo.subtract(monto);
                } else {
                    saldoNuevo = saldoViejo.add(monto);
                }
                String saldoNuevoS = saldoNuevo.toString();
                ContentValues registro = new ContentValues();
                registro.put("saldo", saldoNuevoS);
                int cant = bd.update("tarjetas", registro, "telefono=" + celular, null);

            }

        }
        fila.close();
        bd.close();
        return true;
    }

    public boolean verificarTelefono(String celOrigen, String celDestino,String tipo){
        if(celOrigen.equals(celDestino) || celDestino.isEmpty()){
            return false;
        }
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        if(tipo.equals("disponible")) {
            Cursor fila = bd.rawQuery("select * from usuarios where telefono='" + celDestino + "'", null);
            if (fila.moveToFirst()) {
                bd.close();
                return true;
            }else{
                return false;
            }
        }else{
            Cursor fila = bd.rawQuery("select * from tarjetas where telefono='" + celDestino + "'", null);
            if (fila.moveToFirst()) {
                bd.close();
                return true;
            }
        }
        return true;
    }

    public  boolean verificarMonto(String monto,String celOrigen,String tipo){

        if(monto.isEmpty() ){
            return false;
        }
        char c = monto.charAt(0);
        if(c == '0'){
            return false;
        }

        if(!(monto.matches("[0-9]+"))){
            return false;
        }


        BigDecimal saldo= new BigDecimal(monto);
        if (saldo.compareTo(BigDecimal.ZERO) <= 0) {  //Validar que el monto sea mayor a cero
            return false;
        }

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        if(tipo.equals("disponible")) {
            Cursor fila = bd.rawQuery("select * from usuarios where telefono='" + celOrigen + "'", null);
            if (fila.moveToFirst()) {
                BigDecimal saldoViejo = new BigDecimal(fila.getString(5));
                BigDecimal montoBD = new BigDecimal(monto);
                if (saldoViejo.compareTo(montoBD) < 0) {    // Validar que el monto se ajuste al presupuesto actual
                    return false;
                }
            }
        }else if(tipo.equals("tarjeta")){
            Cursor fila = bd.rawQuery("select * from tarjetas where telefono='" + celOrigen + "'", null);
            if (fila.moveToFirst()) {
                BigDecimal saldoViejo = new BigDecimal(fila.getString(3));
                BigDecimal montoBD = new BigDecimal(monto);
                if (saldoViejo.compareTo(montoBD) < 0) {    // Validar que el monto se ajuste al presupuesto actual
                    return false;
                }
            }
        }

        return true;
    }


    public void consultaDisponible(String telefono,String dispon) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        if(dispon.equals("disponible")){
            tvDispo1.setText("Disponible");
            icono_dis.setVisibility(View.VISIBLE);
            icono_tar.setVisibility(View.INVISIBLE);
            Cursor fila = bd.rawQuery(
                    "select * from usuarios where telefono='" + telefono +"'", null);
            if (fila.moveToFirst()) {
                disponible.setText(fila.getString(5));
            }
        }else{
            tvDispo1.setText("Tarjeta");
            icono_dis.setVisibility(View.INVISIBLE);
            icono_tar.setVisibility(View.VISIBLE);
            Cursor fila = bd.rawQuery(
                    "select * from tarjetas where telefono='" + telefono +"'", null);
            if (fila.moveToFirst()) {
                disponible.setText(fila.getString(3));
            }
        }

        bd.close();

    }


    public void verDisponible(View v){
        Intent i=new Intent(this,FormaDePagoActivity.class);
        if(TextUtils.isEmpty(etCel.getText().toString())){

        }else{
            i.putExtra("nombre_contacto",etCel.getText().toString());
            i.putExtra("numCuanto",etCuanto.getText().toString());
            i.putExtra("mensaje",etMensaje.getText().toString());
        }
        startActivity(i);
    }

    public void atras(View v){

        Intent intent1= new Intent(this,InicioActivity.class);
        intent1.putExtra("telefono", datoTelefono);
        startActivity(intent1);
    }

    public void onBackPressed() {
        Intent intent1= new Intent(this,InicioActivity.class);
        intent1.putExtra("telefono", datoTelefono);
        startActivity(intent1);
    }

    public boolean verificarTarjeta(String tel){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
            Cursor fila = bd.rawQuery("select * from tarjetas where telefono='" + tel + "'", null);
            if (fila.moveToFirst()) {
                bd.close();
                return true;
            }
            return false;
    }

    public void users(View v){
        Intent i=new Intent(this,ListUsersActivity.class);
        i.putExtra("type",tipo);
        i.putExtra("numCuanto",etCuanto.getText().toString());
        i.putExtra("mensaje",etMensaje.getText().toString());
        startActivity(i);
    }

}