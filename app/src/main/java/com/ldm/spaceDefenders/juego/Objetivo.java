package com.ldm.spaceDefenders.juego;

public class Objetivo {
    public static final int TIPO_1 = 0;
    public static final int TIPO_2 = 1;
    public static final int TIPO_3 = 2;
    public static final int TIPO_4 = 3;
    public static final int TIPO_ESPECIAL = 8;
    public int x, y;
    public int tipo;

    public Objetivo(int x, int y, int tipo) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
    }
}
