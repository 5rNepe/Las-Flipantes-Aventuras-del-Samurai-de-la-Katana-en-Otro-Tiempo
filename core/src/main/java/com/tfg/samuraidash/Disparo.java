package com.tfg.samuraidash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Disparo {
    private float x, y;
    private Vector2 velocity;
    private float speed;
    private TextureRegion region;
    private float width, height;
    private float timeAlive = 0;
    private final float MAX_TIME_ALIVE = 3.0f;

    public Disparo(float x, float y, float targetX, float targetY,
                      Texture texture, float width, float height, float speed) {
        this.x = x;
        this.y = y;
        Vector2 direction = new Vector2(targetX - x, targetY - y).nor();
        this.velocity = new Vector2(direction.x * speed, direction.y * speed);
        this.speed = speed;
        this.width = width;
        this.height = height;

        this.region = new TextureRegion(texture);
        if (velocity.x < 0) {
            region.flip(true, false);
        }
    }

    public void update(float delta) {
        // Actualizar posición
        x += velocity.x * delta;
        y += velocity.y * delta;

        timeAlive += delta;
    }

    public void render(SpriteBatch batch) {
        batch.draw(region, x, y, width, height);
    }

    public boolean collidesWith(Samurai samurai) {
        return x + width > samurai.getX() + 50 &&
            x < samurai.getX() + samurai.getWidth() &&
            y + height > samurai.getY() &&
            y < samurai.getY() + samurai.getHeight();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void dispose() {
    }

    public boolean elimina() {
        return timeAlive >= MAX_TIME_ALIVE;
    }
}
