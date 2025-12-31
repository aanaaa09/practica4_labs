package com.ldm.spaceDefenders.juego;

import com.ldm.spaceDefenders.Juego;
import com.ldm.spaceDefenders.Graficos;
import com.ldm.spaceDefenders.Pantalla;
import com.ldm.spaceDefenders.Graficos.PixmapFormat;

public class LoadingScreen extends Pantalla{
    public LoadingScreen(Juego juego) {
        super(juego);
    }

    @Override
    public void update(float deltaTime) {
        Graficos g = juego.getGraphics();
        Assets.fondo = g.newPixmap("fondo.png", PixmapFormat.RGB565);
        Assets.logo = g.newPixmap("logo.png", PixmapFormat.ARGB4444);
        Assets.menu = g.newPixmap("menuu.png", PixmapFormat.ARGB4444);
        Assets.botones = g.newPixmap("botones.png", PixmapFormat.ARGB4444);
        Assets.ayuda1 = g.newPixmap("A1.png", PixmapFormat.ARGB4444);
        Assets.ayuda2 = g.newPixmap("A2.png", PixmapFormat.ARGB4444);
        Assets.ayuda3 = g.newPixmap("A3.png", PixmapFormat.ARGB4444);
        Assets.ayuda4 = g.newPixmap("leyendaa.png", PixmapFormat.ARGB4444);
        Assets.preparadoN = g.newPixmap("preparadoNormal.png", PixmapFormat.ARGB4444);
        Assets.preparadoE = g.newPixmap("preparadoExtremo.png", PixmapFormat.ARGB4444);
        Assets.menupausa = g.newPixmap("menu_pausa.png", PixmapFormat.ARGB4444);
        Assets.finjuego = g.newPixmap("fin_juego.png", PixmapFormat.ARGB4444);

       //objetivos
        Assets.objetivoEspecial = g.newPixmap("objetivoEspecial.png", PixmapFormat.ARGB4444);
        Assets.objetivo1 = g.newPixmap("objetivo1.png", PixmapFormat.ARGB4444);
        Assets.objetivo2 = g.newPixmap("objetivo2.png", PixmapFormat.ARGB4444);
        Assets.objetivo3 = g.newPixmap("objetivo3.png", PixmapFormat.ARGB4444);

        // Sonidos con nombres genéricos
        Assets.clic = juego.getAudio().nuevoSonido("clic.ogg");
        Assets.disparo = juego.getAudio().nuevoSonido("disparo.ogg");
        Assets.error = juego.getAudio().nuevoSonido("ains.ogg");


        Configuraciones.cargar(juego.getFileIO());
        juego.setScreen(new PantallaLoginRegistro(juego));
    }

    @Override
    public void present(float deltaTime) {

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