package com.chavez.eduardo.udbtour;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class CacheMapasModel {
    private MapDBHelper mapDBHelper;
    private SQLiteDatabase db;

    private static String TABLE_NAME = "cacheMapa";

    public CacheMapasModel(Context context) {
        mapDBHelper = new MapDBHelper(context);
        db = mapDBHelper.getWritableDatabase();
    }

    public void insertar( String nombre, String descripcion, double latitud,
                         double longitud, String imagen, String thumbnail, String categoria) {
        String query = "INSERT INTO " + TABLE_NAME +
                " (nombre, descripcion, latitud, longitud, imagen, thumbnail, categoria) VALUES ('"+nombre+"','"+descripcion+"','"+latitud+"','"+
                longitud+"','"+imagen+"','"+thumbnail+"','"+categoria+"');";
        db.execSQL(query);

    }

    public Cursor mostrarTodo() {
        Cursor fila = db.rawQuery("select * from "+ TABLE_NAME, null);
        return fila;

    }

    public int mostrarTamanio() {
        Cursor fila = db.rawQuery("select * from "+ TABLE_NAME, null);
        return fila.getCount();

    }


    public void eliminarTodo (){
        db.execSQL("delete from cacheMapa");
    }

}
