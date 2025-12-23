package com.ldm.spaceDefenders.juego;

import java.util.List;

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

        int toolbarHeight = 40;
        int toolbarColor = 0xFF001F3F; // Azul marino

        g.drawRect(0, 0, g.getWidth(), toolbarHeight, toolbarColor);

        String tituloToolbar = "SPACE DEFENDERS";
        int textColor = 0xFFFFFFFF;
        int textSize = 20;
        int textY = toolbarHeight / 2 + textSize / 2 - 5;

        g.drawText(tituloToolbar, g.getWidth() / 2, textY, textColor, textSize, true);

        g.drawPixmap(Assets.logo, 57, 40);
        g.drawPixmap(Assets.menu, 50, 280);

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