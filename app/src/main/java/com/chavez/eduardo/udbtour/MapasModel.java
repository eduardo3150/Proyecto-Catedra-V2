package com.chavez.eduardo.udbtour;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Eduardo_Chavez on 12/5/2017.
 */

public class MapasModel {
    private MapDBHelper mapDBHelper;
    private SQLiteDatabase db;

    private static String TABLE_NAME = "mapas";

    public MapasModel(Context context) {
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
        Cursor fila = db.rawQuery("select * from mapas", null);
        return fila;

    }


    public void actualizar(int id, String nombre, String descripcion, double latitud, double longitud, String imagen, String thumbnail, String categoria) {
        String consulta;
        consulta = "UPDATE mapas SET nombre='" + nombre + "',descripcion='" + descripcion +
                "',latitud='" + latitud + "',longitud='" + longitud + "',imagen='" + imagen +
                "',thumbnail='" + thumbnail + "',categoria='" + categoria + "' where id=" + id + ";";

        db.execSQL(consulta);

    }

    public Cursor mostrarPorID(int id) {

        String consulta;
        String[] respuesta = new String[8];
        consulta = "SELECT * FROM mapas where id =" + id + " ";
        Cursor fila = db.rawQuery(consulta, null);

        return fila;
    }

    public void eliminar (int id){
        db.execSQL("delete from mapas where id ='"+ id +"'");
    }

}
