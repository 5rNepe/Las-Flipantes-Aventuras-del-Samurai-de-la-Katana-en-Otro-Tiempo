package com.tfg.samuraidash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class CambiarPantalla implements Accion {
    private Screen nuevaPantalla;  // La nueva pantalla a la que se cambiará
    private Game game;  // Referencia al juego para cambiar de pantalla
    private boolean terminada = false;  // Estado de la acción

    public CambiarPantalla(Game game, Screen nuevaPantalla) {
        this.game = game;
        this.nuevaPantalla = nuevaPantalla;
    }

    @Override
    public void ejecutar(float delta) {
        // Cambiar de pantalla
        game.setScreen(nuevaPantalla);  // Establecemos la nueva pantalla
        terminada = true;  // Marcamos la acción como terminada
    }

    @Override
    public boolean terminada() {
        return terminada;  // La acción se considera terminada tan pronto como cambiamos de pantalla
    }
}

