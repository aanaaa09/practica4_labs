package com.ldm.spaceDefenders;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileIO {
    InputStream leerAsset(String nombreArchivo) throws IOException;

    InputStream leerArchivo(String nombreArchivo) throws IOException;

    OutputStream escribirArchivo(String nombreArchivo) throws IOException;

}

