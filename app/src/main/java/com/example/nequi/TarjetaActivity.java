package com.example.nequi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class TarjetaActivity extends AppCompatActivity {

    private boolean control=true;
    private String telefono;
    private FrameLayout flFront;
    private FrameLayout flBack;
    private TextView tvNombre,tvNumCuenta, tvNumCuentaRefresh,tvMoneyCard;
    private ConstraintLayout btnMas, btnMenos;
    private boolean isFlipped = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Recuperar datos del SharedPreference
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        telefono = sessionManager.getPhone();

        if (consultaTarjeta(telefono)){
            setContentView(R.layout.activity_tarjeta);

            flFront = findViewById(R.id.flFront);
            flBack = findViewById(R.id.flBack);
            tvNombre = findViewById(R.id.tvNombre);
            tvNumCuenta = findViewById(R.id.tvNumCuenta);
            tvMoneyCard = findViewById(R.id.tvMoneyCard);
            btnMas = findViewById(R.id.btnMas);
            btnMenos = findViewById(R.id.btnMenos);

            tvNombre.setText(consultaDatos(telefono));
                tvNumCuenta.setText(formatearNumeroTarjeta(consultaNumCuenta(telefono)));
            tvMoneyCard.setText("$ " +getMoneyCard());
        }else{
            setContentView(R.layout.activity_create_card);
            tvNumCuentaRefresh = findViewById(R.id.tvNumCuentaRefresh);
            tvNumCuentaRefresh.setText(formatearNumeroTarjeta(generarNumRandom()));
        }

    }

    public void createNewNumber(View v){
        tvNumCuentaRefresh.setText(formatearNumeroTarjeta(generarNumRandom()));
    }

    public String consultaDatos(String telefono) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from usuarios where telefono='" + telefono +"'", null);
        if (fila.moveToFirst()) {
            return fila.getString(1);
        }
        bd.close();
        return "";
    }

    public String consultaNumCuenta(String telefono) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select * from tarjetas where telefono='" + telefono +"'", null);
        if (fila.moveToFirst()) {
            return fila.getString(1);
        }
        bd.close();
        return "Vacio";
    }

    public boolean consultaTarjeta(String telefono) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select * from tarjetas where telefono='" + telefono +"'", null);
        if (fila.moveToFirst()) {
           return true;
        }
        bd.close();
        return false;
    }

    public String getMoneyCard(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select * from tarjetas where telefono='" + telefono +"'", null);
        if (fila.moveToFirst()) {
            String saldo=fila.getString(3);
            return saldo;
        }
        bd.close();
        return "";
    }


    public void back_card(View v){

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.flip_animation);
        flFront.startAnimation(animation);
        flBack.startAnimation(animation);

        // Cambiar la visibilidad según el estado actual
        if (isFlipped) {
            flFront.setVisibility(View.VISIBLE);
            flBack.setVisibility(View.INVISIBLE);
            btnMas.setVisibility(View.VISIBLE);
            btnMenos.setVisibility(View.VISIBLE);
        } else {
            flFront.setVisibility(View.INVISIBLE);
            flBack.setVisibility(View.VISIBLE);
            btnMas.setVisibility(View.INVISIBLE);
            btnMenos.setVisibility(View.INVISIBLE);
        }
        isFlipped = !isFlipped;
    }

    private String generarNumRandom() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Generar los primeros 15 dígitos
        for (int i = 0; i < 15; i++) {
            int digit = random.nextInt(10); // Generar un número aleatorio entre 0 y 9
            sb.append(digit);
        }
        // Generar el último dígito asegurándose de que no sea cero
        int lastDigit = random.nextInt(9) + 1; // Generar un número aleatorio entre 1 y 9
        sb.append(lastDigit);

        return sb.toString();
    }
    public static String formatearNumeroTarjeta(String numeroTarjeta) {
        StringBuilder sb = new StringBuilder(numeroTarjeta);

        // Insertar guiones cada cuatro caracteres
        for (int i = 4; i < sb.length(); i += 5) {
            sb.insert(i, "-");
        }

        return sb.toString();
    }


    public void createCard(View v){
        String num_cuenta= tvNumCuentaRefresh.getText().toString();
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = dateFormat.format(new Date());
        ContentValues registro = new ContentValues();
        registro.put("num_cuenta", num_cuenta);
        registro.put("telefono", telefono);
        registro.put("saldo", "0");
        registro.put("fecha_creacion", fecha);
        bd.insert("tarjetas", null, registro);
        bd.close();
        Toast.makeText(this, "¡Tarjeta creada Exitosamente!",Toast.LENGTH_SHORT).show();

        Intent intent1= new Intent(this, InicioActivity.class);
        intent1.putExtra("telefono", telefono );
        startActivity(intent1);
    }

    public void addMoney(View v){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from tarjetas where telefono='" + telefono +"'", null);
        if(fila.moveToFirst()){
            BigDecimal saldoViejo =new BigDecimal(fila.getString(3));
            BigDecimal monto= new BigDecimal(2000);
            BigDecimal saldoNuevo= saldoViejo.add(monto);

            BigDecimal saldoDisponible =new BigDecimal(updateMoneyUser(monto,"-"));

            if(saldoDisponible.compareTo(BigDecimal.ZERO) >= 0 ){  //Saldo en Disponible debe ser mayor o igual a 0

                tvMoneyCard.setText("$ "+String.valueOf(saldoNuevo));

                String saldoNuevoS= saldoNuevo.toString();
                ContentValues registro = new ContentValues();
                registro.put("saldo", saldoNuevoS);
                int cant = bd.update("tarjetas", registro, "telefono=" + telefono, null);

            }

        }
        fila.close();
        bd.close();
    }

    public void subtractMoney(View v){

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from tarjetas where telefono='" + telefono +"'", null);

        if(fila.moveToFirst()){
            BigDecimal saldoViejo =new BigDecimal(fila.getString(3));
            BigDecimal monto= new BigDecimal(2000);
            BigDecimal saldoNuevo= saldoViejo.subtract(monto);

                if(saldoNuevo.compareTo(BigDecimal.ZERO) >= 0){
                    tvMoneyCard.setText("$ "+String.valueOf(saldoNuevo));
                    control=true;
                    String saldoNuevoS= saldoNuevo.toString();
                    ContentValues registro = new ContentValues();
                    registro.put("saldo", saldoNuevoS);
                    int cant = bd.update("tarjetas", registro, "telefono=" + telefono, null);
                    String dispo = updateMoneyUser(monto,"+");
                }else{
                    Toast.makeText(this,"No puede seguir retirando!",Toast.LENGTH_LONG).show();
                }
        }
        fila.close();
        bd.close();
    }


    public String updateMoneyUser(BigDecimal money,String operator){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from usuarios where telefono='" + telefono +"'", null);

        if (fila.moveToFirst()){
            BigDecimal moneyAvailable = new BigDecimal(fila.getString(5));
            BigDecimal moneyCurrentAvailable;
            if(operator.equals("-")){
                moneyCurrentAvailable = moneyAvailable.subtract(money);
            }else{
                moneyCurrentAvailable = moneyAvailable.add(money);
            }

            if (moneyCurrentAvailable.compareTo(BigDecimal.ZERO) > 0){

                String moneyCurrent= moneyCurrentAvailable.toString();
                ContentValues registerUser = new ContentValues();
                registerUser.put("saldo", moneyCurrent);
                int cantU = bd.update("usuarios", registerUser, "telefono=" + telefono, null);
                fila.close();
                bd.close();
                return moneyCurrent;
            }else  if (moneyCurrentAvailable.compareTo(BigDecimal.ZERO) == 0){
                String moneyCurrent= moneyCurrentAvailable.toString();
                ContentValues registerUser = new ContentValues();
                registerUser.put("saldo", moneyCurrent);
                int cantU = bd.update("usuarios", registerUser, "telefono=" + telefono, null);
                fila.close();
                bd.close();
                return moneyCurrent;
            }else{
                Toast.makeText(this, "No cuenta con fondos necesarios", Toast.LENGTH_SHORT).show();
                return "-1";
            }
        }
        fila.close();
        bd.close();
        return "0";
    }


    public void onBackPressed() {
        Intent intent1=new Intent(this, InicioActivity.class);
        intent1.putExtra("telefono", telefono);
        startActivity(intent1);
    }


    public void back(View v){
        Intent intent1=new Intent(this, InicioActivity.class);
        intent1.putExtra("telefono", telefono);
        startActivity(intent1);
    }

    public void promos(View v){
        Intent i=new Intent(this,PromoBeneficioActivity.class);
        startActivity(i);
    }
}