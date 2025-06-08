package com.tfg.samuraidash;

public interface Accion {
    // Método que se ejecuta cada frame
    void ejecutar(float delta);

    // Método que indica si la acción ha terminado
    boolean terminada();
}

