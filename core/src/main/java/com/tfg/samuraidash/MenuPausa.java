package com.tfg.samuraidash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuPausa {
    private Stage stage;
    private Table mainTable;
    private Table controlesTable;
    private BitmapFont font;
    private boolean visible = false;
    private Texture fondo;
    private Texture controlesTexture;
    private Texture buttonTexture;
    private Texture buttonPressedTexture;

    public MenuPausa(Screen nivelActual) {
        stage = new Stage(new ScreenViewport());

        // Cargar recursos
        font = new BitmapFont(Gdx.files.internal("fonts/fuentehiero2.fnt"));
        fondo = new Texture(Gdx.files.internal("Overlays/9.png"));
        controlesTexture = new Texture(Gdx.files.internal("controles.png"));

        // Crear texturas para botones
        buttonTexture = crearTexturaBoton(200, 50, new Color(0.1f, 0.1f, 0.1f, 0.9f), new Color(0.98f, 0.82f, 0.18f, 1));
        buttonPressedTexture = crearTexturaBoton(200, 50, new Color(0.2f, 0.2f, 0.2f, 0.9f), new Color(0.98f, 0.82f, 0.18f, 1));

        crearUI();
    }

    private Texture crearTexturaBoton(int width, int height, Color colorFondo, Color colorBorde) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        // Relleno del botón
        pixmap.setColor(colorFondo);
        pixmap.fill();

        // Borde del botón
        pixmap.setColor(colorBorde);
        pixmap.drawRectangle(0, 0, width-1, height-1);
        pixmap.drawRectangle(1, 1, width-3, height-3);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void crearUI() {
        // Configurar estilo de botones
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonPressedTexture));
        buttonStyle.over = new TextureRegionDrawable(new TextureRegion(buttonTexture));

        // Tabla principal del menú de pausa
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        // Fondo semitransparente
        Image fondoImage = new Image(fondo);
        fondoImage.setColor(0.1f, 0.1f, 0.1f, 0.8f);
        fondoImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(fondoImage);

        // Botones del menú principal
        TextButton reanudarBtn = new TextButton("REANUDAR", buttonStyle);
        TextButton controlesBtn = new TextButton("CONTROLES", buttonStyle);
        TextButton salirBtn = new TextButton("SALIR", buttonStyle);

        // Añadir que pasa al clicar
        reanudarBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                reanudarJuego();
            }
        });

        controlesBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mostrarControles();
            }
        });

        salirBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                salirAlMenu();
            }
        });

        // Organizar botones
        mainTable.add(reanudarBtn).padBottom(20).width(200).height(50).row();
        mainTable.add(controlesBtn).padBottom(20).width(200).height(50).row();
        mainTable.add(salirBtn).width(200).height(50).row();

        // Tabla de controles
        controlesTable = new Table();
        controlesTable.setFillParent(true);
        controlesTable.center();

        Image controlesImage = new Image(controlesTexture);
        TextButton volverBtn = new TextButton("VOLVER", buttonStyle);

        volverBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mostrarMenuPrincipal();
            }
        });

        controlesTable.add(controlesImage).padBottom(20).row();
        controlesTable.add(volverBtn).width(200).height(50);

        stage.addActor(mainTable);
    }

    public void render(SpriteBatch batch) {
        if (!visible) return;

        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        batch.begin();
    }

    public void toggle() {
        visible = !visible;
        if (visible) {
            stage.getRoot().setVisible(true);
            Gdx.input.setInputProcessor(stage);
        } else {
            stage.getRoot().setVisible(false);
            Gdx.input.setInputProcessor(null);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    private void reanudarJuego() {
        toggle();
    }

    private void mostrarControles() {
        mainTable.setVisible(false);
        controlesTable.setVisible(true);
        stage.addActor(controlesTable);
    }

    private void mostrarMenuPrincipal() {
        controlesTable.setVisible(false);
        mainTable.setVisible(true);
    }

    private void salirAlMenu() {
        ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new InicioPantalla());
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        font.dispose();
        fondo.dispose();
        controlesTexture.dispose();
        buttonTexture.dispose();
        buttonPressedTexture.dispose();
    }
}
