package com.ldm.spaceDefenders.juego;

import java.util.List;

import android.graphics.Color;

import com.ldm.spaceDefenders.Juego;
import com.ldm.spaceDefenders.Graficos;
import com.ldm.spaceDefenders.Input.TouchEvent;
import com.ldm.spaceDefenders.Pantalla;

public class MainMenuScreen extends Pantalla {
    public MainMenuScreen(Juego juego) {
        super(juego);
    }

    @Override
    public void update(float deltaTime) {
        Graficos g = juego.getGraphics();
        List<TouchEvent> touchEvents = juego.getInput().getTouchEvents();
        juego.getInput().getKeyEvents();

        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                // Botón sonido
                if (inBounds(event, 0, g.getHeight() - 64, 64, 64)) {
                    Configuraciones.sonidoHabilitado = !Configuraciones.sonidoHabilitado;
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                }

                // Modo Normal
                if (inBounds(event, 50 + 16, 270, 205, 31)) {
                    juego.setScreen(new PantallaJuego(juego, false));
                    if (Configuraciones.sonidoHabilitado) {
                        Assets.clic.play(1);
                    }
                    return;
                }

                // Modo Extremo
                if (inBounds(event, 50, 280 + 25, 222, 23)) {
                    juego.setScreen(new PantallaJuego(juego, true));
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    return;
                }

                // Puntuaciones
                if (inBounds(event, 50 + 7, 280 + 50, 210, 21)) {
                    juego.setScreen(new PantallaMaximasPuntuaciones(juego));
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    return;
                }

                // Ayuda
                if (inBounds(event, 50 + 48, 280 + 70, 132, 40)) {
                    juego.setScreen(new PantallaAyuda(juego));
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    return;
                }

                // Botón Cerrar Sesión (centrado debajo de ayuda)
                int botonCerrarX = (g.getWidth() - 150) / 2; // Centrado
                int botonCerrarY = 280 + 70 + 50; // Debajo de ayuda
                if (inBounds(event, botonCerrarX, botonCerrarY, 150, 35)) {
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    SesionUsuario.cerrarSesion();
                    juego.setScreen(new PantallaLoginRegistro(juego));
                    return;
                }
            }
        }
    }

    private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
        return event.x > x && event.x < x + width - 1 &&
                event.y > y && event.y < y + height - 1;
    }

    @Override
    public void present(float deltaTime) {
        Graficos g = juego.getGraphics();

        g.drawPixmap(Assets.fondo, 0, 0);

        // Dibujar logo y menú
        g.drawPixmap(Assets.logo, 57, 40);
        g.drawPixmap(Assets.menu, 50, 280);

        // Botón cerrar sesión centrado debajo de ayuda
        if (SesionUsuario.haySesionActiva()) {
            int botonCerrarX = (g.getWidth() - 150) / 2;
            int botonCerrarY = 280 + 70 + 50;

            g.drawRect(botonCerrarX, botonCerrarY, 150, 35, Color.rgb(180, 0, 0));
            g.drawText("Cerrar Sesion", botonCerrarX + 75, botonCerrarY + 22, Color.WHITE, 14, true);
        }

        // Botón de sonido
        if (Configuraciones.sonidoHabilitado)
            g.drawPixmap(Assets.botones, 5, g.getHeight() - 70, 5, 2, 66, 64);
        else
            g.drawPixmap(Assets.botones, 5, g.getHeight() - 70, 68, 2, 66, 64);
    }

    @Override
    public void pause() {
        Configuraciones.save(juego.getFileIO());
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}