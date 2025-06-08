package com.tfg.samuraidash;

public class Datos {
    private String nombre;
    private int nivelMaxCompletado;
    private boolean tutorialCompletado;

    public Datos() {
        // Constructor vacío necesario para JSON
    }

    public Datos(String nombre, int nivelMaxCompletado, boolean tutorialCompletado) {
        this.nombre = nombre;
        this.nivelMaxCompletado = nivelMaxCompletado;
        this.tutorialCompletado = tutorialCompletado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isTutorialCompletado() {
        return tutorialCompletado;
    }

    public void setTutorialCompletado(boolean tutorialCompletado) {
        this.tutorialCompletado = tutorialCompletado;
    }

    public int getNivelMaxCompletado() {
        return nivelMaxCompletado;
    }

    public void setNivelMaxCompletado(int nivelMaxCompletado) {
        this.nivelMaxCompletado = nivelMaxCompletado;
    }
}
