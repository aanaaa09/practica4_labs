package com.ldm.spaceDefenders.juego;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import com.ldm.spaceDefenders.Graficos;
import com.ldm.spaceDefenders.Input.TouchEvent;
import com.ldm.spaceDefenders.Juego;
import com.ldm.spaceDefenders.Pantalla;
import com.ldm.spaceDefenders.androidimpl.AndroidJuego;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class PantallaLoginRegistro extends Pantalla {

    private enum Modo { LOGIN, REGISTRO }
    private Modo modoActual = Modo.LOGIN;

    private AdminSQLiteOpenHelper admin;

    // Campos de texto simulados (sin teclado real en este framework)
    private String emailInput = "";
    private String passwordInput = "";
    private String nombreInput = "";

    private String mensajeError = "";
    private int colorMensaje = Color.RED;

    private int campoActivo = 0; // 0=email, 1=password, 2=nombre

    public PantallaLoginRegistro(Juego juego) {
        super(juego);
        // Obtener contexto de Android desde AndroidJuego
        if (juego instanceof AndroidJuego) {
            admin = new AdminSQLiteOpenHelper((AndroidJuego) juego);
        }
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = juego.getInput().getTouchEvents();

        for (TouchEvent event : touchEvents) {
            if (event.type == TouchEvent.TOUCH_UP) {

                // Botón cambiar modo (LOGIN ↔ REGISTRO)
                if (inBounds(event, 20, 400, 130, 40)) {
                    modoActual = (modoActual == Modo.LOGIN) ? Modo.REGISTRO : Modo.LOGIN;
                    limpiarCampos();
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    return;
                }

                // Botón ENTRAR / REGISTRAR
                if (inBounds(event, 200, 400, 100, 40)) {
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);

                    if (modoActual == Modo.LOGIN) {
                        realizarLogin();
                    } else {
                        realizarRegistro();
                    }
                    return;
                }

                // NOTA: En un juego real, necesitarías implementar un teclado virtual
                // Por simplicidad, voy a crear usuarios de prueba automáticamente

                // Botón "Usuario de prueba"
                if (inBounds(event, 170, 450, 140, 30)) {
                    crearUsuarioPrueba();
                    return;
                }
            }
        }
    }

    private void realizarLogin() {
        // Validaciones básicas
        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            mensajeError = "Completa todos los campos";
            colorMensaje = Color.rgb(255, 165, 0); // Naranja
            return;
        }

        SQLiteDatabase db = admin.getReadableDatabase();
        String hashedPassword = hashPassword(passwordInput);

        Cursor cursor = db.query(
                AdminSQLiteOpenHelper.TABLE_USUARIOS,
                new String[]{AdminSQLiteOpenHelper.COLUMN_NOMBRE, AdminSQLiteOpenHelper.COLUMN_EMAIL},
                AdminSQLiteOpenHelper.COLUMN_EMAIL + "=? AND " + AdminSQLiteOpenHelper.COLUMN_PASSWORD + "=?",
                new String[]{emailInput, hashedPassword},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(0);
            String email = cursor.getString(1);

            cursor.close();
            db.close();

            // Login exitoso - ir al menú con datos del usuario
            SesionUsuario.emailActual = email;
            SesionUsuario.nombreActual = nombre;

            juego.setScreen(new MainMenuScreen(juego));
        } else {
            cursor.close();
            db.close();
            mensajeError = "Credenciales incorrectas";
            colorMensaje = Color.RED;
        }
    }

    private void realizarRegistro() {
        if (emailInput.isEmpty() || passwordInput.isEmpty() || nombreInput.isEmpty()) {
            mensajeError = "Completa todos los campos";
            colorMensaje = Color.rgb(255, 165, 0);
            return;
        }

        SQLiteDatabase db = admin.getWritableDatabase();

        // Verificar si el email ya existe
        Cursor cursor = db.query(
                AdminSQLiteOpenHelper.TABLE_USUARIOS,
                new String[]{AdminSQLiteOpenHelper.COLUMN_EMAIL},
                AdminSQLiteOpenHelper.COLUMN_EMAIL + "=?",
                new String[]{emailInput},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            mensajeError = "Email ya registrado";
            colorMensaje = Color.RED;
            return;
        }
        cursor.close();

        // Insertar nuevo usuario
        ContentValues valores = new ContentValues();
        valores.put(AdminSQLiteOpenHelper.COLUMN_EMAIL, emailInput);
        valores.put(AdminSQLiteOpenHelper.COLUMN_PASSWORD, hashPassword(passwordInput));
        valores.put(AdminSQLiteOpenHelper.COLUMN_NOMBRE, nombreInput);

        long resultado = db.insert(AdminSQLiteOpenHelper.TABLE_USUARIOS, null, valores);
        db.close();

        if (resultado != -1) {
            mensajeError = "Cuenta creada! Iniciando sesion...";
            colorMensaje = Color.GREEN;

            SesionUsuario.emailActual = emailInput;
            SesionUsuario.nombreActual = nombreInput;

            // Ir al menú después de 1 segundo
            new android.os.Handler().postDelayed(() -> {
                juego.setScreen(new MainMenuScreen(juego));
            }, 1000);
        } else {
            mensajeError = "Error al crear cuenta";
            colorMensaje = Color.RED;
        }
    }

    private void crearUsuarioPrueba() {
        // Usuario de prueba para testing
        emailInput = "test@space.com";
        passwordInput = "123456";
        nombreInput = "Piloto Test";

        realizarRegistro();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }

    private void limpiarCampos() {
        emailInput = "";
        passwordInput = "";
        nombreInput = "";
        mensajeError = "";
    }

    private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
        return event.x > x && event.x < x + width &&
                event.y > y && event.y < y + height;
    }

    @Override
    public void present(float deltaTime) {
        Graficos g = juego.getGraphics();

        // Fondo
        g.drawPixmap(Assets.fondo, 0, 0);

        // Título
        String titulo = (modoActual == Modo.LOGIN) ? "INICIAR SESION" : "CREAR CUENTA";
        g.drawText(titulo, g.getWidth() / 2, 80, Color.WHITE, 24, true);

        // Información de campos (simulado)
        g.drawText("Email: " + emailInput, 50, 150, Color.WHITE, 18, false);
        g.drawText("Password: " + ocultarPassword(passwordInput), 50, 200, Color.WHITE, 18, false);

        if (modoActual == Modo.REGISTRO) {
            g.drawText("Nombre: " + nombreInput, 50, 250, Color.WHITE, 18, false);
        }

        // Mensaje de error/éxito
        if (!mensajeError.isEmpty()) {
            g.drawText(mensajeError, g.getWidth() / 2, 320, colorMensaje, 16, true);
        }

        // Botones
        String textoBotonCambio = (modoActual == Modo.LOGIN) ? "Registrarse" : "Ya tengo cuenta";
        g.drawRect(20, 400, 130, 40, Color.DKGRAY);
        g.drawText(textoBotonCambio, 85, 425, Color.WHITE, 14, true);

        String textoBotonAccion = (modoActual == Modo.LOGIN) ? "ENTRAR" : "REGISTRAR";
        g.drawRect(200, 400, 100, 40, Color.rgb(0, 150, 0));
        g.drawText(textoBotonAccion, 250, 425, Color.WHITE, 16, true);

        // Botón usuario de prueba (solo para testing)
        g.drawRect(170, 450, 140, 30, Color.rgb(100, 100, 255));
        g.drawText("Usuario Prueba", 240, 470, Color.WHITE, 12, true);

        // Nota importante
        g.drawText("NOTA: Sistema simplificado - Click en 'Usuario Prueba'",
                g.getWidth() / 2, g.getHeight() - 20, Color.YELLOW, 12, true);
    }

    private String ocultarPassword(String password) {
        return "*".repeat(password.length());
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {}
}