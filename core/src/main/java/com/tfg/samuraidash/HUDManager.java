package com.tfg.samuraidash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class HUDManager {
    private Samurai samurai;
    private OrthographicCamera camera;

    // Texturas
    private Texture whitePixelTex;
    private Texture katanaIcon;
    private Texture staminaBarTex;

    // Fuentes
    private BitmapFont font;
    private BitmapFont japaneseFont;

    private float levelTimer;
    private final float MAX_LEVEL_TIME = 200f;
    private boolean timeUp = false;
    private boolean timerRunning = true;
    private int comboCount;
    private float comboTimeLeft;
    private final float MAX_COMBO_TIME = 3f; // Tiempo máximo inicial del combo
    private int comboMultiplier = 1; // Multiplicador de combo (1, 2, 4, 8)
    private int comboMultiplierMax = 1;

    private float shakeDuration = 0f;
    private float shakeIntensity = 0f;
    private float shakeTimer = 0f;
    private Vector2 shakeOffset = new Vector2();


    public HUDManager(Samurai samurai, OrthographicCamera camera) {
        this.samurai = samurai;
        this.camera = camera;
        this.levelTimer = MAX_LEVEL_TIME;
        this.timeUp = false;

        // Crear textura blanca de 1x1 píxel
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixelTex = new Texture(pixmap);
        pixmap.dispose();

        // Cargar texturas
        katanaIcon = new Texture("ui/katana_icon.png");
        staminaBarTex = new Texture("ui/stamina_bar.png");

        // Configurar fuentes
        font = new BitmapFont();
        font.getData().setScale(1f);

        japaneseFont = new BitmapFont(Gdx.files.internal("fonts/fuentejapon2.fnt"));
        japaneseFont.getData().setScale(0.6f);
    }

    public void setSamurai(Samurai samurai) {
        this.samurai = samurai;
    }

    public int getComboMultiplierMax() {
        return comboMultiplierMax;
    }

    public float getLevelTimer() {
        return levelTimer;
    }

    public void update(float delta) {
        if (timerRunning && !timeUp) {
            levelTimer -= delta;

            // Cuando el tiempo llega a cero
            if (levelTimer <= 0) {
                levelTimer = 0;
                timeUp = true;
                handleTimeUp();
            }
        }

        if (comboMultiplier > comboMultiplierMax){
            comboMultiplierMax = comboMultiplier;
        }

        if (comboCount > 0) {
            comboTimeLeft -= delta;
            if (comboTimeLeft <= 0) {
                resetCombo();
            }
        }

        if(samurai.isDead()){
            resetCombo();
            resetTimer();
        }

        if (shakeTimer > 0) {
            shakeTimer -= delta;
            if (shakeTimer <= 0) {
                shakeOffset.set(0, 0);
            } else {
                float progress = shakeTimer / shakeDuration;
                float currentIntensity = shakeIntensity * progress;
                shakeOffset.set(
                    MathUtils.random(-1f, 1f) * currentIntensity,
                    MathUtils.random(-1f, 1f) * currentIntensity
                );
            }
        }

        if(samurai.isDashing()){
            triggerShake(0.2f, 5f);
        }
    }

    public void render(SpriteBatch batch) {
        float screenLeft = camera.position.x - camera.viewportWidth/2 + shakeOffset.x;
        float screenRight = camera.position.x + camera.viewportWidth/2 + shakeOffset.x;
        float screenTop = camera.position.y + camera.viewportHeight/2 + shakeOffset.y;
        float screenBottom = camera.position.y - camera.viewportHeight/2 + shakeOffset.y;

        // Margen para los elementos del HUD
        float margin = 15;

        int seconds = (int)levelTimer;
        String timerText = String.format("%03d", seconds); // Formato 3 dígitos con ceros

        // Cambiar color cuando quedan menos de 10 segundos
        if (seconds <= 10 && !timeUp) {
            float blink = (float) Math.abs(Math.sin(levelTimer * 10f));
            japaneseFont.setColor(1f, blink, blink, 1f);
        } else if (timeUp) {
            japaneseFont.setColor(Color.RED);
        } else {
            japaneseFont.setColor(Color.WHITE);
        }

        japaneseFont.draw(batch, timerText,
            screenRight - margin - 75, screenTop - margin + 5);

        japaneseFont.setColor(Color.WHITE);

        // --- BARRA DE COMBO (arriba izquierda) ---
        renderComboBar(batch, screenLeft + margin, screenTop - margin);

        // --- BARRA DE STAMINA (abajo izquierda) ---
        renderStaminaBar(batch, screenRight - margin - 610, screenBottom + margin + 20);

        // --- BARRA DE FRENESÍ (abajo derecha) ---
        renderFrenzyBar(batch, screenRight - margin - 180, screenBottom + margin + 20);
    }

    private void handleTimeUp() {
        samurai.die();
    }

    public void pauseTimer() {
        timerRunning = false;
    }

    public void resetTimer() {
        levelTimer = MAX_LEVEL_TIME;
        timeUp = false;
    }

    private void renderComboBar(SpriteBatch batch, float x, float y) {
        float barWidth = 180;
        float barHeight = 10;
        float comboProgress = comboTimeLeft / MAX_COMBO_TIME;
        Color comboColor = getComboColor(comboMultiplier);

        // Texto "Combo"
        japaneseFont.setColor(comboColor);
        japaneseFont.draw(batch, "COMBO", x, y + barHeight);

        // Fondo de la barra
        batch.setColor(0.1f, 0.1f, 0.1f, 0.8f);
        batch.draw(whitePixelTex, x, y - 50, barWidth, barHeight);

        // Barra de combo con color que cambia según el multiplicador
        batch.setColor(comboColor);
        batch.draw(whitePixelTex, x, y - 50, barWidth * comboProgress, barHeight);

        // Borde
        batch.setColor(0.8f, 0.8f, 0.8f, 0.5f);
        batch.draw(whitePixelTex, x - 1, y - 51, barWidth + 2, barHeight + 2);

        // Mostrar multiplicador de combo
        String multiplierText = "X" + comboMultiplier;
        japaneseFont.setColor(comboColor);
        japaneseFont.draw(batch, multiplierText, x, y - 55);

        // Efecto especial para combos altos
        if (comboMultiplier >= 4) {
            float pulse = (float)(Math.sin(levelTimer * 10) * 0.2f + 0.8f);
            batch.setColor(comboColor.r, comboColor.g, comboColor.b, pulse * 0.4f);
            batch.draw(whitePixelTex,
                x - 3,
                y - 53,
                barWidth + 6,
                barHeight + 6);
        }
        batch.setColor(Color.WHITE);
    }

    private Color getComboColor(int multiplier) {
        switch (multiplier) {
            case 1: return new Color(0.2f, 0.7f, 1f, 1); // Azul
            case 2: return new Color(0.4f, 1f, 0.4f, 1); // Verde
            case 4: return new Color(1f, 0.8f, 0.2f, 1); // Amarillo
            case 8: return new Color(1f, 0.3f, 0.3f, 1); // Rojo
            default: return Color.WHITE;
        }
    }

    private void renderFrenzyBar(SpriteBatch batch, float x, float y) {
        if (!samurai.isFrenzyActive()) {
            float barWidth = 180;
            float barHeight = 10;
            float chargeWidth = barWidth * (samurai.getFrenzyCharge() / 100f);

            // Texto "Frenesí"
            japaneseFont.setColor(1, 0.8f, 0.8f, 1);
            japaneseFont.draw(batch, "FRENESI", x, y + barHeight + 30);

            // Fondo de la barra
            batch.setColor(0.1f, 0.1f, 0.1f, 0.8f);
            batch.draw(whitePixelTex, x, y - 20, barWidth, barHeight);

            // Barra de carga con degradado
            for (float i = 0; i < chargeWidth; i += 2) {
                float progress = i / barWidth;
                batch.setColor(
                    1.0f,
                    0.2f + 0.3f * progress,
                    0.2f + 0.1f * progress,
                    0.9f
                );
                batch.draw(whitePixelTex, x + i, y - 20, 2, barHeight);
            }

            // Icono de katana
            if (chargeWidth > 0) {
                float katanaWidth = barHeight * 3;
                float katanaHeight = katanaWidth * ((float)katanaIcon.getHeight() / katanaIcon.getWidth());

                batch.setColor(Color.WHITE);
                batch.draw(katanaIcon,
                    x + chargeWidth - katanaWidth/2,
                    y + barHeight/2 - 20 - katanaHeight/2,
                    katanaWidth, katanaHeight);
            }

            // Efecto especial al 100%
            if (samurai.getFrenzyCharge() >= 100) {
                float pulse = (float)(Math.sin(levelTimer * 10) * 0.2f + 0.8f);
                batch.setColor(1, 0.7f, 0.3f, pulse * 0.6f);
                batch.draw(whitePixelTex,
                    x - 3,
                    y - 23,
                    barWidth + 6,
                    barHeight + 6);
            }
        }
        batch.setColor(Color.WHITE);
    }

    private void renderStaminaBar(SpriteBatch batch, float x, float y) {
        float barWidth = 180;
        float barHeight = 10;
        float stamina = samurai.getStamina();

        // Texto "Estamina"
        japaneseFont.setColor(0.8f, 1, 0.8f, 1);
        japaneseFont.draw(batch, "ESTAMINA", x, y + barHeight + 33);

        // Fondo
        batch.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        batch.draw(whitePixelTex, x, y - 20, barWidth, barHeight);

        // Barra de stamina
        Color staminaColor = stamina > 0.5f ?
            new Color(0.2f, 0.8f, 0.2f, 1) :
            stamina > 0.2f ?
                new Color(0.8f, 0.8f, 0.2f, 1) :
                new Color(0.8f, 0.2f, 0.2f, 1);

        batch.setColor(staminaColor);
        batch.draw(whitePixelTex, x, y - 20, barWidth * stamina, barHeight);

        // Borde
        batch.setColor(0.8f, 0.8f, 0.8f, 0.5f);
        batch.draw(whitePixelTex, x - 1, y - 21, barWidth + 2, barHeight + 2);

        batch.setColor(Color.WHITE);
    }

    public void addCombo() {
        comboCount++;
        comboTimeLeft = MAX_COMBO_TIME;

        // Actualizar multiplicador de combo basado en el conteo
        if (comboCount >= 10) {
            comboMultiplier = 8;
        } else if (comboCount >= 6) {
            comboMultiplier = 4;
        } else if (comboCount >= 3) {
            comboMultiplier = 2;
        } else {
            comboMultiplier = 1;
        }

        // Añadir carga de frenesí con el multiplicador de combo
        float frenzyGain = (5 + comboCount * 2) * comboMultiplier;
        samurai.addFrenzyCharge(frenzyGain);
    }

    public void resetCombo() {
        comboCount = 0;
        comboTimeLeft = 0;
        comboMultiplier = 1;
    }

    public void triggerShake(float duration, float intensity) {
        this.shakeDuration = duration;
        this.shakeIntensity = intensity;
        this.shakeTimer = duration;
    }

    public void dispose() {
        whitePixelTex.dispose();
        katanaIcon.dispose();
        staminaBarTex.dispose();
        font.dispose();
        japaneseFont.dispose();
    }
}
