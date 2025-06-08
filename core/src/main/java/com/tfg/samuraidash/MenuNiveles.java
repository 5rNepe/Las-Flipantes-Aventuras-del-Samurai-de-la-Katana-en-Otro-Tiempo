package com.tfg.samuraidash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tfg.samuraidash.Niveles.lobby.Lobby;
import com.tfg.samuraidash.Niveles.nivel.Nivel1;
import com.tfg.samuraidash.Niveles.nivel.Nivel2;
import com.tfg.samuraidash.Niveles.nivel.Nivel3;
import com.tfg.samuraidash.Niveles.nivel.Nivel4;
import com.tfg.samuraidash.Niveles.tutorial.NivelTutorial;
import com.tfg.samuraidash.Niveles.tutorial.NivelTutorialFinal;

public class MenuNiveles {
    private Stage stage;
    private Table mainTable;
    private BitmapFont font;
    private boolean visible = false;
    private Texture fondo;
    private Texture buttonTexture;
    private Texture buttonPressedTexture;
    private Texture lockedButtonTexture;
    private Texture completedTexture;

    private Datos datosPartida;
    private int nivelMaxDesbloqueado;

    private static final int BUTTON_WIDTH = 290;
    private static final int BUTTON_HEIGHT = 75;
    private static final int COMPLETED_ICON_SIZE = 22;
    private static final int PADDING = 10;

    public MenuNiveles() {
        stage = new Stage(new FitViewport(640, 480));
        cargarDatosPartida();
        crearRecursos();
        crearUI();
    }

    private void cargarDatosPartida() {
        Preferences prefs = Gdx.app.getPreferences("GameSettings");
        int selectedSlot = prefs.getInteger("selectedSlot", 0);

        FileHandle file = Gdx.files.local("partida" + (selectedSlot + 1) + ".json");
        if (file.exists()) {
            String json = file.readString();
            datosPartida = new Json().fromJson(Datos.class, json);
            nivelMaxDesbloqueado = datosPartida.getNivelMaxCompletado();
        } else {
            nivelMaxDesbloqueado = 0;
        }
    }

    private void crearRecursos() {
        font = new BitmapFont(Gdx.files.internal("fonts/fuentehiero2.fnt"));
        fondo = new Texture(Gdx.files.internal("Overlays/9.png"));

        buttonTexture = crearTexturaBoton(BUTTON_WIDTH, BUTTON_HEIGHT,
            new Color(0.1f, 0.1f, 0.1f, 0.9f),
            new Color(0.98f, 0.82f, 0.18f, 1));

        buttonPressedTexture = crearTexturaBoton(BUTTON_WIDTH, BUTTON_HEIGHT,
            new Color(0.15f, 0.15f, 0.15f, 0.9f),
            new Color(0.98f, 0.92f, 0.38f, 1));

        lockedButtonTexture = crearTexturaBoton(BUTTON_WIDTH, BUTTON_HEIGHT,
            new Color(0.2f, 0.2f, 0.2f, 0.7f),
            new Color(0.4f, 0.4f, 0.4f, 1));

        completedTexture = crearTexturaCompletado(COMPLETED_ICON_SIZE, COMPLETED_ICON_SIZE);
    }

    private Texture crearTexturaBoton(int width, int height, Color colorFondo, Color colorBorde) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        // Relleno con degradado
        for (int y = 0; y < height; y++) {
            float factor = y / (float)height;
            Color c = new Color(
                colorFondo.r * (0.9f + 0.1f * factor),
                colorFondo.g * (0.9f + 0.1f * factor),
                colorFondo.b * (0.9f + 0.1f * factor),
                colorFondo.a
            );
            pixmap.setColor(c);
            pixmap.drawLine(0, y, width, y);
        }

