package com.ldm.lluviaDeChuches.juego;

import java.util.List;

import com.ldm.lluviaDeChuches.Juego;
import com.ldm.lluviaDeChuches.Graficos;
import com.ldm.lluviaDeChuches.Input.TouchEvent;
import com.ldm.lluviaDeChuches.Pantalla;


public class PantallaAyuda3 extends Pantalla {
    public PantallaAyuda3(Juego juego) {
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
                    juego.setScreen(new PantallaAyuda4(juego));
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
        g.drawPixmap(Assets.ayuda3, 20, 7);
        g.drawPixmap(Assets.botones, 252, 418, 5, 65, 66, 66);
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
