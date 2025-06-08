package com.tfg.samuraidash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class InicioPantalla implements Screen {
    private SpriteBatch batch;
    private Texture fondo;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;

    private float fondoX1, fondoX2;

    private Texture buttonTexture;
    private Texture buttonPressedTexture;
    private Rectangle[] buttonBounds;
    private String[] buttonTexts = {"INICIAR", "SALIR"};
    private Music musicaFondo;

    @Override
    public void show() {
        batch = new SpriteBatch();
        fondo = new Texture("Fondos/city 2/7.png");

        // Fuentes
        titleFont = new BitmapFont(Gdx.files.internal("fonts/fuentehiero1.fnt"));
        buttonFont = new BitmapFont(Gdx.files.internal("fonts/fuentehiero2.fnt"));
        titleFont.getData().setScale(3.5f);
        buttonFont.getData().setScale(1.8f);
        fondoX1 = 0;
        fondoX2 = fondo.getWidth();

        // Crear texturas para botones
        buttonTexture = crearTexturaBoton(280, 70,
            new Color(0.1f, 0.1f, 0.1f, 0.8f),
            new Color(0.98f, 0.82f, 0.18f, 1));

        buttonPressedTexture = crearTexturaBoton(280, 70,
            new Color(0.2f, 0.2f, 0.2f, 0.9f),
            new Color(0.98f, 0.92f, 0.38f, 1));

        // Configurar botones
        buttonBounds = new Rectangle[3];
        int startY = Gdx.graphics.getHeight()/2 - 50;
        for (int i = 0; i < 3; i++) {
            buttonBounds[i] = new Rectangle(
                Gdx.graphics.getWidth()/2 - 140,
                startY - (i * 90),
                280, 70);
        }
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica/menuprincipal.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.3f);
        musicaFondo.play();
    }

    private Texture crearTexturaBoton(int width, int height, Color colorFondo, Color colorBorde) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        // Relleno con degradado vertical
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

    @Override
    public void render(float delta) {
        // Mover el fondo
        fondoX1 -= 10 * delta;
        fondoX2 -= 10 * delta;

        if (fondoX1 <= -fondo.getWidth()) {
            fondoX1 = fondo.getWidth();  // Vuelve a colocar el fondo al final
        }
        if (fondoX2 <= -fondo.getWidth()) {
            fondoX2 = fondo.getWidth();  // Vuelve a colocar el fondo al final
        }

        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();

        // Dibuja fondo en movimiento
        batch.draw(fondo, fondoX1, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(fondo, fondoX2, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Dibuja título "FASKOT" centrado
        GlyphLayout titleLayout = new GlyphLayout(titleFont, "FASEOT");
        titleFont.draw(batch, "FASKOT",
            Gdx.graphics.getWidth()/2 - titleLayout.width/2,
            Gdx.graphics.getHeight() - 100);

        // Dibuja botones en orden correcto
        for (int i = 0; i < 2; i++) {
            boolean isHovered = buttonBounds[i].contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
            Texture btnTexture = isHovered ? buttonPressedTexture : buttonTexture;

            batch.draw(btnTexture, buttonBounds[i].x, buttonBounds[i].y, buttonBounds[i].width, buttonBounds[i].height);

            GlyphLayout btnLayout = new GlyphLayout(buttonFont, buttonTexts[i]);
            buttonFont.setColor(isHovered ? Color.GOLD : Color.WHITE);
            buttonFont.draw(batch, buttonTexts[i],
                buttonBounds[i].x + buttonBounds[i].width/2 - btnLayout.width/2,
                buttonBounds[i].y + buttonBounds[i].height/2 + btnLayout.height/2);
        }

        batch.end();

        // Manejar clics
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            for (int i = 0; i < 3; i++) {
                if (buttonBounds[i].contains(Gdx.input.getX(), mouseY)) {
                    switch (i) {
                        case 0: // Iniciar Partida
                            musicaFondo.pause();
                            ((Game)Gdx.app.getApplicationListener()).setScreen(new PartidaPantalla());
                            break;
                        case 1: // Salir
                            Gdx.app.exit();
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        fondo.dispose();
        titleFont.dispose();
        buttonFont.dispose();
        buttonTexture.dispose();
        buttonPressedTexture.dispose();
        musicaFondo.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
