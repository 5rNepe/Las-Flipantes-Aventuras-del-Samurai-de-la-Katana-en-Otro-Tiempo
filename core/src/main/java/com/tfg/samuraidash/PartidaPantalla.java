package com.tfg.samuraidash;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;
import com.tfg.samuraidash.Niveles.lobby.Lobby;
import com.tfg.samuraidash.Niveles.tutorial.NivelTutorial;

public class PartidaPantalla implements Screen, InputProcessor {
    private static final int MAX_NAME_LENGTH = 10;
    private SpriteBatch batch;
    private Texture fondo;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;

    private Texture slotTexture;
    private Texture slotHoverTexture;
    private Texture buttonTexture;
    private Texture buttonPressedTexture;

    private Rectangle[] slotBounds;
    private Rectangle backButtonBounds;
    private String[] slotNames = {"NUEVA PARTIDA", "NUEVA PARTIDA", "NUEVA PARTIDA"};
    private boolean writingName = false;
    private String currentText = "";
    private int selectedSlot = -1;
    private long lastBlinkTime = 0;
    private boolean showCursor = true;
    private float fondoX1, fondoX2;
    private Music musicaFondo;

    @Override
    public void show() {
        batch = new SpriteBatch();
        fondo = new Texture("Fondos/city 6/7.png");

        // Fuentes
        titleFont = new BitmapFont(Gdx.files.internal("fonts/fuentehiero1.fnt"));
        buttonFont = new BitmapFont(Gdx.files.internal("fonts/fuentehiero2.fnt"));
        titleFont.getData().setScale(2.0f);
        buttonFont.getData().setScale(1.5f);
        fondoX1 = 0;
        fondoX2 = fondo.getWidth();

        // Texturas para slots
        slotTexture = crearTexturaBoton(320, 80,
            new Color(0.1f, 0.1f, 0.1f, 0.7f),
            new Color(0.5f, 0.8f, 0.2f, 1));

        slotHoverTexture = crearTexturaBoton(320, 80,
            new Color(0.2f, 0.2f, 0.2f, 0.8f),
            new Color(0.7f, 0.9f, 0.3f, 1));

        // Texturas para botón Volver
        buttonTexture = crearTexturaBoton(160, 70,
            new Color(0.1f, 0.1f, 0.1f, 0.8f),
            new Color(0.8f, 0.2f, 0.2f, 1));

        buttonPressedTexture = crearTexturaBoton(160, 70,
            new Color(0.2f, 0.2f, 0.2f, 0.9f),
            new Color(0.9f, 0.3f, 0.3f, 1));

        // Configuración de botones de partida
        slotBounds = new Rectangle[3];
        int startY = 120;
        for (int i = 0; i < 3; i++) {
            slotBounds[i] = new Rectangle(
                Gdx.graphics.getWidth()/2 - 160,
                startY + (i * 90),
                320, 80);
        }

        // Botón Volver
        backButtonBounds = new Rectangle(20, 25, 160, 70);

        // Cargar partidas guardadas
        for (int i = 0; i < 3; i++) {
            Datos savedGame = loadGameData(i);
            if (savedGame != null) {
                slotNames[i] = savedGame.getNombre();
            }
        }

        Gdx.input.setInputProcessor(this);
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica/escoger.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.3f);
        musicaFondo.play();
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

