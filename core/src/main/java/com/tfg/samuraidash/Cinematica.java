package com.tfg.samuraidash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class Cinematica {
    private List<Accion> acciones = new ArrayList<>();
    private int indiceAccion = 0;
    private Samurai samurai;
    private NPC npc;
    private boolean enCinematica = false;

    public Cinematica(Samurai samurai, NPC npc) {
        this.samurai = samurai;
        this.npc = npc;
    }

    public Cinematica(Samurai samurai) {
        this.samurai = samurai;
    }

    public boolean isEnCinematica() {
        return enCinematica;
    }

    // Agregar acciones a la cinemática
    public void agregarAccion(Accion accion) {
        acciones.add(accion);
    }

    public void setNpc(NPC npc) {
        this.npc = npc;
    }

    public void setSamurai(Samurai samurai) {
        this.samurai = samurai;
    }

    // Iniciar la cinemática
    public void iniciar() {
        enCinematica = true;
    }

    // Actualizar las acciones de la cinemática
    public void actualizar(float delta) {
        if (!enCinematica) return; // Si no estamos en cinematica, no hacemos nada

        if (indiceAccion < acciones.size()) {
            Accion accion = acciones.get(indiceAccion);
            accion.ejecutar(delta);

            // Si la acción ha terminado, avanzamos a la siguiente
            if (accion.terminada()) {
                indiceAccion++;
            }
        }

        // Si hemos completado todas las acciones, terminamos la cinemática
        if (indiceAccion >= acciones.size()) {
            enCinematica = false;
        }
    }

}
