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
    public static Pixmap jugador;
    public static Pixmap aura;
    public static Pixmap obstaculo1;
    public static Pixmap obstaculo2;
    public static Pixmap escudo;
    public static Pixmap objetivo1;
    public static Pixmap objetivo2;
    public static Pixmap objetivo3;
    public static Pixmap objetivoEspecial;

    public static Sonido clic;
    public static Sonido error;
    public static Sonido acierto;
    public static Sonido fallo;
    public static Sonido perder;
    public static Sonido bonus;

    public static Pixmap obtenerPixmapObjetivo(int tipo) {
        switch (tipo) {
            case Objetivo.TIPO_1:
                return Assets.objetivo1;
            case Objetivo.TIPO_2:
                return Assets.objetivo2;
            case Objetivo.TIPO_3:
                return Assets.objetivo3;
            case Objetivo.TIPO_4:
                return Assets.escudo;
            case Objetivo.TIPO_ESPECIAL:
                return Assets.objetivoEspecial;
            default:
                return null;
        }
    }

    public static Pixmap obtenerPixmapObstaculo(int tipo) {
        switch (tipo) {
            case 1:
                return Assets.obstaculo1;
            case 2:
                return Assets.obstaculo2;
            default:
                return null;
        }
    }
}