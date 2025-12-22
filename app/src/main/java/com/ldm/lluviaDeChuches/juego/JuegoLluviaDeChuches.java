package com.ldm.lluviaDeChuches.juego;

import com.ldm.lluviaDeChuches.Pantalla;
import com.ldm.lluviaDeChuches.androidimpl.AndroidJuego;

public class JuegoLluviaDeChuches extends AndroidJuego {
    @Override
    public Pantalla getStartScreen() {
        return new LoadingScreen(this);
    }
}
