package com.ldm.spaceDefenders.androidimpl;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.ldm.ejemplojuegopiratas.juego.R;
import com.ldm.spaceDefenders.Audio;
import com.ldm.spaceDefenders.FileIO;
import com.ldm.spaceDefenders.Juego;
import com.ldm.spaceDefenders.Graficos;
import com.ldm.spaceDefenders.Input;
import com.ldm.spaceDefenders.Pantalla;

public abstract class AndroidJuego extends Activity implements Juego {
    private AndroidFastRenderView renderView;
    private Graficos graficos;
    private Audio audio;
    private Input input;
    private FileIO fileIO;
    private Pantalla pantalla;
    private WakeLock wakeLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mantener la pantalla activa
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Determinar si la orientación es paisaje o retrato
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        int frameBufferWidth = isLandscape ? 480 : 320;
        int frameBufferHeight = isLandscape ? 320 : 480;

        // Crear el framebuffer
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Config.RGB_565);

        // Obtener dimensiones de pantalla modernas usando DisplayMetrics
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float scaleX = (float) frameBufferWidth / metrics.widthPixels;
        float scaleY = (float) frameBufferHeight / metrics.heightPixels;

        // Inicializar componentes
        renderView = new AndroidFastRenderView(this, frameBuffer);
        graficos = new AndroidGraficos(getAssets(), frameBuffer);
        fileIO = new AndroidFileIO(this); // Pasar el contexto
        audio = new AndroidAudio(this);
        input = new AndroidInput(this, renderView, scaleX, scaleY);
        pantalla = getStartScreen();

        // Establecer la vista de renderizado
        setContentView(renderView);

        // Configurar WakeLock con un nombre único
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.ldm.ejemplojuegopiratas:GLGame");
    }

    @Override
    public void onResume() {
        super.onResume();
        // Adquirir WakeLock con un timeout
        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire(10 * 60 * 1000L /* 10 minutos */);
        }
        pantalla.resume();
        renderView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Liberar WakeLock
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        renderView.pause();
        pantalla.pause();

        if (isFinishing()) {
            pantalla.dispose();
        }
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public FileIO getFileIO() {
        return fileIO;
    }

    @Override
    public Graficos getGraphics() {
        return graficos;
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public void setScreen(Pantalla pantalla) {
        if (pantalla == null) {
            throw new IllegalArgumentException("Pantalla no debe ser null");
        }
        this.pantalla.pause();
        this.pantalla.dispose();
        pantalla.resume();
        pantalla.update(0);
        this.pantalla = pantalla;
    }

    @Override
    public Pantalla getCurrentScreen() {
        return pantalla;
    }
}