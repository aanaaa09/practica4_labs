package com.ldm.lluviaDeChuches.androidimpl;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import com.ldm.lluviaDeChuches.Audio;
import com.ldm.lluviaDeChuches.Sonido;


public class AndroidAudio implements Audio {
    AssetManager assets;
    SoundPool soundPool;

    public AndroidAudio(Activity activity) {
        // Configurar el flujo de volumen para controlar el volumen multimedia
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Obtener el gestor de recursos de los assets
        this.assets = activity.getAssets();

        // Configurar los atributos de audio
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA) // Uso multimedia
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) // Tipo de contenido
                .build();

        // Crear la instancia de SoundPool.Builder
        this.soundPool = new SoundPool.Builder()
                .setMaxStreams(20) // Número máximo de streams
                .setAudioAttributes(audioAttributes) // Atributos de audio
                .build();
    }

    @Override
    public Sonido nuevoSonido(String filename) {
        try {
            AssetFileDescriptor assetDescriptor = assets.openFd(filename);
            int soundId = soundPool.load(assetDescriptor, 0);
            return new AndroidSonido(soundPool, soundId);
        } catch (IOException e) {
            throw new RuntimeException("No se ha podido cargar archivo '" + filename + "'");
        }
    }
}

