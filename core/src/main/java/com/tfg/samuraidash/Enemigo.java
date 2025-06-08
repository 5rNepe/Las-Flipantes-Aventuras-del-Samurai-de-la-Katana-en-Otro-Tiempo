package com.tfg.samuraidash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

public class Enemigo {
    private Animation<TextureRegion> idleAnimation, runAnimation, dashAnimation, deadAnimation;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime = 0;
    private float x, y;
    private float patrolStart, patrolEnd;
    private float speed = 100;
    private boolean movingRight = true;
    private boolean isDashing = false;
    private float dashTime = 0;
    private final float DASH_DURATION = 0.3f;
    private final float DASH_SPEED = 300;
    private boolean isDead = false;
    private Samurai samurai;
    private int tipoEnemigo;
    private float idleTime = 0;
    private final float IDLE_DURATION = 2.0f;
    private float visionRange; // Rango de visión
    private float dashCooldown = 0;
    private final float DASH_COOLDOWN = 0.5f;
    private float attackCooldown = 0;
    private final float ATTACK_COOLDOWN = 2.0f; // Para el arquero

    private Array<Disparo> proyectiles;
    private Texture projectileTexture;
    private final float PROJECTILE_SPEED = 200f;
    private final float PROJECTILE_WIDTH = 16f;
    private final float PROJECTILE_HEIGHT = 8f;
    private float attackAnimationTime = 0;
    private final float ATTACK_DISPLAY_TIME = 0.4f;
    private boolean isAttacking = false;
    private Sound deathSound, sonidoDisparo, sonidoDash;
    private float samuraiX, samuraiY, samuraiWidth, samuraiHeight;

    public Enemigo(float x, float y, float patrolEnd, Samurai samurai, int tipoEnemigo, String idle, int idle1, String run, int run1, String attack, int attack1, String dead, int dead1) {
        this.x = x;
        this.y = y;
        this.patrolStart = x;
        this.patrolEnd = patrolEnd;
        this.samurai = samurai;
        this.tipoEnemigo = tipoEnemigo;

        this.proyectiles = new Array<>();


        if (tipoEnemigo == 1) {
            this.visionRange = 200f; // Mayor rango de visión
            this.speed = 80; // Más lento que el Ronin
            this.projectileTexture = new Texture("bala.png");
        } else if (tipoEnemigo == 0) { // Ronin por defecto
            this.visionRange = 100f;
            this.speed = 100;
        }

        // Cargar las animaciones
        idleAnimation = loadAnimation(idle, idle1, 0.2f);
        runAnimation = loadAnimation(run, run1, 0.15f);  // Animación de correr
        dashAnimation = loadAnimation(attack, attack1, 0.1f);
        deadAnimation = loadAnimation(dead, dead1, 0.1f);  // Animación de muerte

        currentAnimation = idleAnimation;  // Inicia con la animación de idle
        deathSound = Gdx.audio.newSound(Gdx.files.internal("musica/muerte.mp3"));
        sonidoDash = Gdx.audio.newSound(Gdx.files.internal("musica/dashenemigo.mp3"));
        sonidoDisparo = Gdx.audio.newSound(Gdx.files.internal("musica/disparo.mp3"));
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return currentAnimation.getKeyFrame(stateTime).getRegionWidth();
    }

    public float getHeight() {
        return currentAnimation.getKeyFrame(stateTime).getRegionHeight() - 30;
    }

