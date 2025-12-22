package com.ldm.lluviaDeChuches.juego;

import com.ldm.lluviaDeChuches.Juego;
import com.ldm.lluviaDeChuches.Graficos;
import com.ldm.lluviaDeChuches.Pantalla;
import com.ldm.lluviaDeChuches.Graficos.PixmapFormat;

public class LoadingScreen extends Pantalla{
    public LoadingScreen(Juego juego) {
        super(juego);
    }

    @Override
    public void update(float deltaTime) {
        Graficos g = juego.getGraphics();
        Assets.fondo = g.newPixmap("fondo.png", PixmapFormat.RGB565);
        Assets.logo = g.newPixmap("logo.png", PixmapFormat.ARGB4444);
        Assets.menu = g.newPixmap("menu.png", PixmapFormat.ARGB4444);
        Assets.botones = g.newPixmap("botones.png", PixmapFormat.ARGB4444);
        Assets.ayuda1 = g.newPixmap("ayuda1.png", PixmapFormat.ARGB4444);
        Assets.ayuda2 = g.newPixmap("ayuda2.png", PixmapFormat.ARGB4444);
        Assets.ayuda3 = g.newPixmap("ayuda3.png", PixmapFormat.ARGB4444);
        Assets.ayuda4 = g.newPixmap("ayuda4.png", PixmapFormat.ARGB4444);
        Assets.numeros = g.newPixmap("numeros.png", PixmapFormat.ARGB4444);
        Assets.preparadoN = g.newPixmap("preparadoN.png", PixmapFormat.ARGB4444);
        Assets.preparadoE = g.newPixmap("preparadoE.png", PixmapFormat.ARGB4444);
        Assets.menupausa = g.newPixmap("menupausa.png", PixmapFormat.ARGB4444);
        Assets.finjuego = g.newPixmap("finjuego.png", PixmapFormat.ARGB4444);
        Assets.nino = g.newPixmap("nino.png", PixmapFormat.ARGB4444);
        Assets.aura = g.newPixmap("aura.png", PixmapFormat.ARGB4444);
        Assets.cactus = g.newPixmap("cactus.png", PixmapFormat.ARGB4444);
        Assets.sombrero = g.newPixmap("sombrero.png", PixmapFormat.ARGB4444);
        Assets.mosca = g.newPixmap("mosca.png", PixmapFormat.ARGB4444);
        Assets.escudo = g.newPixmap("escudo.png", PixmapFormat.ARGB4444);
        Assets.caramelo = g.newPixmap("caramelo.png", PixmapFormat.ARGB4444);
        Assets.piruleta = g.newPixmap("piruleta.png", PixmapFormat.ARGB4444);
        Assets.copa = g.newPixmap("copa.png", PixmapFormat.ARGB4444);
        Assets.clic = juego.getAudio().nuevoSonido("clic.ogg");
        Assets.comer = juego.getAudio().nuevoSonido("comer.ogg");
        Assets.platoroto = juego.getAudio().nuevoSonido("platoroto.ogg");
        Assets.asco = juego.getAudio().nuevoSonido("asco.ogg");
        Assets.ains = juego.getAudio().nuevoSonido("ains.ogg");
        Assets.clink = juego.getAudio().nuevoSonido("clink.ogg");


        Configuraciones.cargar(juego.getFileIO());
        juego.setScreen(new MainMenuScreen(juego));
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