package com.tfg.samuraidash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tfg.samuraidash.Niveles.lobby.Lobby;

public class PantallaVictoria implements Screen {
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont fontTitulo;
    private BitmapFont fontTexto;
    private Texture whitePixel;
    private float tiempoTranscurrido = 0;
    private ShapeRenderer shapeRenderer;

    // Estados de animación
    private enum EstadoAnimacion { ENTRADA, MOSTRANDO, SALIDA }
    private EstadoAnimacion estadoActual = EstadoAnimacion.ENTRADA;
    private float tiempoAnimacion = 0;
    private final float DURACION_ENTRADA = 1.2f;
    private final float DURACION_SALIDA = 0.8f;
    private boolean transicionCompletada = false;

    // Referencias a elementos interfaz
    private Vector2 posicionOriginalStatsTable;
    private Label tituloLabel;
    private Label continuarLabel;
    private Table fondo;
    private Table statsTable;
    private Table mainTable;
    private Music musicaFondo;

    // Sistema de partículas
    private class Particula {
        Vector2 posicion;
        Vector2 velocidad;
        float tamaño;
        float rotacion;
        Color color;
        float vida;
        float vidaMaxima;

        public Particula(boolean explosion) {
            if (explosion) {
                // Para explosión inicial
                posicion = new Vector2(320, 240);
                velocidad = new Vector2(
                    MathUtils.random(-300, 300),
                    MathUtils.random(-300, 300)
                );
                tamaño = MathUtils.random(1.5f, 3f);
                vida = vidaMaxima = MathUtils.random(0.8f, 1.2f);
                color = new Color(
                    MathUtils.random(0.8f, 1f),
                    MathUtils.random(0.6f, 0.8f),
                    MathUtils.random(0.1f, 0.3f),
                    1
                );
            } else {
                // Para partículas normales
                posicion = new Vector2(
                    MathUtils.random(0, 640),
                    MathUtils.random(-100, 480)
                );
                velocidad = new Vector2(0, MathUtils.random(1f, 3f));
                tamaño = MathUtils.random(0.5f, 2f);
                rotacion = MathUtils.random(0, 360);
                color = Color.YELLOW;
            }
        }
    }

    private Array<Particula> particulas = new Array<>();
    private Array<Particula> particulasExplosion = new Array<>();
    private TextureRegion estrellaRegion;

    public PantallaVictoria(float tiempoNivel, int enemigosDerrotados, int muertes, int comboMaximo) {
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        shapeRenderer = new ShapeRenderer();

        // Crear textura blanca
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();

        // Configurar fuentes
        fontTitulo = new BitmapFont(Gdx.files.internal("fonts/fuentehiero.fnt"));
        fontTexto = new BitmapFont(Gdx.files.internal("fonts/fuentehiero.fnt"));
        fontTitulo.getData().setScale(1.5f);
        fontTexto.getData().setScale(0.9f);

        // Crear textura de estrella
        estrellaRegion = crearTexturaEstrella();

        // Crear partículas normales
        for (int i = 0; i < 30; i++) {
            particulas.add(new Particula(false));
        }

        // Crear partículas para explosión inicial
        for (int i = 0; i < 60; i++) {
            particulasExplosion.add(new Particula(true));
        }

        crearUI(tiempoNivel, enemigosDerrotados, muertes, comboMaximo);
    }

    private TextureRegion crearTexturaEstrella() {
        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.YELLOW);

        // Dibujar estrella simple
        int[] starX = {8, 10, 16, 11, 13, 8, 3, 5, 0, 6};
        int[] starY = {0, 6, 6, 10, 16, 12, 16, 10, 6, 6};

        for (int i = 0; i < starX.length; i++) {
            int next = (i + 1) % starX.length;
            pixmap.drawLine(starX[i], starY[i], starX[next], starY[next]);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegion(texture);
    }

