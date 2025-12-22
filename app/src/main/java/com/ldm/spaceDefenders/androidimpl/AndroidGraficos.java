package com.ldm.spaceDefenders.androidimpl;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;

import com.ldm.spaceDefenders.Graficos;
import com.ldm.spaceDefenders.Pixmap;

// Clase que implementa la interfaz Graficos, utilizada para manejar gráficos en Android
public class AndroidGraficos implements Graficos {
    AssetManager assets;
    Bitmap frameBuffer;
    Canvas canvas;
    Paint paint;
    Rect srcRect = new Rect();
    Rect dstRect = new Rect();

    // Constructor que inicializa los recursos necesarios
    public AndroidGraficos(AssetManager assets, Bitmap frameBuffer) {
        this.assets = assets;
        this.frameBuffer = frameBuffer;
        this.canvas = new Canvas(frameBuffer);
        this.paint = new Paint();
    }

    // Método para cargar una nueva imagen (Pixmap)
    @Override
    public Pixmap newPixmap(String fileName, PixmapFormat format) {
        Config config;
        if (format == PixmapFormat.RGB565)
            config = Config.RGB_565;
        else if (format == PixmapFormat.ARGB4444)
            config = Config.ARGB_4444;
        else
            config = Config.ARGB_8888;

        Options options = new Options();
        options.inPreferredConfig = config;

        InputStream in = null;
        Bitmap bitmap;
        try {
            in = assets.open(fileName); // Abre el archivo desde los assets
            bitmap = BitmapFactory.decodeStream(in); // Decodifica la imagen
            if (bitmap == null) // Si no se pudo cargar, lanza una excepción
                throw new RuntimeException("No se ha podido cargar bitmap desde asset '" + fileName + "'");
        } catch (IOException e) {
            // Registro más robusto utilizando Log
            Log.e("AndroidGraficos", "Error al cargar el bitmap desde los assets: " + fileName, e);
            throw new RuntimeException("No se ha podido cargar bitmap desde asset '" + fileName + "'", e);
        } finally {
            if (in != null) { // Cierra el flujo de entrada
                try {
                    in.close();
                } catch (IOException e) {
                    // Registro del error al cerrar el flujo
                    Log.w("AndroidGraficos", "Error al cerrar el flujo de entrada para: " + fileName, e);
                }
            }
        }
        // Ajusta el formato del Pixmap según la configuración del Bitmap
        if (bitmap.getConfig() == Config.RGB_565)
            format = PixmapFormat.RGB565;
        else if (bitmap.getConfig() == Config.ARGB_4444)
            format = PixmapFormat.ARGB4444;
        else
            format = PixmapFormat.ARGB8888;

        return new AndroidPixmap(bitmap, format); // Devuelve el nuevo Pixmap
    }

    // Limpia el lienzo con un color especificado
    @Override
    public void clear(int color) {
        canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8,
                (color & 0xff));
    }

    // Dibuja un píxel en la posición especificada
    @Override
    public void drawPixel(int x, int y, int color) {
        paint.setColor(color);
        canvas.drawPoint(x, y, paint);
    }

    // Dibuja una línea entre dos puntos
    @Override
    public void drawLine(int x, int y, int x2, int y2, int color) {
        paint.setColor(color);
        canvas.drawLine(x, y, x2, y2, paint);
    }


    // Dibuja un rectángulo en la posición especificada
    @Override
    public void drawRect(int x, int y, int width, int height, int color) {
        paint.setColor(color);
        paint.setStyle(Style.FILL);
        canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
    }

    // Dibuja un Pixmap en una región específica del lienzo
    @Override
    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY,
                           int srcWidth, int srcHeight) {

        // Define las coordenadas de la región fuente
        srcRect.left = srcX;
        srcRect.top = srcY;
        srcRect.right = srcX + srcWidth - 1;
        srcRect.bottom = srcY + srcHeight - 1;

        // Define las coordenadas de la región destino
        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + srcWidth - 1;
        dstRect.bottom = y + srcHeight - 1;

        canvas.drawBitmap(((AndroidPixmap) pixmap).bitmap, srcRect, dstRect,
                null);
    }

    // Dibuja un Pixmap en la posición especificada
    @Override
    public void drawPixmap(Pixmap pixmap, int x, int y) {
        canvas.drawBitmap(((AndroidPixmap)pixmap).bitmap, x, y, null);
    }

    @Override
    public void drawText(String text, int x, int y, int color, int textSize, boolean centered) {
        paint.setColor(color);               // Establece el color del texto
        paint.setTextSize(textSize);         // Establece el tamaño del texto
        paint.setAntiAlias(true);            // Suaviza los bordes del texto

        if (centered) {
            float textWidth = paint.measureText(text); // Calcula el ancho del texto
            x -= (int) (textWidth / 2);                        // Ajusta la posición para centrarlo horizontalmente
        }

        // Dibuja el texto en el canvas
        canvas.drawText(text, x, y, paint);
    }

    @Override
    public int getWidth() {
        return frameBuffer.getWidth();
    }

    @Override
    public int getHeight() {
        return frameBuffer.getHeight();
    }
}
