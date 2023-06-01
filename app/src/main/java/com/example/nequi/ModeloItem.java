package com.example.nequi;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ModeloItem {
    private String celOrigen,celDestino,monto,metodo_envio,mensaje,fecha_creacion,celLogeado,nomOri,nomDes;

    public  ModeloItem(String celOrigen, String celDestino, String monto, String metodo_envio, String mensaje, String fecha_creacion,String celLogeado,String nomOri,String nomDes) {
        this.celOrigen= celOrigen;
        this.celDestino = celDestino;
        this.monto = monto;
        this.metodo_envio = metodo_envio;
        this.mensaje = mensaje;
        this.fecha_creacion= fecha_creacion;
        this.celLogeado=celLogeado;
        this.nomOri=nomOri;
        this.nomDes=nomDes;
    }

    public String getTexto1() {
        return celOrigen;
    }

    public String getTexto2() {
        return celDestino;
    }

    public String getTexto3() {
        return monto;
    }

    public String getTexto4() {
        return metodo_envio;
    }

    public String getTexto5() {
        return mensaje;
    }

    public String getTexto6() {
        return fecha_creacion;
    }

    public String getTexto7() {  return celLogeado;  }

    public String getTexto8() {
        return nomOri;
    }

    public String getTexto9() {  return nomDes;  }


}