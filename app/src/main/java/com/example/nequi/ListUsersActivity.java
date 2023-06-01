package com.example.nequi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListUsersActivity extends AppCompatActivity {

    private ArrayList<String> telefonos,nombres;
    private ContactAdapter adaptador1;
    private ListView lv1;
    private String tipo,datoTelefono,cuanto,mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        fillDateList();

        Bundle bundle = getIntent().getExtras();
        tipo=bundle.getString("type");
        cuanto=bundle.getString("numCuanto");
        mensaje=bundle.getString("mensaje");

    }

    public void fillDateList() {
        telefonos = new ArrayList<>();
        nombres= new ArrayList<>();
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "bd_nequi", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from usuarios", null);
        if (fila.moveToFirst()) {
            do {
                String nombre = fila.getString(1);
                String telefono = fila.getString(3);
                telefonos.add(telefono);
                nombres.add(nombre);



            } while (fila.moveToNext());
        }
        bd.close();

        adaptador1 = new ContactAdapter(this, R.layout.list_item_contact, telefonos, nombres);
        lv1 = findViewById(R.id.list1);
        lv1.setAdapter(adaptador1);
    }

    private class ContactAdapter extends ArrayAdapter<String> {

        private int layoutResourceId;

        public ContactAdapter(Context context, int layoutResourceId, ArrayList<String> telefonos, ArrayList<String> nombres) {
            super(context, layoutResourceId,  nombres);
            this.layoutResourceId = layoutResourceId;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = getLayoutInflater();
                view = inflater.inflate(layoutResourceId, null);
            }

            // Obtener el nombre y el teléfono del contacto actual
            String nombre= nombres.get(position);
            String telefono = telefonos.get(position);

            // Establecer el nombre y el teléfono en los TextView correspondientes
            TextView nameTextView = view.findViewById(R.id.nombre);
            nameTextView.setText(nombre);
            TextView phoneTextView = view.findViewById(R.id.telefono);
            phoneTextView.setText("Tel: " + telefono);

            //Parte para disponer de los caracteres para el usuarios
            String[] palabrasSeparadas = nombre.split(" ");
            String inicial = "";
            if (palabrasSeparadas.length >= 1) {
                String palabra1 = palabrasSeparadas[0];
                char primerCaracterPalabra1 = palabra1.charAt(0);
                inicial = String.valueOf(primerCaracterPalabra1);
            }
            TextView ini = view.findViewById(R.id.tvInicial);
            ini.setText(inicial);

            LinearLayout llContacto = view.findViewById(R.id.llContacto);
            llContacto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enviarContacto(nombre,telefono,tipo);
                }
            });


            return view;
        }
    }


    private void enviarContacto(String nombre,String telefono,String tipo) {
        Intent intent = new Intent(this, EnviaPlataActivity.class);
        intent.putExtra("nombre_contacto", nombre);
        intent.putExtra("telefono", telefono);
        intent.putExtra("type",tipo);
        intent.putExtra("numCuanto",cuanto);
        intent.putExtra("mensaje",mensaje);
        startActivity(intent);
    }

    public void back(View v){
        finish();
    }
}