package com.ldm.lluviaDeChuches.juego;

import java.util.List;
import android.graphics.Color;
import com.ldm.lluviaDeChuches.Juego;
import com.ldm.lluviaDeChuches.Graficos;
import com.ldm.lluviaDeChuches.Input.TouchEvent;
import com.ldm.lluviaDeChuches.Pixmap;
import com.ldm.lluviaDeChuches.Pantalla;

public class PantallaJuego extends Pantalla {
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
            if (event.type == TouchEvent.TOUCH_UP) {
                // Botón de pausa
                if (event.x < 64 && event.y < 64) {
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    estado = EstadoJuego.Pausado;
                    return;
                }

                // Disparar a objetivos (solo si no es el botón de pausa)
                if (event.y >= 64) {
                    mundo.dispararAObjetivo(event.x, event.y);
                }
            }
        }

        mundo.update(deltaTime);

        if (mundo.finalJuego) {
            if (Configuraciones.sonidoHabilitado)
                Assets.perder.play(1);
            estado = EstadoJuego.FinJuego;
        }

        if (antiguaPuntuacion != mundo.puntuacion) {
            antiguaPuntuacion = mundo.puntuacion;
            puntuacion = String.valueOf(antiguaPuntuacion);
        }
    }

    private void updatePaused(List<TouchEvent> touchEvents) {
        for (TouchEvent event : touchEvents) {
            if (event.type == TouchEvent.TOUCH_UP) {
                if (event.x > 55 && event.x <= 260 && event.y > 115 && event.y <= 150) {
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    estado = EstadoJuego.Ejecutandose;
                    return;
                }

                if (event.x > 100 && event.x <= 235 && event.y > 190 && event.y <= 220) {
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    juego.setScreen(new MainMenuScreen(juego));
                    return;
                }
            }
        }
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

    @Override
    public void present(float deltaTime) {
        Graficos g = juego.getGraphics();

        if (g != null) {
            g.drawPixmap(Assets.fondo, 0, 0);
            drawWorld(mundo);

            if (estado == EstadoJuego.Preparado)
                drawReadyUI();
            else if (estado == EstadoJuego.Ejecutandose)
                drawRunningUI();
            else if (estado == EstadoJuego.Pausado)
                drawPausedUI();
            else if (estado == EstadoJuego.FinJuego)
                drawGameOverUI();

            drawText(g, puntuacion, g.getWidth() / 2 - puntuacion.length() * 20 / 2, g.getHeight() - 42);
        }
    }

    private void drawWorld(Mundo mundo) {
        Graficos g = juego.getGraphics();

        // Dibujar los objetivos en posiciones fijas
        for (int i = 0; i < mundo.objetivos.size(); i++) {
            Objetivo objetivo = mundo.objetivos.get(i);
            Pixmap objetivoPixmap = Assets.obtenerPixmapObjetivo(objetivo.tipo);

            int objetivoX = objetivo.x * 32;
            int objetivoY = 80 + (i * 50);

            g.drawPixmap(objetivoPixmap, objetivoX, objetivoY);

            // Dibujar barra de tiempo restante
            float tiempoRestante = mundo.getTiempoRestanteObjetivo(i);
            float porcentaje = tiempoRestante / 3.0f; // 3 segundos máximo
            int anchoBarra = (int)(32 * porcentaje);

            int colorBarra;
            if (porcentaje > 0.6f) {
                colorBarra = Color.GREEN;
            } else if (porcentaje > 0.3f) {
                colorBarra = Color.YELLOW;
            } else {
                colorBarra = Color.RED;
            }

            g.drawRect(objetivoX, objetivoY - 5, anchoBarra, 3, colorBarra);
        }

        // Mostrar fallos restantes
        String fallosTexto = "Fallos: " + mundo.getObjetivosFallados() + "/" + mundo.getMaxFallos();
        g.drawText(fallosTexto, 10, 50, Color.WHITE, 16, false);

        g.drawLine(0, 416, 480, 416, Color.rgb(10, 10, 80));
    }

    private void drawReadyUI() {
        Graficos g = juego.getGraphics();
        Pixmap preparadoPixmap = modoExtremo ? Assets.preparadoE : Assets.preparadoN;
        g.drawPixmap(preparadoPixmap, 10, 40);
    }

    private void drawRunningUI() {
        Graficos g = juego.getGraphics();
        g.drawPixmap(Assets.botones, 0, 0, 68, 128, 64, 64); // Botón de pausa
    }

    private void drawPausedUI() {
        Graficos g = juego.getGraphics();
        g.drawPixmap(Assets.menupausa, 60, 120);
        g.drawLine(0, 416, 480, 416, Color.BLACK);
    }

    private void drawGameOverUI() {
        Graficos g = juego.getGraphics();
        g.drawPixmap(Assets.finjuego, 36, 100);
        g.drawPixmap(Assets.botones, 128, 200, 5, 128, 66, 66);
        g.drawLine(0, 416, 480, 416, Color.BLACK);
    }

    public void drawText(Graficos g, String line, int x, int y) {
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);

            int srcX;
            int srcWidth;

            if (character == '.') {
                srcX = 327;
                srcWidth = 15;
            } else {
                srcX = (character - '0') * 32;
                srcWidth = 32;
            }

            g.drawPixmap(Assets.numeros, x, y, srcX, 0, srcWidth, 32);
            x += srcWidth;
        }
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