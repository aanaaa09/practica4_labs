package com.ldm.spaceDefenders.juego;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ldm.spaceDefenders.Graficos;
import com.ldm.spaceDefenders.Input.TouchEvent;
import com.ldm.spaceDefenders.Juego;
import com.ldm.spaceDefenders.Pantalla;
import com.ldm.spaceDefenders.androidimpl.AndroidJuego;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Pattern;

public class PantallaLoginRegistro extends Pantalla {

    private enum Modo { LOGIN, REGISTRO }
    private Modo modoActual = Modo.LOGIN;

    private AdminSQLiteOpenHelper admin;

    // Campos de texto
    private String emailInput = "";
    private String passwordInput = "";
    private String nombreInput = "";

    private String mensajeError = "";
    private int colorMensaje = Color.RED;

    private AndroidJuego androidJuego;

    // Patrón para validar emails
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public PantallaLoginRegistro(Juego juego) {
        super(juego);
        if (juego instanceof AndroidJuego) {
            androidJuego = (AndroidJuego) juego;
            admin = new AdminSQLiteOpenHelper(androidJuego);
        }
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = juego.getInput().getTouchEvents();

        for (TouchEvent event : touchEvents) {
            if (event.type == TouchEvent.TOUCH_UP) {

                // Calcular posiciones (igual que en present)
                int inicioY = 140;
                int alturaCampo = 35;
                int anchoCampo = 280;
                int xCampo = (juego.getGraphics().getWidth() - anchoCampo) / 2;

                // Campo Email
                if (inBounds(event, xCampo, inicioY, anchoCampo, alturaCampo)) {
                    mostrarDialogoInput("Email", emailInput, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                            texto -> emailInput = texto);
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    return;
                }

                // Campo Password
                if (inBounds(event, xCampo, inicioY + 45, anchoCampo, alturaCampo)) {
                    mostrarDialogoInput("Contraseña", passwordInput,
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD,
                            texto -> passwordInput = texto);
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    return;
                }

                // Campo Nombre (solo en registro)
                if (modoActual == Modo.REGISTRO && inBounds(event, xCampo, inicioY + 90, anchoCampo, alturaCampo)) {
                    mostrarDialogoInput("Nombre", nombreInput, InputType.TYPE_CLASS_TEXT,
                            texto -> nombreInput = texto);
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    return;
                }

                // Botones
                int botonY = modoActual == Modo.REGISTRO ? 310 : 280;
                int anchoBoton = 120;
                int espacioEntreBotones = 20;
                int xBoton1 = (juego.getGraphics().getWidth() / 2) - anchoBoton - (espacioEntreBotones / 2);
                int xBoton2 = (juego.getGraphics().getWidth() / 2) + (espacioEntreBotones / 2);

                // Botón cambiar modo (LOGIN ↔ REGISTRO)
                if (inBounds(event, xBoton1, botonY, anchoBoton, 35)) {
                    modoActual = (modoActual == Modo.LOGIN) ? Modo.REGISTRO : Modo.LOGIN;
                    limpiarCampos();
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);
                    return;
                }

                // Botón ENTRAR / REGISTRAR
                if (inBounds(event, xBoton2, botonY, anchoBoton, 35)) {
                    if (Configuraciones.sonidoHabilitado)
                        Assets.clic.play(1);

                    if (modoActual == Modo.LOGIN) {
                        realizarLogin();
                    } else {
                        realizarRegistro();
                    }
                    return;
                }
            }
        }
    }

    private void mostrarDialogoInput(String titulo, String valorActual, int inputType, InputCallback callback) {
        androidJuego.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(androidJuego);
            builder.setTitle(titulo);

            final EditText input = new EditText(androidJuego);
            input.setInputType(inputType);
            input.setText(valorActual);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);

            builder.setView(input);

            builder.setPositiveButton("Aceptar", (dialog, which) -> {
                String texto = input.getText().toString().trim();
                callback.onInput(texto);
            });

            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

            builder.show();
        });
    }

    private interface InputCallback {
        void onInput(String texto);
    }

    // Validar formato de email
    private boolean esEmailValido(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private void realizarLogin() {
        // Validar que los campos no estén vacíos
        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            mensajeError = "Completa todos los campos";
            colorMensaje = Color.rgb(255, 165, 0);
            return;
        }

        // Validar formato de email
        if (!esEmailValido(emailInput)) {
            mensajeError = "Email invalido (usa xxx@xxx.xxx)";
            colorMensaje = Color.RED;
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
        // Validar que los campos no estén vacíos
        if (emailInput.isEmpty() || passwordInput.isEmpty() || nombreInput.isEmpty()) {
            mensajeError = "Completa todos los campos";
            colorMensaje = Color.rgb(255, 165, 0);
            return;
        }

        // Validar formato de email
        if (!esEmailValido(emailInput)) {
            mensajeError = "Email invalido (usa xxx@xxx.xxx)";
            colorMensaje = Color.RED;
            return;
        }

        // Validar longitud mínima de contraseña
        if (passwordInput.length() < 4) {
            mensajeError = "Contraseña muy corta (min 4)";
            colorMensaje = Color.RED;
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

        // Insertar nuevo usuario con contraseña hasheada
        ContentValues valores = new ContentValues();
        valores.put(AdminSQLiteOpenHelper.COLUMN_EMAIL, emailInput);
        valores.put(AdminSQLiteOpenHelper.COLUMN_PASSWORD, hashPassword(passwordInput)); // SE HASHEA AQUÍ
        valores.put(AdminSQLiteOpenHelper.COLUMN_NOMBRE, nombreInput);

        long resultado = db.insert(AdminSQLiteOpenHelper.TABLE_USUARIOS, null, valores);
        db.close();

        if (resultado != -1) {
            mensajeError = "Cuenta creada! Iniciando sesion...";
            colorMensaje = Color.GREEN;

            SesionUsuario.emailActual = emailInput;
            SesionUsuario.nombreActual = nombreInput;

            new android.os.Handler().postDelayed(() -> juego.setScreen(new MainMenuScreen(juego)), 1000);
        } else {
            mensajeError = "Error al crear cuenta";
            colorMensaje = Color.RED;
        }
    }

    // Método que hashea la contraseña con SHA-256
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
            // Si falla el hash, devolver la contraseña sin hashear (fallback)
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

        g.drawPixmap(Assets.fondo, 0, 0);

        // Título más pequeño y elegante
        String titulo = (modoActual == Modo.LOGIN) ? "INICIAR SESION" : "CREAR CUENTA";
        g.drawText(titulo, g.getWidth() / 2, 100, Color.rgb(255, 215, 0), 18, true);

        // Espaciado y tamaño ajustados
        int inicioY = 140;
        int alturaCampo = 35;
        int anchoCampo = 280;
        int xCampo = (g.getWidth() - anchoCampo) / 2;

        // Campo Email
        g.drawRect(xCampo, inicioY, anchoCampo, alturaCampo, Color.rgb(40, 40, 60));
        String emailTexto = emailInput.isEmpty() ? "Email" : emailInput;
        g.drawText(emailTexto, xCampo + 10, inicioY + 22,
                emailInput.isEmpty() ? Color.rgb(150, 150, 150) : Color.WHITE, 14, false);

        // Campo Password
        g.drawRect(xCampo, inicioY + 45, anchoCampo, alturaCampo, Color.rgb(40, 40, 60));
        String passTexto = passwordInput.isEmpty() ? "Contraseña" : ocultarPassword(passwordInput);
        g.drawText(passTexto, xCampo + 10, inicioY + 67,
                passwordInput.isEmpty() ? Color.rgb(150, 150, 150) : Color.WHITE, 14, false);

        // Campo Nombre (solo en registro)
        if (modoActual == Modo.REGISTRO) {
            g.drawRect(xCampo, inicioY + 90, anchoCampo, alturaCampo, Color.rgb(40, 40, 60));
            String nombreTexto = nombreInput.isEmpty() ? "Nombre" : nombreInput;
            g.drawText(nombreTexto, xCampo + 10, inicioY + 112,
                    nombreInput.isEmpty() ? Color.rgb(150, 150, 150) : Color.WHITE, 14, false);
        }

        // Mensaje de error/éxito
        if (!mensajeError.isEmpty()) {
            int mensajeY = modoActual == Modo.REGISTRO ? 280 : 250;
            g.drawText(mensajeError, g.getWidth() / 2, mensajeY, colorMensaje, 13, true);
        }

        // Botones centrados
        int botonY = modoActual == Modo.REGISTRO ? 310 : 280;
        int anchoBoton = 120;
        int espacioEntreBotones = 20;
        int xBoton1 = (g.getWidth() / 2) - anchoBoton - (espacioEntreBotones / 2);
        int xBoton2 = (g.getWidth() / 2) + (espacioEntreBotones / 2);

        // Botón izquierdo (cambiar modo)
        String textoBotonCambio = (modoActual == Modo.LOGIN) ? "Registrarse" : "Iniciar Sesión";
        g.drawRect(xBoton1, botonY, anchoBoton, 35, Color.rgb(60, 60, 80));
        g.drawText(textoBotonCambio, xBoton1 + (anchoBoton / 2), botonY + 22, Color.WHITE, 12, true);

        // Botón derecho (acción principal)
        String textoBotonAccion = (modoActual == Modo.LOGIN) ? "ENTRAR" : "REGISTRAR";
        g.drawRect(xBoton2, botonY, anchoBoton, 35, Color.rgb(0, 120, 220));
        g.drawText(textoBotonAccion, xBoton2 + (anchoBoton / 2), botonY + 22, Color.WHITE, 13, true);
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