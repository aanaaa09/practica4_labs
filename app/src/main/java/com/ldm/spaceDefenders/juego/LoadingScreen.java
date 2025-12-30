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
        Assets.jugador = g.newPixmap("nino.png", PixmapFormat.ARGB4444);
        Assets.aura = g.newPixmap("aura.png", PixmapFormat.ARGB4444);
        Assets.objetivoEspecial = g.newPixmap("mosca.png", PixmapFormat.ARGB4444);
        Assets.escudo = g.newPixmap("escudo.png", PixmapFormat.ARGB4444);
        Assets.objetivo1 = g.newPixmap("caramelo.png", PixmapFormat.ARGB4444);
        Assets.objetivo2 = g.newPixmap("piruleta.png", PixmapFormat.ARGB4444);
        Assets.objetivo3 = g.newPixmap("copa.png", PixmapFormat.ARGB4444);

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