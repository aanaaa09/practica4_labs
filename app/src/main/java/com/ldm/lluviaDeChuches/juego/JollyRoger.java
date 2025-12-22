package com.ldm.lluviaDeChuches.juego;

public class JollyRoger {
    public int x;
    public final int y;

    public JollyRoger(int anchoTablero) {
        this.x = anchoTablero / 2;
        this.y = 12;
    }

    public void moverIzquierda() {
        x -= 1;
        if (x < 0) x = 0;
    }

    public void moverDerecha(int anchoTablero) {
        x += 1;
        if (x >= anchoTablero) x = anchoTablero - 1;
    }
}
