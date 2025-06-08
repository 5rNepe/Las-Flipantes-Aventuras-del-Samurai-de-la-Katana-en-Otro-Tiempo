package com.tfg.samuraidash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Objects;

public class NPC {
    private float x, y;
    private int width, height;
    private TextureRegion currentTexture;
    private Animation<TextureRegion> idleAnimation;
    private boolean isFacingLeft;
    private boolean isNearPlayer;
    private String textocabeza = "";

    private float stateTime = 0f;
    private static final int INTERACT_RADIUS = 50;

    private String npcNombre;
    private Samurai samurai;
    public Dialogo dialogo;
    private OrthographicCamera camera;
    private BitmapFont font;
    private BitmapFont font2;

    // Cargamos el rectángulo de fondo
    private Texture dialogBackground, flechaTexture;
    private boolean dialogoCompletado = false;
    private long blinkStartTime;
    private boolean showArrow = true;

    public NPC(Samurai samurai, float x, float y, String animationFilePath, int frames, float frameTime, Animation.PlayMode playMode, String npcNombre, Array<String> textosDialogo, OrthographicCamera camera) {
        this.samurai = samurai;
        this.camera = camera;
        this.x = x;
        this.y = y;
        this.npcNombre = npcNombre;
        idleAnimation = loadAnimation(animationFilePath, frames, frameTime, playMode);
        dialogoCompletado = false;


        TextureRegion firstFrame = idleAnimation.getKeyFrame(0f);
        this.width = firstFrame.getRegionWidth();
        this.height = firstFrame.getRegionHeight();

        this.isFacingLeft = false;
        this.isNearPlayer = false;

        this.dialogo = new Dialogo(npcNombre, textosDialogo);
        font = new BitmapFont(Gdx.files.internal("fonts/fuentehiero2.fnt"));
        font2 = new BitmapFont(Gdx.files.internal("fonts/fuentehiero1.fnt"));

        // Cargamos la imagen del rectángulo de fondo para el diálogo
        flechaTexture = new Texture("flecha.png");
        dialogBackground = new Texture("dialogos.png");
    }

    public NPC(Samurai samurai, float x, float y, String animationFilePath, int frames, float frameTime, Animation.PlayMode playMode, String npcNombre, Array<String> textosDialogo, OrthographicCamera camera, String textocabeza) {
        this.samurai = samurai;
        this.camera = camera;
        this.x = x;
        this.y = y;
        this.npcNombre = npcNombre;
        this.textocabeza = textocabeza;
        idleAnimation = loadAnimation(animationFilePath, frames, frameTime, playMode);
        dialogoCompletado = false;

        TextureRegion firstFrame = idleAnimation.getKeyFrame(0f);
        this.width = firstFrame.getRegionWidth();
        this.height = firstFrame.getRegionHeight();

        this.isFacingLeft = false;
        this.isNearPlayer = false;

        this.dialogo = new Dialogo(npcNombre, textosDialogo);
        font = new BitmapFont(Gdx.files.internal("fonts/fuentehiero2.fnt"));
        font2 = new BitmapFont(Gdx.files.internal("fonts/fuentehiero1.fnt"));

        // Cargamos la imagen del rectángulo de fondo para el diálogo
        flechaTexture = new Texture("flecha.png");
        dialogBackground = new Texture("dialogos.png");
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    private Animation<TextureRegion> loadAnimation(String file, int frames, float frameTime, Animation.PlayMode mode) {
        Texture texture = new Texture(file);
        return loadAnimation(texture, frames, frameTime, mode);
    }

    private Animation<TextureRegion> loadAnimation(Texture texture, int frames, float frameTime, Animation.PlayMode mode) {
        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / frames, texture.getHeight());
        Array<TextureRegion> framesArray = new Array<>();
        for (int i = 0; i < frames; i++) {
            framesArray.add(tmp[0][i]);
        }
        Animation<TextureRegion> anim = new Animation<>(frameTime, framesArray);
        anim.setPlayMode(mode);
        return anim;
    }

