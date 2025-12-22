package com.ldm.spaceDefenders.androidimpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AndroidFastRenderView extends SurfaceView implements Runnable {
    AndroidJuego juego;
    Bitmap framebuffer;
    Thread renderThread = null;
    SurfaceHolder holder;
    volatile boolean running = false;

    // Constructor usado para instancias programáticas
    public AndroidFastRenderView(Context context) {
        super(context);
    }

    // Constructor usado cuando se define la vista en XML
    public AndroidFastRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Constructor usado cuando se define la vista en XML con estilos personalizados
    public AndroidFastRenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //Constructor personalizado
    public AndroidFastRenderView(AndroidJuego juego, Bitmap framebuffer) {
        super(juego);
        this.juego = juego;
        this.framebuffer = framebuffer;
        this.holder = getHolder();
    }

    public void resume() {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    public void run() {
        Rect dstRect = new Rect();
        long startTime = System.nanoTime();
        while(running) {
            if(!holder.getSurface().isValid())
                continue;

            float deltaTime = (System.nanoTime()-startTime) / 1000000000.0f;
            startTime = System.nanoTime();

            juego.getCurrentScreen().update(deltaTime);
            juego.getCurrentScreen().present(deltaTime);

            Canvas canvas = holder.lockCanvas();
            canvas.getClipBounds(dstRect);
            canvas.drawBitmap(framebuffer, null, dstRect, null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        running = false;
        while(true) {
            try {
                renderThread.join();
                break;
            } catch (InterruptedException e) {
                // retry
            }
        }
    }
}