package com.example.nequi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper{
    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table usuarios(id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,nombre text,correo text,telefono text,pin text,saldo text,fecha_creacion text)");

        db.execSQL("create table transacciones(id_transaccion INTEGER PRIMARY KEY AUTOINCREMENT,num_origen text, num_destino text,monto text,metodo_envio text, mensaje text, fecha_creacion text)");

        db.execSQL("create table tarjetas(id_tarjeta INTEGER PRIMARY KEY AUTOINCREMENT,num_cuenta text,telefono text,saldo text, fecha_creacion text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
