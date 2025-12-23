package com.ldm.spaceDefenders.juego;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "spacedefenders.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla usuarios
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_NOMBRE = "nombre";

    // Tabla puntuaciones (historial de partidas)
    public static final String TABLE_PUNTUACIONES = "puntuaciones";
    public static final String COLUMN_PUNT_ID = "id";
    public static final String COLUMN_PUNT_EMAIL = "email_usuario";
    public static final String COLUMN_PUNT_PUNTOS = "puntos";
    public static final String COLUMN_PUNT_MODO = "modo"; // "normal" o "extremo"
    public static final String COLUMN_PUNT_FECHA = "fecha";

    public AdminSQLiteOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla usuarios
        String createUsuarios = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_NOMBRE + " TEXT NOT NULL)";
        db.execSQL(createUsuarios);

        // Crear tabla puntuaciones
        String createPuntuaciones = "CREATE TABLE " + TABLE_PUNTUACIONES + " (" +
                COLUMN_PUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PUNT_EMAIL + " TEXT NOT NULL, " +
                COLUMN_PUNT_PUNTOS + " INTEGER NOT NULL, " +
                COLUMN_PUNT_MODO + " TEXT NOT NULL, " +
                COLUMN_PUNT_FECHA + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COLUMN_PUNT_EMAIL + ") REFERENCES " +
                TABLE_USUARIOS + "(" + COLUMN_EMAIL + "))";
        db.execSQL(createPuntuaciones);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PUNTUACIONES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }
}