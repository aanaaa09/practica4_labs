package com.ldm.spaceDefenders.juego;

import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import com.ldm.spaceDefenders.Juego;
import com.ldm.spaceDefenders.Graficos;
import com.ldm.spaceDefenders.Input.TouchEvent;
import com.ldm.spaceDefenders.Pixmap;
import com.ldm.spaceDefenders.Pantalla;
import com.ldm.spaceDefenders.androidimpl.AndroidJuego;

public class PantallaJuego extends Pantalla {
    private static final int HUD_HEIGHT = 35;

    enum EstadoJuego {
        Preparado,
        Ejecutandose,
        Pausado,
        FinJuego
    }

    EstadoJuego estado = EstadoJuego.Preparado;
    Mundo mundo;
    int antiguaPuntuacion = 0;
    String puntuacion = "0";
    private boolean modoExtremo;
    private boolean puntuacionGuardada = false;

    // Variables para la mira
    private int miraX = -1;
    private int miraY = -1;
    private boolean mostrarMira = false;

    public PantallaJuego(Juego juego, boolean modoExtremo) {
        super(juego);
        this.modoExtremo = modoExtremo;
        mundo = new Mundo(modoExtremo);
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = juego.getInput().getTouchEvents();
        juego.getInput().getKeyEvents();

        if (estado == EstadoJuego.Preparado)
            updateReady(touchEvents);
        else if (estado == EstadoJuego.Ejecutandose)
            updateRunning(touchEvents, deltaTime);
        else if (estado == EstadoJuego.Pausado)
            updatePaused(touchEvents);
        else if (estado == EstadoJuego.FinJuego)
            updateGameOver(touchEvents);
    }

    private void updateReady(List<TouchEvent> touchEvents) {
        if (!touchEvents.isEmpty()) {
            estado = EstadoJuego.Ejecutandose;
        }
    }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {
        for (TouchEvent event : touchEvents) {
            if (event.type == TouchEvent.TOUCH_DOWN || event.type == TouchEvent.TOUCH_DRAGGED) {
                if (event.y >= 64) {
                    mostrarMira = true;
                    miraX = event.x;
                    miraY = event.y;
                }
            }

            if (event.type == TouchEvent.TOUCH_UP) {
                if (event.x < 64 && event.y < 64) {
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    estado = EstadoJuego.Pausado;
                    mostrarMira = false;
                    return;
                }

                if (event.y >= 64) {
                    mundo.dispararAObjetivo(event.x, event.y);
                }

                mostrarMira = false;
            }
        }

        mundo.update(deltaTime);

        if (mundo.finalJuego) {
            if (Configuraciones.sonidoHabilitado)
                Assets.error.play(1);
            estado = EstadoJuego.FinJuego;
            guardarPuntuacion();
        }

        if (antiguaPuntuacion != mundo.puntuacion) {
            antiguaPuntuacion = mundo.puntuacion;
            puntuacion = String.valueOf(antiguaPuntuacion);
        }
    }

    private void updatePaused(List<TouchEvent> touchEvents) {
        for (TouchEvent event : touchEvents) {
            if (event.type == TouchEvent.TOUCH_UP) {
                // Calcular posición centrada del menú de pausa
                int menuX = (juego.getGraphics().getWidth() - Assets.menupausa.getWidth()) / 2;
                int menuY = (juego.getGraphics().getHeight() - Assets.menupausa.getHeight()) / 2;

                // Botón Reanudar
                if (inBounds(event, menuX + 55, menuY + 35, 205, 31)) {
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    estado = EstadoJuego.Ejecutandose;
                    return;
                }

                // Botón Menú Principal
                if (inBounds(event, menuX + 50, menuY + 90, 222, 23)) {
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    juego.setScreen(new MainMenuScreen(juego));
                    return;
                }
            }
        }
    }

