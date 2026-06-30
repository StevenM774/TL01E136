package com.ucenm.tl01e136;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteConexion extends SQLiteOpenHelper {

    public static final String NameDatabase = "TL01E136";
    public static final String tablaContactos = "contactos";
    
    // Campos
    public static final String id = "id";
    public static final String pais = "pais";
    public static final String nombre = "nombre";
    public static final String telefono = "telefono";
    public static final String nota = "nota";
    public static final String imagen = "imagen";

    public static final String CreateTableContactos = "CREATE TABLE " + tablaContactos + 
            " (id INTEGER PRIMARY KEY AUTOINCREMENT, pais TEXT, nombre TEXT, telefono INTEGER, nota TEXT, imagen BLOB)";

    public static final String DropTableContactos = "DROP TABLE IF EXISTS " + tablaContactos;

    public SQLiteConexion(Context context, String dbname, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbname, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateTableContactos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DropTableContactos);
        onCreate(db);
    }
}