        // Borde
        pixmap.setColor(colorBorde);
        pixmap.drawRectangle(0, 0, width, height);
        pixmap.drawRectangle(1, 1, width-2, height-2);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture crearTexturaCompletado(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.8f, 0.2f, 0.9f));
        pixmap.fillCircle(width/2, height/2, width/2);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void crearUI() {
        // Estilo para botones
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = new Color(0.98f, 0.82f, 0.18f, 1);
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonPressedTexture));
        buttonStyle.over = new TextureRegionDrawable(new TextureRegion(buttonPressedTexture));

        // Estilo para botones bloqueados
        TextButton.TextButtonStyle lockedButtonStyle = new TextButton.TextButtonStyle();
        lockedButtonStyle.font = font;
        lockedButtonStyle.fontColor = new Color(0.6f, 0.6f, 0.6f, 1);
        lockedButtonStyle.up = new TextureRegionDrawable(new TextureRegion(lockedButtonTexture));

        // Estilo para el título
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = font;
        titleStyle.fontColor = new Color(0.98f, 0.82f, 0.18f, 1);

        // Configurar tabla principal
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(PADDING);

        // Fondo semitransparente
        Image fondoImage = new Image(fondo);
        fondoImage.setColor(0.1f, 0.1f, 0.1f, 0.85f);
        fondoImage.setSize(640, 480);
        stage.addActor(fondoImage);
        stage.addActor(mainTable);

        // Título
        Label titulo = new Label("SELECCIONAR NIVEL", titleStyle);
        titulo.setAlignment(Align.center);
        mainTable.add(titulo).colspan(2).padBottom(15).row();

        // Niveles disponibles
        String[] nombresNiveles = {
            "TUTORIAL",
            "NIVEL 1: VESTIGIOS DEL FUTURO",
            "NIVEL 2: INVIERNO PERPETUO",
            "NIVEL 3: EL INICIO DEL OCASO",
            "NIVEL 4: PORTAL DEL TIEMPO"
        };

        // Tabla para los niveles
        Table nivelesTable = new Table();
        nivelesTable.pad(5);

        for (int i = 0; i < nombresNiveles.length; i++) {
            final int nivel = i;
            boolean desbloqueado = (i == 0) || (i <= nivelMaxDesbloqueado + 1);
            boolean completado = (i <= nivelMaxDesbloqueado);

            // Contenedor para cada nivel
            Table nivelContainer = new Table();

            // Crear botón
            TextButton nivelBtn;
            if (desbloqueado) {
                nivelBtn = new TextButton(nombresNiveles[i], buttonStyle);
                nivelBtn.getLabel().setAlignment(Align.center);
                nivelBtn.getLabel().setWrap(true);
                nivelBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        seleccionarNivel(nivel);
                    }
                });
            } else {
                nivelBtn = new TextButton("BLOQUEADO", lockedButtonStyle);
                nivelBtn.getLabel().setAlignment(Align.center);
            }

            // Añadir botón e indicador
            nivelContainer.add(nivelBtn).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(3);

            if (completado) {
                Image completadoImg = new Image(completedTexture);
                nivelContainer.add(completadoImg).size(COMPLETED_ICON_SIZE).padLeft(5);
            }

            // Añadir a la tabla
            if (i % 2 == 0) {
                nivelesTable.add(nivelContainer);
            } else {
                nivelesTable.add(nivelContainer).row();
            }
        }

        // Ajustar si hay número impar de niveles
        if (nombresNiveles.length % 2 != 0) {
            nivelesTable.add().width(BUTTON_WIDTH).height(BUTTON_HEIGHT).row();
        }

        mainTable.add(nivelesTable).colspan(2).row();

        // Botón de volver
        TextButton volverBtn = new TextButton("VOLVER", buttonStyle);
        volverBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cerrarMenu();
            }
        });

        mainTable.add(volverBtn).colspan(2).padTop(15).width(180).height(55);
    }

    private void seleccionarNivel(int nivel) {
        Game game = (Game) Gdx.app.getApplicationListener();

        switch (nivel) {
            case 0:
                game.setScreen(new NivelTutorial());
                break;
            case 1:
                game.setScreen(new Nivel1());
                break;
            case 2:
                game.setScreen(new Nivel2());
                break;
            case 3:
                game.setScreen(new Nivel3());
                break;
            case 4:
                game.setScreen(new Nivel4());
                break;
            default:
                game.setScreen(new Lobby());
                break;
        }

        cerrarMenu();
    }

    public void render(SpriteBatch batch) {
        if (!visible) return;

        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        batch.begin();
    }

    public void mostrar() {
        cargarDatosPartida();
        visible = true;
        Gdx.input.setInputProcessor(stage);
    }

    public void cerrarMenu() {
        visible = false;
        Gdx.input.setInputProcessor(null);
    }

    public boolean isVisible() {
        return visible;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        font.dispose();
        fondo.dispose();
        buttonTexture.dispose();
        buttonPressedTexture.dispose();
        lockedButtonTexture.dispose();
        completedTexture.dispose();
    }
}
