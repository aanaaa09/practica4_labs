package com.ldm.lluviaDeChuches.juego;
import java.util.List;
import com.ldm.lluviaDeChuches.Juego;
import com.ldm.lluviaDeChuches.Graficos;
import com.ldm.lluviaDeChuches.Input.TouchEvent;
import com.ldm.lluviaDeChuches.Pantalla;


public class PantallaMaximasPuntuaciones extends Pantalla {
    String[] lineas = new String[5];

    public PantallaMaximasPuntuaciones(Juego juego) {
        super(juego);

        for (int i = 0; i < 5; i++) {
            lineas[i] = (i + 1) + ". " + Configuraciones.maxPuntuaciones[i];
        }
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = juego.getInput().getTouchEvents();
        juego.getInput().getKeyEvents();

        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (event.x < 64 && event.y > 416) {
                    if(Configuraciones.sonidoHabilitado)
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
        g.drawPixmap(Assets.menu, 50, 20, 0, 60, 215, 25);

        int y = 100;
        for (int i = 0; i < 5; i++) {
            dibujarTexto(g, lineas[i], 20, y);
            y += 50;
        }

        g.drawPixmap(Assets.botones, 5, 417, 68, 64, 80, 70);
    }

    public void dibujarTexto(Graficos g, String linea, int x, int y) {
        int len = linea.length();
        for (int i = 0; i < len; i++) {
            char character = linea.charAt(i);

            if (character == ' ') {
                x += 30;
                continue;
            }

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

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}

