package com.ldm.lluviaDeChuches.juego;

import com.ldm.lluviaDeChuches.Pixmap;
import com.ldm.lluviaDeChuches.Sonido;

public class Assets {
    public static Pixmap fondo;
    public static Pixmap logo;
    public static Pixmap menu;
    public static Pixmap botones;
    public static Pixmap ayuda1;
    public static Pixmap ayuda2;
    public static Pixmap ayuda3;
    public static Pixmap ayuda4;
    public static Pixmap numeros;
    public static Pixmap preparadoE;
    public static Pixmap preparadoN;
    public static Pixmap menupausa;
    public static Pixmap finjuego;
    public static Pixmap nino;
    public static Pixmap aura;
    public static Pixmap cactus;
    public static Pixmap sombrero;
    public static Pixmap escudo;
    public static Pixmap copa;
    public static Pixmap piruleta;
    public static Pixmap caramelo;
    public static Pixmap mosca;

    public static Sonido clic;
    public static Sonido platoroto;
    public static Sonido comer;
    public static Sonido asco;
    public static Sonido ains;
    public static Sonido clink;

    public static Pixmap obtenerPixmapIngrediente(int tipo) {
        switch (tipo) {
            case Ingredientes.TIPO_1:
                return Assets.caramelo;
            case Ingredientes.TIPO_2:
                return Assets.piruleta;
            case Ingredientes.TIPO_3:
                return Assets.copa;
            case Ingredientes.TIPO_4:
                return Assets.escudo;
            case Ingredientes.TIPO_MOSCA:
                return Assets.mosca;
            default:
                return null;
        }
    }

    public static Pixmap obtenerPixmapObstaculo(int tipo) {
        switch (tipo) {
            case 1:
                return Assets.cactus;
            case 2:
                return Assets.sombrero;
            default:
                return null;
        }
    }

}