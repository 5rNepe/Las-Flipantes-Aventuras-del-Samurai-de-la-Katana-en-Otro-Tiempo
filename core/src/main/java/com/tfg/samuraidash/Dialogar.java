package com.tfg.samuraidash;

public class Dialogar implements Accion {
    private NPC npc;
    private boolean terminada = false;

    public Dialogar(NPC npc) {
        this.npc = npc;
    }

    @Override
    public void ejecutar(float delta) {
        // Solo iniciar el diálogo si no está en progreso
        if (!npc.dialogo.isInProgress() && !npc.dialogo.hasStarted()) {
            npc.dialogo.start();  // Iniciar el diálogo si no está en progreso y no ha comenzado antes
        }

        // Actualizar el diálogo, pero solo si está en progreso
        if (npc.dialogo.isInProgress()) {
            npc.dialogo.update(delta);  // Actualizar el diálogo con el tiempo transcurrido
        }

        // Si el diálogo ha terminado, marcar la acción como terminada
        if (!npc.dialogo.isInProgress()) {
            terminada = true;
        }
    }

    @Override
    public boolean terminada() {
        return terminada;  // Retorna si el diálogo ha terminado
    }
}
