package com.ldm.lluviaDeChuches.juego;

public class Ingredientes {
    public static final int TIPO_1 = 0;
    public static final int TIPO_2 = 1;
    public static final int TIPO_3 = 2;
    public static final int TIPO_4 = 3;
    public static final int TIPO_MOSCA = 4;
    public int x, y;
    public int tipo;

    public Ingredientes(int x, int y, int tipo) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
    }
}
