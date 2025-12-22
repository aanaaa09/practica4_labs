package com.ldm.spaceDefenders;

import com.ldm.spaceDefenders.Graficos.PixmapFormat;

public interface Pixmap {
    int getWidth();

    int getHeight();

    PixmapFormat getFormat();

    void dispose();
}

