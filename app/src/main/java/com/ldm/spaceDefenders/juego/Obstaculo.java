
package com.ldm.spaceDefenders.juego;

public class Obstaculo {
    public int x, y;
    public float tiempoVida;
    public int tipo;

    public Obstaculo(int x, int y, int tipo) {
        this.x = x;
        this.y = y;
        this.tiempoVida = 0;
        this.tipo = tipo;
    }
}
