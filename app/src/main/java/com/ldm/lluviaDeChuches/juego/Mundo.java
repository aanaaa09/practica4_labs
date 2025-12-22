package com.ldm.lluviaDeChuches.juego;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Mundo {
    static final int MUNDO_ANCHO = 10;
    static final int MUNDO_ALTO = 13;
    static final float TICK_INICIAL = 0.7f;

    private float tiempoProximoIngrediente = 1.5f;
    private float tiempoProximaMosca = 2.0f;
    private float tiempoProximoObstaculo = 2.5f;
    private boolean escudoActivo = false;
    private float tiempoEscudoRestante = 0;

    public JollyRoger jollyroger;
    public List<Ingredientes> ingredientes = new ArrayList<>();
    public boolean finalJuego = false;
    public int puntuacion = 0;

    private List<Obstaculo> obstaculos = new ArrayList<>();
    private List<Ingredientes> moscas = new ArrayList<>();
    private boolean moscaComida = false;

    private Random random = new Random();
    private boolean modoExtremo;

    float tiempoTick = 0;
    static float tick = TICK_INICIAL;

    public Mundo(boolean modoExtremo) {
        this.modoExtremo = modoExtremo;
        jollyroger = new JollyRoger(MUNDO_ANCHO);
        colocarIngredientes();
    }

    public List<Obstaculo> getObstaculos() {
        return obstaculos;
    }

    public List<Ingredientes> getMoscas() {
        return moscas;
    }

    public void update(float deltaTime) {
        if (finalJuego) return;

        tiempoTick += deltaTime;

        // Reducir temporizadores para generación progresiva (solo en modo extremo)
        if (modoExtremo) {
            tiempoProximoIngrediente -= deltaTime;
            tiempoProximaMosca -= deltaTime;
            tiempoProximoObstaculo -= deltaTime;
        }

        // Reducir el tiempo del escudo si está activo
        if (escudoActivo) {
            tiempoEscudoRestante -= deltaTime;
            if (tiempoEscudoRestante <= 0) {
                escudoActivo = false;
            }
        }

        while (tiempoTick > tick) {
            tiempoTick -= tick;

            moverObjetos();

            // Verificar si algún ingrediente toca el suelo
            for (int i = ingredientes.size() - 1; i >= 0; i--) {
                Ingredientes ingrediente = ingredientes.get(i);

                if (ingrediente.y >= MUNDO_ALTO) {
                    if (ingrediente.tipo == Ingredientes.TIPO_4) {

                        ingredientes.remove(i);
                    } else if (escudoActivo) {

                        ingredientes.remove(i);
                    } else {

                        finalJuego = true;
                        return;
                    }
                }
            }

            moscas.removeIf(mosca -> mosca.y >= MUNDO_ALTO);

            moscas.removeIf(mosca -> mosca.y >= MUNDO_ALTO);
            obstaculos.removeIf(obstaculo -> obstaculo.y >= MUNDO_ALTO);

            detectarColisiones();

            if (modoExtremo) {

                if (ingredientes.size() < 3 && tiempoProximoIngrediente <= 0) {
                    colocarIngredientes();
                    tiempoProximoIngrediente = 1.5f;
                }

                if (moscas.size() < 2 && tiempoProximaMosca <= 0) {
                    generarMosca();
                    tiempoProximaMosca = 2.0f;
                }

                if (obstaculos.size() < 2 && tiempoProximoObstaculo <= 0) {
                    generarObstaculo();
                    tiempoProximoObstaculo = 2.5f;
                }
            } else {
                if (ingredientes.isEmpty()) {
                    colocarIngredientes();
                }

                if (moscas.isEmpty() && random.nextInt(100) < 10) { // 10% probabilidad
                    generarMosca();
                }

                if (obstaculos.isEmpty() && random.nextInt(100) < 5) { // 5% probabilidad
                    generarObstaculo();
                }
            }

            ajustarVelocidad();
        }
    }




    private void moverObjetos() {
        for (Ingredientes ingrediente : ingredientes) {
            ingrediente.y += 1;
        }

        for (Ingredientes mosca : moscas) {
            mosca.y += 1;
        }

        for (Obstaculo obstaculo : obstaculos) {
            obstaculo.y += 1;
        }
    }

    private boolean rectangulosSeSuperponen(int x1, int y1, int ancho1, int alto1, int x2, int y2, int ancho2, int alto2) {
        return x1 < x2 + ancho2 && x1 + ancho1 > x2 && y1 < y2 + alto2 && y1 + alto1 > y2;
    }


    private void detectarColisiones() {
        int ninoX = jollyroger.x * 32;
        int ninoY = jollyroger.y * 32;
        int ninoAncho = 32;
        int ninoAlto = 32;

        for (int i = ingredientes.size() - 1; i >= 0; i--) {
            Ingredientes ingrediente = ingredientes.get(i);

            // Definir las dimensiones del ingrediente
            int ingredienteX = ingrediente.x * 32;
            int ingredienteY = ingrediente.y * 32;
            int ingredienteAncho = 32;
            int ingredienteAlto = 32;

            // Comprobar si las áreas del chef y el ingrediente se superponen
            if (rectangulosSeSuperponen(ninoX, ninoY, ninoAncho, ninoAlto, ingredienteX, ingredienteY, ingredienteAncho, ingredienteAlto)) {
                manejarIngredientes(i);
            }
        }

        for (int i = moscas.size() - 1; i >= 0; i--) {
            Ingredientes mosca = moscas.get(i);

            int moscaX = mosca.x * 32;
            int moscaY = mosca.y * 32;
            int moscaAncho = 32;
            int moscaAlto = 32;

            // Comprobar si las áreas del chef y la mosca se superponen
            if (rectangulosSeSuperponen(ninoX, ninoY, ninoAncho, ninoAlto, moscaX, moscaY, moscaAncho, moscaAlto)) {
                if (!escudoActivo) {
                    manejarMosca(i);
                } else {
                    moscas.remove(i);
                }
            }
        }

        // Verificar colisiones con obstáculos
        for (int i = obstaculos.size() - 1; i >= 0; i--) {
            Obstaculo obstaculo = obstaculos.get(i);

            // Definir las dimensiones del obstáculo
            int obstaculoX = obstaculo.x * 32;
            int obstaculoY = obstaculo.y * 32;
            int obstaculoAncho = 32;
            int obstaculoAlto = 32;

            // Comprobar si las áreas del chef y el obstáculo se superponen
            if (rectangulosSeSuperponen(ninoX, ninoY, ninoAncho, ninoAlto, obstaculoX, obstaculoY, obstaculoAncho, obstaculoAlto)) {
                if (!escudoActivo) {
                    finalJuego = true;
                    return;
                } else {
                    obstaculos.remove(i);
                }
            }
        }
    }


    private void manejarIngredientes(int index) {
        Ingredientes ingrediente = ingredientes.get(index);

        switch (ingrediente.tipo) {
            case Ingredientes.TIPO_1:
                puntuacion += 5;
                break;
            case Ingredientes.TIPO_2:
                puntuacion += 10;
                break;
            case Ingredientes.TIPO_3:
                puntuacion += 15;
                break;
            case Ingredientes.TIPO_4:
                activarEscudo(5);
                if (Configuraciones.sonidoHabilitado) {
                    Assets.clink.play(1);
                }
                break;
        }

        ingredientes.remove(index);
    }

    public void activarEscudo(float duracion) {
        escudoActivo = true;
        tiempoEscudoRestante = duracion;
    }

    public boolean esEscudoActivo() {
        return escudoActivo;
    }

    public boolean moscaFueComida() {
        boolean fueComido = moscaComida;
        moscaComida = false;
        return fueComido;
    }

    private void manejarMosca(int index) {
        puntuacion -= 5;
        if (puntuacion < 0) puntuacion = 0;
        moscas.remove(index);
        moscaComida = true;
        if (Configuraciones.sonidoHabilitado) {
            Assets.asco.play(1);
        }
    }

    private boolean camposOcupados(int x, int y) {

        for (Ingredientes ingrediente : ingredientes) {
            if (ingrediente.x == x && ingrediente.y == y) {
                return true;
            }
        }

        for (Ingredientes mosca : moscas) {
            if (mosca.x == x && mosca.y == y) {
                return true;
            }
        }
        for (Obstaculo obstaculo : obstaculos) {
            if (obstaculo.x == x && obstaculo.y == y) {
                return true;
            }
        }

        return false;
    }

    private void generarMosca() {
        int moscaX, moscaY;
        int intentos = 0;

        do {
            moscaX = random.nextInt(MUNDO_ANCHO);
            moscaY = 0;
            intentos++;
        } while (camposOcupados(moscaX, moscaY) && intentos < 100);

        if (intentos >= 100) {
            return;
        }

        Ingredientes nuevoMosca = new Ingredientes(moscaX, moscaY, Ingredientes.TIPO_MOSCA);
        moscas.add(nuevoMosca);
    }

    private void generarObstaculo() {
        int obstaculoX, obstaculoY;
        int intentos = 0;

        do {
            obstaculoX = random.nextInt(MUNDO_ANCHO);
            obstaculoY = 0;
            intentos++;
        } while (camposOcupados(obstaculoX, obstaculoY) && intentos < 100);

        // Si no se encontró una posición válida después de 100 intentos, no se genera el obstáculo
        if (intentos >= 100) {
            return;
        }

        int tipo = random.nextInt(2) + 1;
        Obstaculo nuevoObstaculo = new Obstaculo(obstaculoX, obstaculoY, tipo);
        obstaculos.add(nuevoObstaculo);
    }

    private void colocarIngredientes() {
        int x = random.nextInt(MUNDO_ANCHO);

        int probabilidad = random.nextInt(100);
        int tipoIngrediente;

        if (probabilidad < 50) {
            tipoIngrediente = Ingredientes.TIPO_1; // 50% de probabilidad
        } else if (probabilidad < 75) {
            tipoIngrediente = Ingredientes.TIPO_2; // 25% de probabilidad
        } else if (probabilidad < 90) {
            tipoIngrediente = Ingredientes.TIPO_3; // 15% de probabilidad
        } else {
            tipoIngrediente = Ingredientes.TIPO_4; // 10% de probabilidad
        }

        ingredientes.add(new Ingredientes(x, 0, tipoIngrediente));
    }


    private void ajustarVelocidad() {
        if (modoExtremo) {
            if (puntuacion == 20 && tick > 0.2f) {
                tick -= 0.02f;
            } else if (puntuacion == 60 && tick > 0.2f) {
                tick -= 0.03f;
            }
        } else {
            if (puntuacion == 40 && tick > 0.3f) {
                tick -= 0.02f;
            } else if (puntuacion == 80 && tick > 0.2f) {
                tick -= 0.03f;
            }
        }

    }
}
