package com.ldm.lluviaDeChuches.juego;

import java.util.List;

import com.ldm.lluviaDeChuches.Juego;
import com.ldm.lluviaDeChuches.Graficos;
import com.ldm.lluviaDeChuches.Input.TouchEvent;
import com.ldm.lluviaDeChuches.Pantalla;

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
                // Juego normal
                if (inBounds(event, 50 + 16, 270, 205, 31)) {
                    juego.setScreen(new PantallaJuego(juego, false));
                    if (Configuraciones.sonidoHabilitado) {
                        Assets.clic.play(1);
                    }

                    return;
                }

                // Juego extremo
                if (inBounds(event, 50, 280 + 25, 222, 23)) { // Ajusta las coordenadas según el diseño
                    juego.setScreen(new PantallaJuego(juego, true)); // Modo extremo
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    return;
                }

                if (inBounds(event, 50 + 7, 280 + 50, 210, 21)) {
                    juego.setScreen(new PantallaMaximasPuntuaciones(juego));
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    return;
                }
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

        // Dibujar el fondo primero
        g.drawPixmap(Assets.fondo, 0, 0);

        // Dibujar Toolbar encima del fondo
        int toolbarHeight = 40;
        int toolbarColor = 0xFFD2B48C; // Marrón oscuro visible (formato ARGB)

        // Dibujar rectángulo del toolbar
        g.drawRect(0, 0, g.getWidth(), toolbarHeight, toolbarColor);


        // Dibujar texto del toolbar
        String tituloToolbar = "Lluvia de CHUCHES";
        int textColor = 0xFFFFFFFF; // Blanco
        int textSize = 20;          // Tamaño del texto
        int textY = toolbarHeight / 2 + textSize / 2 - 5; // Centrar verticalmente

        g.drawText(tituloToolbar, g.getWidth() / 2, textY, textColor, textSize, true);

        // Dibujar el resto del menú
        g.drawPixmap(Assets.logo, 57, 40);
        g.drawPixmap(Assets.menu, 50, 280);

        // Dibujar botón de sonido
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