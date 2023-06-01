package com.example.nequi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void registrar(View v){
        Intent intent1= new Intent(this, RegistroActivity.class);
        startActivity(intent1);
    }

    public void inicio(View v){
        Intent intent1= new Intent(this, LoginActivity.class);
        startActivity(intent1);
    }

    @Override
    public void onBackPressed() {

    }
}