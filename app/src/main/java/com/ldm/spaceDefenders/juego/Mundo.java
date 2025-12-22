package com.ldm.spaceDefenders.juego;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Mundo {
    static final int MUNDO_ANCHO = 10;
    static final int MUNDO_ALTO = 13;
    static final float TICK_INICIAL = 0.7f;

    // Velocidad de caída de objetivos (píxeles por segundo)
    private float velocidadCaida;
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

    // Altura máxima de la pantalla de juego (en píxeles)
    private static final int ALTURA_JUEGO = 416;

    public Mundo(boolean modoExtremo) {
        this.modoExtremo = modoExtremo;
        this.maxFallos = modoExtremo ? 3 : 5;

        // Velocidad de caída según el modo
        this.velocidadCaida = modoExtremo ? 120.0f : 80.0f; // píxeles/segundo
        this.tiempoVidaObjetivo = modoExtremo ? 2.5f : 3.5f;

        colocarObjetivos();
    }

    public void update(float deltaTime) {
        if (finalJuego) return;

        tiempoTick += deltaTime;
        tiempoProximoObjetivo -= deltaTime;

        // Actualizar posición Y (caída) de cada objetivo
        for (int i = objetivos.size() - 1; i >= 0; i--) {
            Objetivo objetivo = objetivos.get(i);

            // Hacer caer el objetivo (y representa la posición vertical en píxeles)
            objetivo.y += (int)(velocidadCaida * deltaTime);

            // Si el objetivo llegó al fondo de la pantalla
            if (objetivo.y >= ALTURA_JUEGO) {
                objetivos.remove(i);
                objetivosFallados++;

                if (Configuraciones.sonidoHabilitado) {
                    Assets.error.play(1);
                }

                if (objetivosFallados >= maxFallos) {
                    finalJuego = true;
                    return;
                }
            }
        }

        // Generar nuevos objetivos
        if (tiempoProximoObjetivo <= 0 && objetivos.size() < (modoExtremo ? 5 : 3)) {
            colocarObjetivos();
            tiempoProximoObjetivo = modoExtremo ? 0.6f : 1.0f;
        }

        ajustarVelocidad();
    }

    public boolean dispararAObjetivo(int touchX, int touchY) {
        // SIEMPRE reproducir el sonido de disparo, independientemente de si acierta o no
        if (Configuraciones.sonidoHabilitado) {
            Assets.disparo.play(1);
        }

        for (int i = objetivos.size() - 1; i >= 0; i--) {
            Objetivo objetivo = objetivos.get(i);

            int objetivoX = objetivo.x * 32;
            int objetivoY = objetivo.y;

            // Área de colisión del objetivo (32x32 píxeles)
            if (touchX >= objetivoX && touchX <= objetivoX + 32 &&
                    touchY >= objetivoY && touchY <= objetivoY + 32) {

                int puntos = calcularPuntos(objetivo.tipo);
                puntuacion += puntos;
                objetivos.remove(i);

                return true;
            }
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

        // Crear objetivo en la parte superior (y = 0)
        objetivos.add(new Objetivo(x, 0, tipoObjetivo));
    }

    private void ajustarVelocidad() {
        if (modoExtremo) {
            if (puntuacion >= 50 && velocidadCaida < 150.0f) {
                velocidadCaida = 150.0f;
            } else if (puntuacion >= 100 && velocidadCaida < 180.0f) {
                velocidadCaida = 180.0f;
            }
        } else {
            if (puntuacion >= 80 && velocidadCaida < 100.0f) {
                velocidadCaida = 100.0f;
            } else if (puntuacion >= 150 && velocidadCaida < 120.0f) {
                velocidadCaida = 120.0f;
            }
        }
    }

    public int getObjetivosFallados() {
        return objetivosFallados;
    }

    public int getMaxFallos() {
        return maxFallos;
    }

    // Obtener el porcentaje de progreso de caída (0.0 a 1.0)
    public float getPorcentajeCaida(Objetivo objetivo) {
        return (float) objetivo.y / ALTURA_JUEGO;
    }
}