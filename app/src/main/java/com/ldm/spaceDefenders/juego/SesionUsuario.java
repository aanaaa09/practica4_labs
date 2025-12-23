package com.ldm.spaceDefenders.juego;

public class SesionUsuario {
    public static String emailActual = "";
    public static String nombreActual = "";

    public static boolean haySesionActiva() {
        return !emailActual.isEmpty();
    }

    public static void cerrarSesion() {
        emailActual = "";
        nombreActual = "";
    }
}