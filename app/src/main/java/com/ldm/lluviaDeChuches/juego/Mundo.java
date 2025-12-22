package com.ldm.lluviaDeChuches.juego;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Mundo {
    static final int MUNDO_ANCHO = 10;
    static final int MUNDO_ALTO = 13;
    static final float TICK_INICIAL = 0.7f;

    private float tiempoProximoObjetivo = 1.5f;
    private float tiempoVidaObjetivo = 3.0f;

    public List<Objetivo> objetivos = new ArrayList<>();
    public boolean finalJuego = false;
    public int puntuacion = 0;

    private int objetivosFallados = 0;
    private int maxFallos = 5;

    private Random random = new Random();
    private boolean modoExtremo;

    float tiempoTick = 0;
    static float tick = TICK_INICIAL;

    public Mundo(boolean modoExtremo) {
        this.modoExtremo = modoExtremo;
        this.maxFallos = modoExtremo ? 3 : 5;
        colocarObjetivos();
    }

    public void update(float deltaTime) {
        if (finalJuego) return;

        tiempoTick += deltaTime;
        tiempoProximoObjetivo -= deltaTime;

        // Actualizar tiempo de vida de cada objetivo
        for (int i = objetivos.size() - 1; i >= 0; i--) {
            Objetivo objetivo = objetivos.get(i);

            if (objetivo.y == 0) {
                objetivo.y = (int)(tiempoVidaObjetivo * 1000);
            }

            objetivo.y -= (int)(deltaTime * 1000);

            if (objetivo.y <= 0) {
                objetivos.remove(i);
                objetivosFallados++;

                if (Configuraciones.sonidoHabilitado) {
                    Assets.perder.play(1);
                }

                if (objetivosFallados >= maxFallos) {
                    finalJuego = true;
                    return;
                }
            }
        }

        // Generar nuevos objetivos
        if (tiempoProximoObjetivo <= 0 && objetivos.size() < (modoExtremo ? 6 : 4)) {
            colocarObjetivos();
            tiempoProximoObjetivo = modoExtremo ? 0.8f : 1.2f;
        }

        ajustarVelocidad();
    }

    public boolean dispararAObjetivo(int touchX, int touchY) {
        for (int i = objetivos.size() - 1; i >= 0; i--) {
            Objetivo objetivo = objetivos.get(i);

            int objetivoX = objetivo.x * 32;
            int objetivoY_pantalla = 80 + (i * 50);

            if (touchX >= objetivoX && touchX <= objetivoX + 32 &&
                    touchY >= objetivoY_pantalla && touchY <= objetivoY_pantalla + 32) {

                int puntos = calcularPuntos(objetivo.tipo);
                puntuacion += puntos;
                objetivos.remove(i);

                if (Configuraciones.sonidoHabilitado) {
                    if (objetivo.tipo == Objetivo.TIPO_4) {
                        Assets.bonus.play(1);
                    } else {
                        Assets.acierto.play(1);
                    }
                }

                return true;
            }
        }

        if (Configuraciones.sonidoHabilitado) {
            Assets.fallo.play(1);
        }
        return false;
    }

    private int calcularPuntos(int tipo) {
        switch (tipo) {
            case Objetivo.TIPO_1: return 5;
            case Objetivo.TIPO_2: return 10;
            case Objetivo.TIPO_3: return 15;
            case Objetivo.TIPO_4: return 25;
            default: return 5;
        }
    }

    private void colocarObjetivos() {
        int x = random.nextInt(MUNDO_ANCHO);

        int probabilidad = random.nextInt(100);
        int tipoObjetivo;

        if (probabilidad < 50) {
            tipoObjetivo = Objetivo.TIPO_1;
        } else if (probabilidad < 75) {
            tipoObjetivo = Objetivo.TIPO_2;
        } else if (probabilidad < 90) {
            tipoObjetivo = Objetivo.TIPO_3;
        } else {
            tipoObjetivo = Objetivo.TIPO_4;
        }

        objetivos.add(new Objetivo(x, 0, tipoObjetivo));
    }

    private void ajustarVelocidad() {
        if (modoExtremo) {
            if (puntuacion >= 50 && tiempoVidaObjetivo > 2.0f) {
                tiempoVidaObjetivo = 2.0f;
            } else if (puntuacion >= 100 && tiempoVidaObjetivo > 1.5f) {
                tiempoVidaObjetivo = 1.5f;
            }
        } else {
            if (puntuacion >= 80 && tiempoVidaObjetivo > 2.5f) {
                tiempoVidaObjetivo = 2.5f;
            } else if (puntuacion >= 150 && tiempoVidaObjetivo > 2.0f) {
                tiempoVidaObjetivo = 2.0f;
            }
        }
    }

    public int getObjetivosFallados() {
        return objetivosFallados;
    }

    public int getMaxFallos() {
        return maxFallos;
    }

    public float getTiempoRestanteObjetivo(int index) {
        if (index >= 0 && index < objetivos.size()) {
            return objetivos.get(index).y / 1000.0f;
        }
        return 0;
    }
}