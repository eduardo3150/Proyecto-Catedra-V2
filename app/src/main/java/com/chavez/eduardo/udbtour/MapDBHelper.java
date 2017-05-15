package com.chavez.eduardo.udbtour;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Eduardo_Chavez on 12/5/2017.
 */

public class MapDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;

    public static final String DATABASE_NAME = "map.db";

    public MapDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE cacheMapa (" +
                " id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " nombre TEXT," +
                " descripcion TEXT," +
                " latitud REAL," +
                " longitud REAL," +
                " imagen TEXT," +
                " thumbnail TEXT," +
                " categoria TEXT" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE mapas (" +
                " id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " nombre TEXT," +
                " descripcion TEXT," +
                " latitud REAL," +
                " longitud REAL," +
                " imagen TEXT," +
                " thumbnail TEXT," +
                " categoria TEXT" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS mapas");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS cacheMapa");
        onCreate(sqLiteDatabase);
    }

}

