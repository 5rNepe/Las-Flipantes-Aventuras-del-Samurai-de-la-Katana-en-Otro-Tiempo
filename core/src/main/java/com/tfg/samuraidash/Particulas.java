package com.tfg.samuraidash;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Particulas {
    private Animation<TextureRegion> animation;
    private float stateTime = 0;
    private float x, y;
    private boolean finished = false;
    private float velocityX;
    private static final int TOTAL_FRAMES = 5; // Número total de frames en la animación
    private int currentFrame = 0; // Para verificar en qué frame está
    private boolean facingLeft;

    public Particulas(Texture texture, float x, float y, boolean facingLeft, float frameDuration) {
        this.x = x;
        this.y = y;
        this.facingLeft = facingLeft;

        this.velocityX = facingLeft ? 50 : -50;

        // Dividir la textura en frames
        int frameWidth = texture.getWidth() / TOTAL_FRAMES;
        TextureRegion[][] tempFrames = TextureRegion.split(texture, frameWidth, texture.getHeight());

        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < TOTAL_FRAMES; i++) {
            TextureRegion frame = tempFrames[0][i];

            if (facingLeft) {
                frame.flip(true, false); // Voltear horizontalmente
            }

            frames.add(frame);
        }

        animation = new Animation<>(frameDuration, frames, Animation.PlayMode.NORMAL);
    }

    public void update(float delta) {
        stateTime += delta;
        currentFrame = (int) (stateTime / animation.getFrameDuration());
        x += velocityX * delta;

        if (animation.isAnimationFinished(stateTime)) {
            finished = true;
        }
    }

    public void render(SpriteBatch batch) {
        if (!finished) {
            batch.draw(animation.getKeyFrame(stateTime), x, y);
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }
}

