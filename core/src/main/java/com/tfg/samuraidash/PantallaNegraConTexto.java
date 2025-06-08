package com.tfg.samuraidash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.List;

public class PantallaNegraConTexto implements Accion {
    private float tiempo;  // Tiempo total de la acción
    private float duracion;  // Duración total que el texto estará en pantalla
    private String textoCompleto = "";  // El texto completo que se debe mostrar
    private String textoActual = "";  // El texto que se está mostrando (se va llenando poco a poco)
    private int textoIndex = 0;  // Índice para cambiar de texto
    private List<String> textosDialogo;  // Lista de textos a mostrar
    private BitmapFont font;  // Fuente para el texto
    private SpriteBatch batch;  // Para dibujar el texto
    private GlyphLayout layout;  // Para calcular el tamaño del texto
    private float timeSinceLastChar = 0;  // Tiempo transcurrido desde la última letra
    private float delayEntreCaracteres = 0.05f;  // Retraso entre cada letra
    private float tiempoMostrarTexto = 0;  // Tiempo que el texto actual ha sido mostrado
    private float tiempoDesvanecimiento = 2f;  // Tiempo para desvanecer el texto
    private float opacidad = 1f;  // La opacidad del texto
    private boolean pantallaNegra = true;  // Estado de la pantalla (si está en negro o no)

    public PantallaNegraConTexto(float duracion, List<String> textosDialogo) {
        // Verificar si la lista de textos no va a dar error
        if (textosDialogo == null || textosDialogo.isEmpty()) {
            throw new IllegalArgumentException("La lista de textos no puede estar vacía.");
        }

        this.duracion = duracion;
        this.textosDialogo = textosDialogo;
        this.font = new BitmapFont();  // Fuente por defecto, puedes usar una fuente personalizada si lo deseas
        this.batch = new SpriteBatch();
        this.layout = new GlyphLayout();

        // Inicializar textoCompleto con el primer texto de la lista
        this.textoCompleto = textosDialogo.get(0);
    }

    @Override
    public void ejecutar(float delta) {
        tiempo += delta;  // Aumentamos el tiempo transcurrido

        // Si aún estamos en la pantalla negra, la dibujamos
        if (pantallaNegra) {
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f);  // Color negro
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }

        // Establecer el color del texto a blanco
        font.setColor(1f, 1f, 1f, opacidad);

        timeSinceLastChar += delta;  // Aumentamos el tiempo transcurrido desde la ultima letra

        // Si ha pasado el tiempo suficiente y aún no hemos terminado de escribir el texto
        if (timeSinceLastChar > delayEntreCaracteres && textoActual.length() < textoCompleto.length()) {
            // Agregar la siguiente letra al texto actual
            textoActual += textoCompleto.charAt(textoActual.length());
            timeSinceLastChar = 0;  // Reiniciar el contador
        }

        // Si hemos terminado de escribir el texto actual, aumentamos el tiempo que ha sido mostrado
        if (textoActual.length() == textoCompleto.length()) {
            tiempoMostrarTexto += delta;
        }

        if (textoActual.length() == textoCompleto.length() && tiempoMostrarTexto >= duracion) {
            // Desvanecemos el texto después de que se ha mostrado el tiempo necesario
            opacidad -= delta / tiempoDesvanecimiento;  // Reducir la opacidad con el tiempo
            if (opacidad <= 0f) {
                opacidad = 0f;
            }
        }

        if (opacidad == 0f && textoIndex < textosDialogo.size() - 1) {
            textoIndex++;
            textoCompleto = textosDialogo.get(textoIndex);  // Obtener el siguiente texto
            textoActual = "";  // Reiniciar el texto actual
            tiempoMostrarTexto = 0;  // Reiniciar el tiempo de mostrar el texto
            tiempo = 0;  // Reiniciar el tiempo total
            opacidad = 1f;  // Restaurar la opacidad al máximo
        }

        if (textoIndex == textosDialogo.size() - 1 && opacidad == 0f) {
            pantallaNegra = false;  // La pantalla ya no es negra
        }

        // Calcular el tamaño del texto con GlyphLayout para centrarlo
        layout.setText(font, textoActual);
        float x = Gdx.graphics.getWidth() / 2 - layout.width / 2;
        float y = Gdx.graphics.getHeight() / 2 + layout.height / 2;

        // Dibujar el texto en la pantalla
        batch.begin();
        font.draw(batch, textoActual, x, y);  // Muestra el texto centrado
        batch.end();
    }

    @Override
    public boolean terminada() {
        return textoIndex >= textosDialogo.size() - 1 && opacidad == 0f;  // La acción termina cuando todos los textos se han mostrado y desvanecido
    }
}
