package com.example.nequi;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PinAccessActivity extends AppCompatActivity {

    private TextView tvPin1, tvPin2, tvPin3, tvPin4,tvf;
    private String telefono;
    private Button btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,btn0;
    private StringBuilder pinBuilder;
    private int contador=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_access);

        tvf=(TextView) findViewById(R.id.tvf);
        tvPin1=(TextView) findViewById(R.id.tvPin1);
        tvPin2=(TextView) findViewById(R.id.tvPin2);
        tvPin3=(TextView) findViewById(R.id.tvPin3);
        tvPin4=(TextView) findViewById(R.id.tvPin4);
        btn0=(Button) findViewById(R.id.btn0);
        btn1=(Button) findViewById(R.id.btn1);
        btn2=(Button) findViewById(R.id.btn2);
        btn3=(Button) findViewById(R.id.btn3);
        btn4=(Button) findViewById(R.id.btn4);
        btn5=(Button) findViewById(R.id.btn5);
        btn6=(Button) findViewById(R.id.btn6);
        btn7=(Button) findViewById(R.id.btn7);
        btn8=(Button) findViewById(R.id.btn8);
        btn9=(Button) findViewById(R.id.btn9);

        // Inicializar el StringBuilder para construir el PIN
        pinBuilder = new StringBuilder();

        Bundle bundle = getIntent().getExtras();
        telefono=bundle.getString("telefono");
    }

    public void verificarPin(String pin) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select * from usuarios where pin='"+pin+"' and telefono='"+telefono+"'", null);
        if (fila.moveToFirst()) {
            SessionManager sessionManager = new SessionManager(getApplicationContext());
            sessionManager.saveSession(telefono);

            Intent intent1= new Intent(this, InicioActivity.class);
            intent1.putExtra("telefono", telefono);
            startActivity(intent1);
        } else {
            Toast.makeText(this, "Pin incorrecto!!",
                    Toast.LENGTH_SHORT).show();
            reiniciarPIN();
        }
        bd.close();

    }


    public void onClick(View view) {
        contador+=1;
        Button button = (Button) view;
        String buttonText = button.getText().toString();
        pinBuilder.append(buttonText);

        if(contador==1){
            tvPin1.setText("*");
        }else if(contador==2){
            tvPin2.setText("*");
        }else if(contador==3){
            tvPin3.setText("*");
        }else{
            tvPin4.setText("*");
        }

        if (pinBuilder.length() == 4) {

            verificarPin(pinBuilder.toString());

        }
    }

    public void reiniciarPIN(){
        contador=0;
        pinBuilder.setLength(0);
        tvPin1.setText("");
        tvPin2.setText("");
        tvPin3.setText("");
        tvPin4.setText("");
    }

    public void onClickBack(View v){

        if (pinBuilder.length() == 3) {
            pinBuilder.deleteCharAt(2);
            tvPin3.setText("");
            contador-=1;

        }else if(pinBuilder.length() == 2) {
            pinBuilder.deleteCharAt(1);
            tvPin2.setText("");
            contador-=1;

        }else if(pinBuilder.length() == 1) {
            pinBuilder.deleteCharAt(0);
            tvPin1.setText("");
            contador-=1;

        }


    }

}