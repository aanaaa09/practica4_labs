package com.ldm.spaceDefenders.juego;

import com.ldm.spaceDefenders.Pantalla;
import com.ldm.spaceDefenders.androidimpl.AndroidJuego;

public class JuegoSpaceDefenders extends AndroidJuego {
    @Override
    public Pantalla getStartScreen() {
        return new LoadingScreen(this);
    }
}