    @Override
    public void render(float delta) {

        fondoX1 -= 30 * delta;  // Velocidad de movimiento aumentada
        fondoX2 -= 30 * delta;

        if (fondoX1 <= -fondo.getWidth()) {
            fondoX1 = fondo.getWidth();  // Vuelve a colocar el fondo al final
        }
        if (fondoX2 <= -fondo.getWidth()) {
            fondoX2 = fondo.getWidth();  // Vuelve a colocar el fondo al final
        }

        // Cursor parpadeante
        if (System.currentTimeMillis() - lastBlinkTime > 500) {
            showCursor = !showCursor;
            lastBlinkTime = System.currentTimeMillis();
        }

        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();

        batch.draw(fondo, fondoX1, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(fondo, fondoX2, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Dibujar título
        titleFont.setColor(0.98f, 0.82f, 0.18f, 1);
        GlyphLayout titleLayout = new GlyphLayout(titleFont, "SELECCIONAR PARTIDA");
        titleFont.draw(batch, "SELECCIONAR PARTIDA",
            Gdx.graphics.getWidth()/2 - titleLayout.width/2,
            Gdx.graphics.getHeight() - 50);

        // Dibujar slots de partida
        for (int i = 0; i < 3; i++) {
            boolean isHovered = slotBounds[i].contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
            Texture slotTex = isHovered ? slotHoverTexture : slotTexture;

            batch.draw(slotTex, slotBounds[i].x, slotBounds[i].y, slotBounds[i].width, slotBounds[i].height);

            GlyphLayout slotLayout = new GlyphLayout(buttonFont, slotNames[i]);
            buttonFont.setColor(isHovered ? Color.YELLOW : Color.WHITE);
            buttonFont.draw(batch, slotNames[i],
                slotBounds[i].x + slotBounds[i].width/2 - slotLayout.width/2,
                slotBounds[i].y + slotBounds[i].height/2 + slotLayout.height/2);
        }

        // Dibujar botón Volver
        boolean isBackHovered = backButtonBounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        Texture backTex = isBackHovered ? buttonPressedTexture : buttonTexture;

        batch.draw(backTex, backButtonBounds.x, backButtonBounds.y, backButtonBounds.width, backButtonBounds.height);

        GlyphLayout backLayout = new GlyphLayout(buttonFont, "VOLVER");
        buttonFont.setColor(isBackHovered ? Color.YELLOW : Color.WHITE);
        buttonFont.draw(batch, "VOLVER",
            backButtonBounds.x + backButtonBounds.width/2 - backLayout.width/2,
            backButtonBounds.y + backButtonBounds.height/2 + backLayout.height/2);

        // Modo de edición de nombre
        if (writingName) {
            String displayText = currentText;
            GlyphLayout editLayout = new GlyphLayout(buttonFont, displayText);

            // Fondo semitransparente
            Pixmap bgPixmap = new Pixmap((int)(editLayout.width + 40), 60, Pixmap.Format.RGBA8888);
            bgPixmap.setColor(0, 0, 0, 0.7f);
            bgPixmap.fill();
            Texture bgTexture = new Texture(bgPixmap);

            batch.draw(bgTexture,
                Gdx.graphics.getWidth()/2 - (editLayout.width + 40)/2,
                Gdx.graphics.getHeight()/2 - 30);
            bgTexture.dispose();
            bgPixmap.dispose();

            // Texto con cursor
            String textToShow = currentText.length() > MAX_NAME_LENGTH ?
                currentText.substring(0, MAX_NAME_LENGTH) : currentText;
            buttonFont.draw(batch, textToShow + (showCursor ? "_" : ""),
                Gdx.graphics.getWidth()/2 - editLayout.width/2,
                Gdx.graphics.getHeight()/2 + 10);
        }

        batch.end();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (writingName) return true;

        int mouseY = Gdx.graphics.getHeight() - screenY;

        // Verificar clic en slots de partida
        for (int i = 0; i < 3; i++) {
            if (slotBounds[i].contains(screenX, mouseY)) {
                Datos gameData = loadGameData(i);

                if (gameData == null) {
                    writingName = true;
                    selectedSlot = i;
                    currentText = "";
                    saveSelectedSlot(i);
                    musicaFondo.stop();
                    return true;
                } else {
                    Game game = (Game) Gdx.app.getApplicationListener();
                    saveSelectedSlot(i);
                    musicaFondo.stop();
                    if (!gameData.isTutorialCompletado()) {
                        game.setScreen(new NivelTutorial());
                    } else {
                        game.setScreen(new Lobby());
                    }
                    return true;
                }
            }
        }

        // Verificar clic en botón Volver
        if (backButtonBounds.contains(screenX, mouseY)) {
            musicaFondo.stop();
            ((Game)Gdx.app.getApplicationListener()).setScreen(new InicioPantalla());
            return true;
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (writingName) {
            if (character == '\b' && currentText.length() > 0) {
                currentText = currentText.substring(0, currentText.length() - 1);
            } else if (character == '\n' || character == '\r') {
                if (!currentText.isEmpty() && currentText.length() <= MAX_NAME_LENGTH) {
                    slotNames[selectedSlot] = currentText;
                    createNewGame(selectedSlot, currentText);
                    writingName = false;
                    saveSelectedSlot(selectedSlot);
                    ((Game)Gdx.app.getApplicationListener()).setScreen(new NivelTutorial());
                }
            } else if (character >= 32 && character <= 126 && currentText.length() < MAX_NAME_LENGTH) {
                currentText += character;
            }
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        batch.dispose();
        fondo.dispose();
        titleFont.dispose();
        buttonFont.dispose();
        slotTexture.dispose();
        slotHoverTexture.dispose();
        buttonTexture.dispose();
        buttonPressedTexture.dispose();
        musicaFondo.dispose();
    }

    private Datos loadGameData(int slot) {
        FileHandle file = Gdx.files.local("partida" + (slot + 1) + ".json");
        if (file.exists()) {
            String json = file.readString();
            return new Json().fromJson(Datos.class, json);
        }
        return null;
    }

    private void saveSelectedSlot(int slot) {
        Preferences prefs = Gdx.app.getPreferences("GameSettings");
        prefs.putInteger("selectedSlot", slot);
        prefs.flush();
    }

    private void createNewGame(int slot, String name) {
        FileHandle file = Gdx.files.local("partida" + (slot + 1) + ".json");
        Datos newGame = new Datos(name, 0, false);
        String json = new Json().toJson(newGame);
        file.writeString(json, false);
    }

    // Métodos vacíos requeridos
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean touchUp(int x, int y, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int x, int y, int pointer) { return false; }
    @Override public boolean mouseMoved(int x, int y) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int x, int y, int pointer, int button) { return false; }
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
