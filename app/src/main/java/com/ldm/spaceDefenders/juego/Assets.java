package com.ldm.spaceDefenders.juego;

import com.ldm.spaceDefenders.Pixmap;
import com.ldm.spaceDefenders.Sonido;

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
    public static Pixmap escudo;
    public static Pixmap objetivo1;
    public static Pixmap objetivo2;
    public static Pixmap objetivo3;
    public static Pixmap objetivoEspecial;

    // Sonidos que se mantienen
    public static Sonido clic;      // Para clics en botones
    public static Sonido error;     // Para cuando un objetivo llega al fondo
    public static Sonido disparo;   // NUEVO: Para cada disparo

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
                return Assets.objetivo1;
        }
    }

}