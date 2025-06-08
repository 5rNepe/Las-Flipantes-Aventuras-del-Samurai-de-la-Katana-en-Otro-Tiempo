package com.tfg.samuraidash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;

public class Dialogo {
    private String npcNombre;
    private Array<String> textosDialogo;
    private int textoIndex;
    private String currentText;
    private float timeSinceLastChar;
    private boolean isInProgress;
    private boolean canProceed;
    private boolean started;
    private Sound sonidoDialogo;


    public Dialogo(String npcNombre, Array<String> textosDialogo) {
        this.npcNombre = npcNombre;
        this.textosDialogo = textosDialogo;
        this.textoIndex = 0;
        this.isInProgress = false;
        this.canProceed = false;
        sonidoDialogo = Gdx.audio.newSound(Gdx.files.internal("Samurai/dialogo.mp3"));
    }

    // Comienza el diálogo
    public void start() {
        started = true;
        isInProgress = true;
        currentText = "";
        timeSinceLastChar = 0;
        textoIndex = 0;
        sonidoDialogo.play();
    }

    // Actualiza el diálogo, añadiendo más texto si es necesario
    public void update(float deltaTime) {
        if (isInProgress) {
            // Aumentar el tiempo para mostrar cada letra
            timeSinceLastChar += deltaTime;

            // Si el tiempo ha superado un umbral, añadimos una letra al texto
            if (timeSinceLastChar > 0.05f && currentText.length() < textosDialogo.get(textoIndex).length()) {
                currentText += textosDialogo.get(textoIndex).charAt(currentText.length());
                timeSinceLastChar = 0;
            }

            // Si hemos mostrado todo el texto, podemos continuar
            if (currentText.length() == textosDialogo.get(textoIndex).length()) {
                canProceed = true;
            }
        }
    }

    // Avanzar al siguiente texto del diálogo
    public void nextText() {
        if (canProceed) {
            textoIndex++;
            if (textoIndex < textosDialogo.size) {
                sonidoDialogo.play();
                currentText = "";
                canProceed = false;  // Resetear para el siguiente texto
            } else {
                isInProgress = false;  // Fin del diálogo
            }
        }
    }

    // Método que verifica si se puede proceder al siguiente texto
    public boolean canProceed() {
        return canProceed;
    }

    public String getNpcNombre() {
        return npcNombre;
    }

    public String getCurrentText() {
        return currentText;
    }

    public boolean isInProgress() {
        return isInProgress;
    }

    public boolean hasStarted() {
        return started;
    }
}
