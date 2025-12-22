package com.ldm.spaceDefenders.juego;

import com.ldm.spaceDefenders.Graficos;
import com.ldm.spaceDefenders.Input.TouchEvent;
import com.ldm.spaceDefenders.Juego;
import com.ldm.spaceDefenders.Pantalla;

import java.util.List;

public class PantallaAyuda4 extends Pantalla {
    public PantallaAyuda4(Juego juego) {
        super(juego);
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = juego.getInput().getTouchEvents();
        juego.getInput().getKeyEvents();

        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_UP) {
                if(event.x > 256 && event.y > 416 ) {
                    juego.setScreen(new MainMenuScreen(juego));
                    if(Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    return;
                }
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Graficos g = juego.getGraphics();
        g.drawPixmap(Assets.fondo, 0, 0);
        g.drawPixmap(Assets.ayuda4, 15, 0);
        g.drawPixmap(Assets.botones, 252, 420, 5, 128, 66, 66);
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