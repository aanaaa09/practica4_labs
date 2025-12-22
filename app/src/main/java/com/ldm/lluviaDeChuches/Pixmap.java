package com.ldm.lluviaDeChuches;

import com.ldm.lluviaDeChuches.Graficos.PixmapFormat;

public interface Pixmap {
    int getWidth();

    int getHeight();

    PixmapFormat getFormat();

    void dispose();
}

