package com.tfg.samuraidash;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Plataforma {
    private float x, y;
    private float width, height;
    private Texture texture;

    public Plataforma(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle getBounds() {
        return new Rectangle(x - 15, y + 20, width - 30, height - 20);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Texture getTexture() {
        return texture;
    }

    public void dispose() {
        texture.dispose();
    }

    // Verificar si el samurai está tocando la plataforma
    public boolean isCollidingWithPlayer(float futureX, float futureY, float playerWidth, float playerHeight) {
        Rectangle futureSamuraiBounds = new Rectangle(futureX, futureY, playerWidth, playerHeight);
        return futureSamuraiBounds.overlaps(getBounds());  // Detecta si el rectángulo del jugador futuro colisiona
    }


}

