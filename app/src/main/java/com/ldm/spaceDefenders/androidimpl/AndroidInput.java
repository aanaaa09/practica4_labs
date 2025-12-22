package com.ldm.spaceDefenders.androidimpl;

import java.util.List;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.ldm.spaceDefenders.Input;

public class AndroidInput implements Input {
    AccelerometerHandler accelHandler;
    KeyboardHandler keyHandler;
    TouchHandler touchHandler;

    public AndroidInput(Context context, View view, float scaleX, float scaleY) {
        accelHandler = new AccelerometerHandler(context);
        keyHandler = new KeyboardHandler(view);
        touchHandler = new MultiTouchHandler(view, scaleX, scaleY); // Siempre usa MultiTouchHandler
    }

    @Override
    public boolean isKeyPressed(int keyCode) {
        return keyHandler.isKeyPressed(keyCode);
    }

    @Override
    public boolean isTouchDown(int pointer) {
        return touchHandler.isTouchDown(pointer);
    }

    @Override
    public int getTouchX(int pointer) {
        return touchHandler.getTouchX(pointer);
    }

    @Override
    public int getTouchY(int pointer) {
        return touchHandler.getTouchY(pointer);
    }

    @Override
    public float getAccelX() {
        return accelHandler.getAccelX();
    }

    @Override
    public float getAccelY() {
        return accelHandler.getAccelY();
    }

    @Override
    public float getAccelZ() {
        return accelHandler.getAccelZ();
    }

    @Override
    @NonNull // Este método no puede devolver un valor nulo
    public List<TouchEvent> getTouchEvents() {
        return touchHandler.getTouchEvents();
    }

    @Override
    @NonNull // Este método no puede devolver un valor nulo
    public List<KeyEvent> getKeyEvents() {
        return keyHandler.getKeyEvents();
    }
}

