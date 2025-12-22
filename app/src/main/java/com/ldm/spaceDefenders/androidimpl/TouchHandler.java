package com.ldm.spaceDefenders.androidimpl;

import java.util.List;

import android.view.View.OnTouchListener;

import com.ldm.spaceDefenders.Input.TouchEvent;


public interface TouchHandler extends OnTouchListener {
    boolean isTouchDown(int pointer);

    int getTouchX(int pointer);

    int getTouchY(int pointer);

    List<TouchEvent> getTouchEvents();
}