    private Animation<TextureRegion> loadAnimation(String file, int frames, float frameTime) {
        Texture texture = new Texture(file);
        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / frames, texture.getHeight());
        Array<TextureRegion> framesArray = new Array<>();
        for (int i = 0; i < frames; i++) {
            framesArray.add(tmp[0][i]);
        }
        return new Animation<>(frameTime, framesArray, Animation.PlayMode.LOOP);
    }

    public void update(float delta, float samuraiX, float samuraiY, float samuraiWidth, float samuraiHeight) {
        stateTime += delta;

        // Actualizamos las propiedades del samurái (solo si cambian)
        this.samuraiX = samuraiX;
        this.samuraiY = samuraiY;
        this.samuraiWidth = samuraiWidth;
        this.samuraiHeight = samuraiHeight;

        if (isDead) {
            if (currentAnimation.getPlayMode() != Animation.PlayMode.NORMAL) {
                currentAnimation.setPlayMode(Animation.PlayMode.NORMAL);
            }
            return;
        }

        if (dashCooldown > 0) dashCooldown -= delta;
        if (attackCooldown > 0) attackCooldown -= delta;

        switch (tipoEnemigo) {
            case 0:
                updateRonin(delta);
                break;
            case 1:
                updateArquero(delta);
                break;
        }

        for (Disparo projectile : proyectiles) {
            projectile.update(delta);
        }

        // Eliminar proyectiles fuera de pantalla
        for (int i = proyectiles.size - 1; i >= 0; i--) {
            Disparo disparo = proyectiles.get(i);
            disparo.update(delta);

            if (disparo.elimina()) {
                disparo.dispose();
                proyectiles.removeIndex(i);
            }
        }
    }

    private void updateRonin(float delta) {
        if (isDashing) {
            float newX = x + (movingRight ? DASH_SPEED : -DASH_SPEED) * delta;
            newX = MathUtils.clamp(newX, patrolStart, patrolEnd);

            if (newX != x) {
                x = newX;
            } else {
                isDashing = false;
            }

            dashTime -= delta;
            if (dashTime <= 0) {
                isDashing = false;
                dashCooldown = DASH_COOLDOWN;
                currentAnimation = runAnimation;
            }
        } else {
            // Comportamiento normal de patrulla
            if ((movingRight && x >= patrolEnd) || (!movingRight && x <= patrolStart)) {
                idleTime += delta;
                currentAnimation = idleAnimation;

                if (idleTime >= IDLE_DURATION) {
                    idleTime = 0;
                    movingRight = !movingRight;
                }
            } else {
                x += (movingRight ? speed : -speed) * delta;
                currentAnimation = runAnimation;
            }

            // Si el samurái está cerca y no hay cooldown, atacar
            if (Math.abs(x - samuraiX) < visionRange && dashCooldown <= 0) {
                isDashing = true;
                dashTime = DASH_DURATION;
                if (Math.abs(samuraiY - y) < 300) {
                    sonidoDash.play();
                }
                currentAnimation = dashAnimation;
                movingRight = (samuraiX > x);
                dashCooldown = DASH_COOLDOWN;
            }
        }
    }

    private void updateArquero(float delta) {
        float distanceToSamurai = Math.abs(x - samuraiX);
        boolean canSeeSamurai = distanceToSamurai < visionRange;

        if (isAttacking) {
            attackAnimationTime += delta;
            if (attackAnimationTime >= ATTACK_DISPLAY_TIME) {
                isAttacking = false;
                currentAnimation = idleAnimation;
            }
            return; // No hacer nada más mientras se muestra la animación de ataque
        }

        if (canSeeSamurai) {
            currentAnimation = idleAnimation;
            movingRight = (samuraiX > x);

            if (attackCooldown <= 0) {
                isAttacking = true;
                attackAnimationTime = 0;
                currentAnimation = dashAnimation;
                stateTime = 0; // Reiniciar la animación
                if (Math.abs(samuraiY - y) < 300) {
                    sonidoDisparo.play();
                }
                shootProjectile();
                attackCooldown = ATTACK_COOLDOWN;
            }
        } else {
            if ((movingRight && x >= patrolEnd) || (!movingRight && x <= patrolStart)) {
                movingRight = !movingRight;
            }
            x += (movingRight ? speed : -speed) * delta;
            currentAnimation = runAnimation;
        }
    }

    private void shootProjectile() {
        if (projectileTexture == null || tipoEnemigo != 1) return;

        // Calcular posición objetivo (centro del samurai)
        float targetX = samuraiX + samuraiWidth/2;
        float targetY = samuraiY + samuraiHeight/2;

        // Posición inicial del proyectil (centro del enemigo)
        float projectileX = x + getWidth()/2;
        float projectileY = y + getHeight()/2 - 5;

        proyectiles.add(new Disparo(projectileX, projectileY, targetX, targetY,
            projectileTexture, PROJECTILE_WIDTH, PROJECTILE_HEIGHT, PROJECTILE_SPEED));
    }

    public Array<Disparo> getProyectiles() {
        return proyectiles;
    }


    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime);

        // Invertir imagen si cambia de dirección
        if (movingRight && currentFrame.isFlipX()) currentFrame.flip(true, false);
        if (!movingRight && !currentFrame.isFlipX()) currentFrame.flip(true, false);

        batch.draw(currentFrame, x, y);

        for (Disparo projectile : proyectiles) {
            projectile.render(batch);
        }
    }

    public void dispose() {
        if (projectileTexture != null) projectileTexture.dispose();
        for (Disparo projectile : proyectiles) {
            projectile.dispose();
        }
        deathSound.dispose();
        sonidoDisparo.dispose();
        sonidoDash.dispose();
    }

    // Llamar para marcar que el enemigo está muerto
    public void die() {
        isDead = true;
        currentAnimation = deadAnimation;
        deathSound.play();
        stateTime = 0; // Reiniciar el tiempo de la animación
        currentAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    // Comprobamos si el enemigo está muerto
    public boolean isDead() {
        return isDead;
    }

    public boolean checkCollisionsWithSamurai(float samuraiX, float samuraiY, float samuraiWidth, float samuraiHeight) {
        return samuraiX + samuraiWidth > x + 10 &&
            samuraiX < x + 10 + getWidth() - 80 &&
            samuraiY + samuraiHeight > y &&
            samuraiY < y + getHeight() - 40;
    }


    public boolean isDashing() {
        return isDashing;
    }
}