    private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
        return event.x >= x && event.x <= x + width &&
                event.y >= y && event.y <= y + height;
    }

    private void updateGameOver(List<TouchEvent> touchEvents) {
        for (TouchEvent event : touchEvents) {
            if (event.type == TouchEvent.TOUCH_UP) {
                if (event.x >= 128 && event.x <= 192 &&
                        event.y >= 200 && event.y <= 264) {
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    juego.setScreen(new MainMenuScreen(juego));
                    return;
                }
            }
        }
    }

    private void guardarPuntuacion() {
        if (puntuacionGuardada) return;

        if (!SesionUsuario.haySesionActiva()) return;

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper((AndroidJuego) juego);
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(AdminSQLiteOpenHelper.COLUMN_PUNT_EMAIL, SesionUsuario.emailActual);
        valores.put(AdminSQLiteOpenHelper.COLUMN_PUNT_PUNTOS, mundo.puntuacion);
        valores.put(AdminSQLiteOpenHelper.COLUMN_PUNT_MODO, modoExtremo ? "extremo" : "normal");

        long resultado = db.insert(AdminSQLiteOpenHelper.TABLE_PUNTUACIONES, null, valores);
        db.close();

        if (resultado != -1) {
            puntuacionGuardada = true;
            System.out.println("Puntuación guardada correctamente: " + mundo.puntuacion);
        } else {
            System.err.println("Error al guardar puntuación");
        }
    }

    @Override
    public void present(float deltaTime) {
        Graficos g = juego.getGraphics();

        if (g != null) {
            g.drawPixmap(Assets.fondo, 0, 0);
            drawWorld(mundo);

            if (mostrarMira && estado == EstadoJuego.Ejecutandose) {
                dibujarMira(g, miraX, miraY);
            }

            if (estado == EstadoJuego.Preparado)
                drawReadyUI();
            else if (estado == EstadoJuego.Ejecutandose)
                drawRunningUI();
            else if (estado == EstadoJuego.Pausado)
                drawPausedUI();
            else if (estado == EstadoJuego.FinJuego)
                drawGameOverUI();

            // Puntuación en la esquina superior derecha CON TEXTO NORMAL
            drawPuntuacionTexto(g, puntuacion);
        }
    }

    // NUEVO MÉTODO: Dibuja la puntuación con texto normal en lugar de sprites
    private void drawPuntuacionTexto(Graficos g, String puntos) {
        // Solo el número, sin "Puntos:"

        // Sombra para dar efecto de profundidad
        g.drawText(puntos, g.getWidth() - 15, 30, Color.argb(150, 0, 0, 0), 24, true);

        // Texto principal en color dorado brillante
        g.drawText(puntos, g.getWidth() - 17, 28, Color.rgb(255, 215, 0), 24, true);
    }

    private void drawWorld(Mundo mundo) {
        Graficos g = juego.getGraphics();

        for (Objetivo objetivo : mundo.objetivos) {
            Pixmap objetivoPixmap = Assets.obtenerPixmapObjetivo(objetivo.tipo);

            int objetivoX = objetivo.x * 32;
            int objetivoY = objetivo.y;

            if (objetivoY >= 64 && objetivoY < 416) {
                g.drawPixmap(objetivoPixmap, objetivoX, objetivoY);

                float porcentaje = mundo.getPorcentajeCaida(objetivo);

                if (porcentaje > 0.7f) {
                    int alpha = (int)((porcentaje - 0.7f) / 0.3f * 150);
                    int colorAdvertencia = Color.argb(alpha, 255, 0, 0);
                    g.drawRect(objetivoX - 2, objetivoY - 2, 36, 36, colorAdvertencia);
                }
            }
        }

        int hudBottom = g.getHeight() - 10;
        String fallosTexto = "Fallos: " + mundo.getObjetivosFallados() + "/" + mundo.getMaxFallos();
        g.drawText(fallosTexto, 10, hudBottom - 5, Color.WHITE, 18, false);
    }

    private void dibujarMira(Graficos g, int x, int y) {
        int tamañoMira = 20;
        int colorMira = Color.argb(200, 255, 50, 50);

        // Cruz de la mira
        g.drawLine(x - tamañoMira, y, x - 5, y, colorMira);
        g.drawLine(x + 5, y, x + tamañoMira, y, colorMira);
        g.drawLine(x, y - tamañoMira, x, y - 5, colorMira);
        g.drawLine(x, y + 5, x, y + tamañoMira, colorMira);

        // Círculo exterior
        int radio = 15;
        for (int angulo = 0; angulo < 360; angulo += 5) {
            double rad = Math.toRadians(angulo);
            int x1 = x + (int)(radio * Math.cos(rad));
            int y1 = y + (int)(radio * Math.sin(rad));

            double rad2 = Math.toRadians(angulo + 5);
            int x2 = x + (int)(radio * Math.cos(rad2));
            int y2 = y + (int)(radio * Math.sin(rad2));

            g.drawLine(x1, y1, x2, y2, colorMira);
        }

        g.drawRect(x - 2, y - 2, 4, 4, Color.RED);
    }

    private void drawReadyUI() {
        Graficos g = juego.getGraphics();
        Pixmap preparadoPixmap = modoExtremo ? Assets.preparadoE : Assets.preparadoN;
        g.drawPixmap(preparadoPixmap, 10, 40);
    }

    private void drawRunningUI() {
        Graficos g = juego.getGraphics();
        g.drawPixmap(Assets.botones, 0, 0, 68, 128, 64, 64);
    }

    private void drawPausedUI() {
        Graficos g = juego.getGraphics();

        // Centrar el asset del menú de pausa
        int x = (g.getWidth() - Assets.menupausa.getWidth()) / 2;
        int y = (g.getHeight() - Assets.menupausa.getHeight()) / 2;

        g.drawPixmap(Assets.menupausa, x, y);
    }

    private void drawGameOverUI() {
        Graficos g = juego.getGraphics();
        g.drawPixmap(Assets.finjuego, 36, 100);
        g.drawPixmap(Assets.botones, 128, 200, 5, 128, 66, 66);
    }

    @Override
    public void pause() {
        if (estado == EstadoJuego.Ejecutandose) {
            estado = EstadoJuego.Pausado;
        }
    }

    @Override
    public void resume() {}

    @Override
    public void dispose() {}
}