package com.ldm.spaceDefenders.juego;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import com.ldm.spaceDefenders.Juego;
import com.ldm.spaceDefenders.Graficos;
import com.ldm.spaceDefenders.Input.TouchEvent;
import com.ldm.spaceDefenders.Pantalla;
import com.ldm.spaceDefenders.androidimpl.AndroidJuego;

public class PantallaMaximasPuntuaciones extends Pantalla {
    private List<String> lineas = new ArrayList<>();
    private AdminSQLiteOpenHelper admin;

    public PantallaMaximasPuntuaciones(Juego juego) {
        super(juego);

        if (juego instanceof AndroidJuego) {
            admin = new AdminSQLiteOpenHelper((AndroidJuego) juego);
        }

        cargarPuntuacionesUsuario();
    }

    private void cargarPuntuacionesUsuario() {
        if (!SesionUsuario.haySesionActiva()) {
            lineas.add("No hay sesion activa");
            return;
        }

        SQLiteDatabase db = admin.getReadableDatabase();

        // Obtener las 5 mejores puntuaciones del usuario
        Cursor cursor = db.query(
                AdminSQLiteOpenHelper.TABLE_PUNTUACIONES,
                new String[]{AdminSQLiteOpenHelper.COLUMN_PUNT_PUNTOS, AdminSQLiteOpenHelper.COLUMN_PUNT_MODO},
                AdminSQLiteOpenHelper.COLUMN_PUNT_EMAIL + "=?",
                new String[]{SesionUsuario.emailActual},
                null, null,
                AdminSQLiteOpenHelper.COLUMN_PUNT_PUNTOS + " DESC",
                "5"
        );

        int posicion = 1;
        if (cursor.moveToFirst()) {
            do {
                int puntos = cursor.getInt(0);
                String modo = cursor.getString(1);
                String modoTexto = modo.equals("extremo") ? "E" : "N";

                lineas.add(posicion + ". " + puntos + " pts (" + modoTexto + ")");
                posicion++;
            } while (cursor.moveToNext());
        } else {
            lineas.add("Sin partidas jugadas");
        }

        cursor.close();
        db.close();
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = juego.getInput().getTouchEvents();

        for (TouchEvent event : touchEvents) {
            if (event.type == TouchEvent.TOUCH_UP) {
                if (event.x < 64 && event.y > 416) {
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    juego.setScreen(new MainMenuScreen(juego));
                    return;
                }
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Graficos g = juego.getGraphics();

        g.drawPixmap(Assets.fondo, 0, 0);

        // Título
        g.drawText("MIS MEJORES PUNTUACIONES", g.getWidth() / 2, 60, Color.WHITE, 20, true);
        g.drawText("Jugador: " + SesionUsuario.nombreActual, g.getWidth() / 2, 90, Color.YELLOW, 16, true);

        // Dibujar puntuaciones
        int y = 140;
        for (String linea : lineas) {
            g.drawText(linea, 50, y, Color.WHITE, 18, false);
            y += 45;
        }

        // Botón volver
        g.drawPixmap(Assets.botones, 5, 417, 68, 64, 80, 70);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {}
}