    private void crearUI(float tiempo, int enemigos, int muertes, int combo) {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        // Estilos
        Label.LabelStyle estiloTitulo = new Label.LabelStyle(fontTitulo, new Color(0.98f, 0.82f, 0.18f, 1));
        Label.LabelStyle estiloTexto = new Label.LabelStyle(fontTexto, new Color(0.8f, 0.8f, 0.8f, 1));
        Label.LabelStyle estiloValor = new Label.LabelStyle(fontTexto, new Color(1, 1, 1, 1));

        // Fondo decorativo
        fondo = new Table();
        fondo.setBackground(new TextureRegionDrawable(new TextureRegion(whitePixel)));
        fondo.setColor(0.1f, 0.1f, 0.15f, 0.9f);
        fondo.setSize(580, 360);
        fondo.setPosition(30, 60);
        stage.addActor(fondo);

        // Título
        tituloLabel = new Label("¡VICTORIA!", estiloTitulo);
        tituloLabel.setAlignment(Align.center);

        // Tabla de estadísticas
        statsTable = new Table();
        statsTable.defaults().pad(8).left();

        String[] textos = {
            "TIEMPO: %.1f s",
            "ENEMIGOS: %d",
            "MUERTES: %d",
            "COMBO: x%d"
        };
        Object[] valores = {tiempo, enemigos, muertes, combo};

        for (int i = 0; i < textos.length; i++) {
            Label labelTexto = new Label(textos[i].split(":")[0] + ":", estiloTexto);
            Label labelValor = new Label(String.format(textos[i].split(":")[1], valores[i]), estiloValor);

            statsTable.add(labelTexto).width(200).right().padRight(20);
            statsTable.add(labelValor).left().row();
        }

        // Texto para continuar
        continuarLabel = new Label("PRESIONA [ESPACIO]", estiloTexto);
        continuarLabel.setAlignment(Align.center);

        // Organización
        mainTable.add(tituloLabel).padBottom(30).row();
        mainTable.add(statsTable).padBottom(30).row();
        mainTable.add(continuarLabel).padTop(20);

        stage.addActor(mainTable);

        // Guardar posición original después del layout
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                posicionOriginalStatsTable = new Vector2(statsTable.getX(), statsTable.getY());
                configurarAnimacionEntrada();
            }
        });
    }

    private void configurarAnimacionEntrada() {
        // Título invisible y escalado inicial
        tituloLabel.setColor(1, 1, 1, 0);
        tituloLabel.setFontScale(3f);

        // Fondo transparente y pequeño
        fondo.setColor(0.1f, 0.1f, 0.15f, 0);
        fondo.setScale(0.8f);

        // Mover la tabla de estadísticas hacia abajo
        if (posicionOriginalStatsTable != null) {
            statsTable.setPosition(posicionOriginalStatsTable.x, posicionOriginalStatsTable.y - 100);
            statsTable.setColor(1, 1, 1, 0);
        }

        // Ocultar texto de continuar inicialmente
        continuarLabel.setColor(1, 1, 1, 0);
    }

    @Override
    public void render(float delta) {
        tiempoTranscurrido += delta;

        // Actualizar lógica de animación
        if (estadoActual == EstadoAnimacion.ENTRADA) {
            tiempoAnimacion += delta;
            actualizarAnimacionEntrada();

            if (tiempoAnimacion >= DURACION_ENTRADA) {
                estadoActual = EstadoAnimacion.MOSTRANDO;
                tiempoAnimacion = 0;
            }
        } else if (estadoActual == EstadoAnimacion.SALIDA) {
            tiempoAnimacion += delta;
            actualizarAnimacionSalida();

            if (tiempoAnimacion >= DURACION_SALIDA && !transicionCompletada) {
                transicionCompletada = true;
                Game game = (Game) Gdx.app.getApplicationListener();
                game.setScreen(new Lobby());
            }
        }

        // Limpiar pantalla
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizar partículas normales
        for (Particula particula : particulas) {
            particula.posicion.y += particula.velocidad.y;
            particula.rotacion += particula.velocidad.y * 10 * delta;

            if (particula.posicion.y > 480) {
                particula.posicion.set(
                    MathUtils.random(0, 640),
                    MathUtils.random(-100, -10)
                );
                particula.velocidad.y = MathUtils.random(1f, 3f);
            }
        }

        // Actualizar partículas de explosión
        for (int i = particulasExplosion.size - 1; i >= 0; i--) {
            Particula p = particulasExplosion.get(i);
            p.posicion.x += p.velocidad.x * delta;
            p.posicion.y += p.velocidad.y * delta;
            p.vida -= delta;

            if (p.vida <= 0) {
                particulasExplosion.removeIndex(i);
            } else {
                p.color.a = p.vida / p.vidaMaxima;
            }
        }

        // Dibujar partículas de explosión
        batch.begin();
        for (Particula particula : particulasExplosion) {
            batch.setColor(particula.color);
            batch.draw(
                estrellaRegion,
                particula.posicion.x, particula.posicion.y,
                8, 8,
                16 * particula.tamaño, 16 * particula.tamaño,
                1, 1,
                particula.rotacion
            );
        }

        // Dibujar partículas normales
        for (Particula particula : particulas) {
            float alpha = MathUtils.random(0.3f, 0.8f);
            batch.setColor(1, 1, 1, alpha);
            batch.draw(
                estrellaRegion,
                particula.posicion.x, particula.posicion.y,
                8, 8,
                16 * particula.tamaño, 16 * particula.tamaño,
                1, 1,
                particula.rotacion
            );
        }
        batch.setColor(Color.WHITE);
        batch.end();

        // Animaciones solo cuando se está mostrando
        if (estadoActual == EstadoAnimacion.MOSTRANDO) {
            // Animación del título
            float pulse = (float) (Math.sin(tiempoTranscurrido * 3) * 0.2f + 0.8f);
            tituloLabel.setColor(1, pulse, pulse * 0.5f, 1);

            // Animación del texto continuar
            if (tiempoTranscurrido > 1.5f) {
                float blink = (float) (Math.sin(tiempoTranscurrido * 3) * 0.3f + 0.7f);
                continuarLabel.setColor(1, 1, 1, blink);
            }
        }

        // Dibujar interfaz
        stage.act(delta);
        stage.draw();

        // Comprobar entrada
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && estadoActual != EstadoAnimacion.SALIDA) {
            musicaFondo.stop();
            estadoActual = EstadoAnimacion.SALIDA;
            tiempoAnimacion = 0;

            // Añadir partículas de salida
            for (int i = 0; i < 40; i++) {
                particulasExplosion.add(new Particula(true));
            }
        }
    }

    private void actualizarAnimacionEntrada() {
        float progreso = Math.min(1, tiempoAnimacion / DURACION_ENTRADA);
        float easedProgreso = (float) Math.pow(progreso, 0.7);

        // Animación del título
        tituloLabel.setColor(1, 1, 1, easedProgreso);
        tituloLabel.setFontScale(1.5f + (3f - 1.5f) * (1 - easedProgreso));

        // Animación del fondo
        fondo.setColor(0.1f, 0.1f, 0.15f, 0.9f * easedProgreso);
        fondo.setScale(0.8f + 0.2f * easedProgreso);

        // Animación de la tabla de estadísticas
        if (posicionOriginalStatsTable != null) {
            statsTable.setColor(1, 1, 1, easedProgreso);
            statsTable.setPosition(
                posicionOriginalStatsTable.x,
                posicionOriginalStatsTable.y - 100 + (100 * easedProgreso)
            );
        }

        // Animación del texto continuar
        if (progreso > 0.7f) {
            float continuarProgreso = (progreso - 0.7f) / 0.3f;
            continuarLabel.setColor(1, 1, 1, continuarProgreso);
        }
    }

    private void actualizarAnimacionSalida() {
        float progreso = Math.min(1, tiempoAnimacion / DURACION_SALIDA);
        float easedProgreso = (float) Math.pow(progreso, 0.5);

        // Animación de desvanecimiento general
        float alpha = 1 - easedProgreso;
        tituloLabel.setColor(1, 1, 1, alpha);
        fondo.setColor(0.1f, 0.1f, 0.15f, 0.9f * alpha);
        statsTable.setColor(1, 1, 1, alpha);
        continuarLabel.setColor(1, 1, 1, alpha);

        fondo.setScale(1 - 0.2f * easedProgreso);

        // Mover todo hacia arriba
        float desplazamiento = 50 * easedProgreso;
        tituloLabel.moveBy(0, desplazamiento);
        statsTable.moveBy(0, desplazamiento);
        continuarLabel.moveBy(0, desplazamiento);
    }

    @Override
    public void show() {
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica/victoria.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.3f);
        musicaFondo.play();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        whitePixel.dispose();
        fontTitulo.dispose();
        fontTexto.dispose();
        estrellaRegion.getTexture().dispose();
        musicaFondo.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
