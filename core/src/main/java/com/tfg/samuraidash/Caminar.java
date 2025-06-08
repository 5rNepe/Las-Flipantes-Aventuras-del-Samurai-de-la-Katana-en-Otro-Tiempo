package com.tfg.samuraidash;

public class Caminar implements Accion {
    private Samurai samurai;
    private float tiempo;  // El tiempo que ha transcurrido desde que empezó la acción
    private float duracion;  // Cuánto tiempo debe caminar el samurái
    private boolean terminada = false;  // Estado de la acción
    private boolean izquierda;

    public Caminar(Samurai samurai, float duracion, boolean izquierda) {
        this.samurai = samurai;
        this.duracion = duracion;
        this.izquierda = izquierda;
    }

    @Override
    public void ejecutar(float delta) {
        if(izquierda) {
            if (tiempo < duracion) {
                tiempo += delta;  // Aumentamos el tiempo transcurrido
                samurai.setRunning(true);  // El samurái sigue caminando
                samurai.setFacingLeft(true);
            } else {
                samurai.setRunning(false);  // El samurái se detiene
                terminada = true;  // La acción ha terminado
            }
        }
        else {
            if (tiempo < duracion) {
                tiempo += delta;  // Aumentamos el tiempo transcurrido
                samurai.setRunning(true);  // El samurái sigue caminando
            } else {
                samurai.setRunning(false);  // El samurái se detiene
                terminada = true;  // La acción ha terminado
            }
        }
    }

    @Override
    public boolean terminada() {
        return terminada;  // Retorna si la acción ha terminado
    }
}