    // Método de actualización del NPC
    public void update(Samurai samurai) {
        stateTime += Gdx.graphics.getDeltaTime();

        // Detectamos si el jugador está cerca para interactuar
        if (Math.abs(samurai.getX() - this.x) < INTERACT_RADIUS && Math.abs(samurai.getY() - this.y) < INTERACT_RADIUS) {
            isNearPlayer = true;
        } else {
            isNearPlayer = false;
        }

        // Comprobamos si el jugador está a la izquierda o derecha para cambiar de dirección
        if (samurai.getX() < this.x && !isFacingLeft) {
            isFacingLeft = true;
        } else if (samurai.getX() > this.x && isFacingLeft) {
            isFacingLeft = false;
        }

        // Actualizamos la animación del NPC
        currentTexture = idleAnimation.getKeyFrame(stateTime, true);

        // Actualizamos el diálogo si está en progreso
        if (dialogo != null && dialogo.isInProgress()) {
            dialogo.update(Gdx.graphics.getDeltaTime());
        }

        actualizarDialogo();
    }

    // Método para dibujar el NPC y el mensaje de interacción
    public void render(SpriteBatch batch) {
        // Si el NPC está mirando a la izquierda, invertimos su imagen
        if (isFacingLeft) {
            batch.draw(currentTexture, x + width, y, -width, height); // Dibujamos invertido
        } else {
            batch.draw(currentTexture, x, y, width, height);
        }

        font.setColor(1, 1, 1, 1);
        font2.setColor(1, 1, 1, 1);

        // Mostrar el mensaje de interacción si el jugador está cerca y no está en diálogo
        if (isNearPlayer && !dialogo.isInProgress() && Objects.equals(textocabeza, "null")) {
            font2.draw(batch, "Presiona E para hablar", x - 60, y + height - 20);
        } else if (isNearPlayer && !dialogo.isInProgress() && Objects.equals(textocabeza, "no")) {

        } else if (isNearPlayer && !dialogo.isInProgress()) {
            font2.draw(batch, textocabeza, x - 60, y + height - 20);
        }

        // Mostrar el diálogo si está en progreso
        if (dialogo != null && dialogo.isInProgress()) {
            samurai.setInDialogue(true);
            // Dibujamos el fondo del diálogo que se estira a lo largo de toda la pantalla
            batch.setColor(0, 0, 0, 0.6f);
            batch.draw(dialogBackground, camera.position.x - 424, camera.position.y - 370, Gdx.graphics.getWidth() + 156, Gdx.graphics.getHeight() + 50);

            // Restablecemos el color
            batch.setColor(1, 1, 1, 1);

            // Dibujamos el nombre del NPC en la parte superior izquierda del fondo
            font.draw(batch, npcNombre, camera.position.x - 300, camera.position.y - 77);

            String dialogoTexto = dialogo.getCurrentText();
            String wrappedText = wrapText(dialogoTexto, 30);

            // Dibujamos el texto del diálogo dentro del cuadro negro
            font.draw(batch, wrappedText, camera.position.x - 300, camera.position.y - 120);

            if (dialogo.canProceed()) {
                // Controlamos el parpadeo cada 0,5 segundos
                if (TimeUtils.nanoTime() - blinkStartTime > 500_000_000L) {
                    showArrow = !showArrow;
                    blinkStartTime = TimeUtils.nanoTime();
                }

                if (showArrow) {
                    batch.draw(flechaTexture, camera.position.x + 230, camera.position.y - 230, 32, 32);
                }
            }

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                dialogo.nextText();  // Pasar al siguiente texto del diálogo
            }

            if (!dialogo.isInProgress()) {
                samurai.setInDialogue(false);  // Liberamos el movimiento solo después de que el diálogo haya terminado
            }
        }
    }

    private String wrapText(String text, int lineLength) {
        StringBuilder wrappedText = new StringBuilder();
        String[] words = text.split(" "); // Dividimos el texto por palabras
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            // Si agregar esta palabra supera el límite, hacemos un salto de línea
            if (line.length() + word.length() > lineLength) {
                wrappedText.append(line).append("\n"); // Añadimos la línea actual
                line.setLength(0); // Reseteamos la línea
            }

            // Agregamos la palabra a la línea (con un espacio si no es la primera)
            if (line.length() > 0) {
                line.append(" ");
            }
            line.append(word);
        }

        // Agregar la última línea si no está vacía
        if (line.length() > 0) {
            wrappedText.append(line);
        }

        return wrappedText.toString();
    }


    // Método para interactuar
    public void interact() {
        if (isNearPlayer) {
            dialogo.start();  // Iniciar el diálogo
        }
    }

    public void actualizarDialogo() {
        if (dialogo != null && !dialogo.isInProgress() && !dialogoCompletado) {
            dialogoCompletado = true;  // Marcar que el diálogo ha terminado
        }
    }

    public boolean isNearPlayer() {
        return isNearPlayer;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
