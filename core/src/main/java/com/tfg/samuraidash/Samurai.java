package com.tfg.samuraidash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Random;
import java.util.Vector;

public class Samurai {
    //Aqui declaro todas las variables que utilizare en la clase para hacer que funcione
    //Variables para las animaciones
    private Animation<TextureRegion> runAnimation, idleAnimation, jumpAnimation, deadAnimation, runFasterAnimation;
    private Animation<TextureRegion>[] attackAnimations;
    private Animation<TextureRegion> currentAnimation;
    private Animation<TextureRegion> currentAttackAnimation;
    private Texture attackTexture1, attackTexture2, attackTexture3;

    //Variables para comprobaciones dentro del codigo
    private boolean isRunning = false; //Comprueba su el samurai esta corriendo
    private boolean isDashing = false; //Comprueba su el samurai esta dasheando
    private float stateTime = 0; //Variable encargada de actualizar los ticks (la pantalla) para que se mueva
    private boolean facingLeft = false; //Comprueba su el samurai esta mirando a la izquierda
    private float x, y; //Coordenadas del samurai
    private float gx, gy; //Coordenadas del inicio del nivel
    private Array<Particulas> particula; //Lista para guardar particulas
    private Texture texturadeparticula; //Textura de las particulas
    private Particulas ultimaParticula = null; //Ultima particula generada
    private float velocityY = 0; // Velocidad vertical para el movimiento
    private float velocityX = 0; // Velocidad horizontal para el movimiento
    private final float GRAVITY = -800;  // Gravedad aplicada al samurái
    private float JUMP_FORCE = 400;  // Potencia del salto
    private float RUN_SPEED = 130;  // Velocidad de movimiento horizontal
    private boolean isOnGround = true;  // Para verificar si está en el suelo
    private float DASH_SPEED = 400; // Velocidad del dash
    private float dashTime = 0; //Tiempo que esta dasheando
    private final float DASH_DURATION = 0.2f; // Duración del dash

    private boolean isInDialogue = false;
    private boolean movimientoHabilitado = true;
    private boolean isDead = false;

    private Texture bloodAnimationTexture;
    private Animation<TextureRegion> bloodAnimation;
    private float bloodAnimationTime = 0;
    private boolean showingBlood = false;
    private Sound deathSound, respawnSound, sonidoDash, sonidoBloqueo;
    private float respawnTimer = 0;
    private final float RESPAWN_DELAY = 0.5f; // Tiempo antes de respawnear

    private float frenzyCharge = 0; // 0-100%
    private boolean frenzyActive = false;
    private float frenzyDuration = 5f; // 5 segundos de duración
    private float frenzyTimer = 0;
    private Texture frenzyOverlay;
    private BitmapFont font;

    private float stamina = 1.0f;
    private final float MAX_STAMINA = 1.0f;
    private final float STAMINA_DEPLETION_RATE = 0.5f; // Por segundo al sprintear
    private final float STAMINA_RECOVERY_RATE = 0.3f; // Por segundo al no sprintear
    private boolean isSprinting = false;

    private final float STAMINA_MIN_TO_SPRINT = 0.2f;  // Mínimo de estamina necesario para sprintar
    private boolean wasSprinting = false; // Para detectar cuando dejamos de sprintar
    private HUDManager hudManager;
    private int numerodemuertes = 0;
    private int numerodeasesinatos = 0;
    private boolean isBlocking = false;
    private final float BLOCK_STAMINA_COST = 0.5f; // 50% de la estamina por bloqueo
    private Animation<TextureRegion> blockAnimation;


    public Samurai(float x, float y) {
        //Aqui en el constructor inicializo todas las variables para cuando creo el objeto que tenga definido
        //Coordenadas Samurai
        this.x = x;
        this.y = y;
        //Coordenadas Inicio nivel
        gx = x;
        gy = y;
        //Cargo todas las animaciones a partir de una funcion que tengo creada en la clase
        blockAnimation = loadAnimation("Shield.png", 2, 0.1f, Animation.PlayMode.NORMAL);
        runAnimation = loadAnimation("Walk.png", 8, 0.1f, Animation.PlayMode.LOOP);
        runFasterAnimation = loadAnimation("Run.png", 8, 0.1f, Animation.PlayMode.LOOP);
        idleAnimation = loadAnimation("Idle.png", 6, 0.2f, Animation.PlayMode.LOOP);
        jumpAnimation = loadAnimation("Jump.png", 12, 0.15f, Animation.PlayMode.NORMAL);
        deadAnimation = loadAnimation("Dead.png", 3, 0.2f, Animation.PlayMode.NORMAL);
        //Estado base de animacion en idle(Quieto)
        currentAnimation = idleAnimation;
        //Creo la textura de las particulas y su lista
        texturadeparticula = new Texture("10.png");
        particula = new Array();

        //Creo las texuras de los tres tipos de ataque
        attackTexture1 = new Texture("Attack_1.png");
        attackTexture2 = new Texture("Attack_2.png");
        attackTexture3 = new Texture("Attack_3.png");

        //Creo una animacion de 3 frames de largo y le meto las texturas convertidas en animacion a partir de la funcion
        attackAnimations = new Animation[3];
        attackAnimations[0] = loadAnimation(attackTexture1, 6, 0.5f, Animation.PlayMode.NORMAL);
        attackAnimations[1] = loadAnimation(attackTexture2, 4, 0.5f, Animation.PlayMode.NORMAL);
        attackAnimations[2] = loadAnimation(attackTexture3, 3, 0.5f, Animation.PlayMode.NORMAL);

        //Valores para la velocidad de correr, dashear y saltar ademas de carga de textura para el estado frenesi
        frenzyOverlay = new Texture("Overlays/Estado_Frenesi.jpg");
        RUN_SPEED = 130;
        DASH_SPEED = 400;
        JUMP_FORCE = 400;

        // Crear fuente básica
        font = new BitmapFont();
        font.getData().setScale(1.5f); // Tamaño de fuente
        font.setColor(Color.WHITE); //Color blanco

        //Creo la textura de la sangre y cargo todos los sonidos necesarios para el samurai
        bloodAnimationTexture = new Texture("Samurai/sangre.png");
        bloodAnimation = loadAnimation(bloodAnimationTexture, 4, 0.1f, Animation.PlayMode.NORMAL);
        deathSound = Gdx.audio.newSound(Gdx.files.internal("Samurai/death.mp3"));
        respawnSound = Gdx.audio.newSound(Gdx.files.internal("Samurai/respawn.wav"));
        sonidoDash = Gdx.audio.newSound(Gdx.files.internal("Samurai/dash.mp3"));
        sonidoBloqueo = Gdx.audio.newSound(Gdx.files.internal("Samurai/bloqueo.mp3"));
    }

    public void setHUDManager(HUDManager hudManager) {
        this.hudManager = hudManager;
    }

    public float getStamina() {
        return stamina;
    }

    public boolean canSprint() {
        // Permitir sprintar solo si tenemos suficiente estamina
        return stamina >= STAMINA_MIN_TO_SPRINT;
    }

    //Metodo encargado de actualizar en tiempo real la estamina
    private void updateStamina(float delta) {
        if (isBlocking) {
            // No recuperar estamina mientras se bloquea
            return;
        }
        // Detección automática cuando soltamos Shift
        boolean shiftReleased = wasSprinting && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        //Si no presionamos shift no estamos corriendo volvemos a lo normal
        if (shiftReleased) {
            isSprinting = false;
            RUN_SPEED = 130; // Volver a velocidad normal inmediatamente
        }

        // Actualización de estamina
        if (isSprinting && isRunning) {
            //Esta linea es la encargada de bajar la estamina cuando estamos corriendo, el minimo siendo 0
            //y el maximo 1, el cual si estamos corriendo le vamos restando 0.5 multiplicado por delta que es el tiempo
            stamina = Math.max(0, stamina - STAMINA_DEPLETION_RATE * delta);

            // Auto-detener el sprint si la estamina es muy baja
            if (stamina <= 0) {
                stamina = 0;
                isSprinting = false;
                RUN_SPEED = 130;
            }
        } else {
            // Recuperar estamina más rápido si no estamos sprintando
            float recoveryRate = isRunning ? STAMINA_RECOVERY_RATE : STAMINA_RECOVERY_RATE * 1.5f;
            //Lo mismo que antes pero en lugar de gastar ganamos poniendo de minimo el maximo de estamina que se puede tener
            //y recurandola por 0.3 por tiempo
            stamina = Math.min(MAX_STAMINA, stamina + recoveryRate * delta);
        }

        wasSprinting = isSprinting; // Guardar estado para el próximo frame
    }

    //Metodo encargada de obtener los limites de samurai para poder realizar las colisiones
    public Rectangle getBounds() {
        return new Rectangle(x, y, getWidth(), getHeight());
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return 51;
    }

    public float getHeight() {
        return 80;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isOnGround() {
        return isOnGround;
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
        anim.setPlayMode(mode);  // Aquí aplicamos el modo de animación
        return anim;
    }

    //Metodo encargado de iniciar el dash
    public void startDash() {
        //Si esta en el suelo puede dashear
        if (isOnGround == true) {
            isDashing = true;
            sonidoDash.play(); //Suena el sonido del dash
            dashTime = DASH_DURATION; //El tiempo que esta dasheando es la duracion del dash
        }

    }

    //Metodo encargado de parar el dash
    public void stopDash() {
        isDashing = false;
        currentAnimation = idleAnimation;  // Volver a la animación idle después del dash
    }

    //El metodo render que es el encargado de renderizar en cada frame todo lo que le pongamos
    public void render(SpriteBatch batch, float x, float y, OrthographicCamera camera) {
        if (showingBlood) {
            TextureRegion bloodFrame = bloodAnimation.getKeyFrame(bloodAnimationTime);
            batch.draw(bloodFrame, x - bloodFrame.getRegionWidth()/2 + getWidth()/2, y - bloodFrame.getRegionHeight()/2 + getHeight()/2);
        }
        for (Particulas particle : particula) {
            particle.render(batch);
        }
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime);

        // Invertir la imagen si va a la izquierda
        if (facingLeft && !currentFrame.isFlipX()) currentFrame.flip(true, false);
        if (!facingLeft && currentFrame.isFlipX()) currentFrame.flip(true, false);

        batch.draw(currentFrame, x, y, currentFrame.getRegionWidth() * 0.9f, currentFrame.getRegionHeight() * 0.9f);

        if (frenzyActive) {
            batch.setColor(1, 0.5f, 0.5f, 0.3f);
            batch.draw(frenzyOverlay,
                camera.position.x - camera.viewportWidth/2,
                camera.position.y - camera.viewportHeight/2,
                camera.viewportWidth, camera.viewportHeight);
            batch.setColor(1, 1, 1, 1);
        }

    }

    public void setRunning(boolean running) {
        if (!isDashing) {
            this.isRunning = running;
            if (running) {
                currentAnimation = runAnimation;
                velocityX = facingLeft ? -RUN_SPEED : RUN_SPEED; // Movimiento horizontal al correr
            } else {
                currentAnimation = idleAnimation;
                velocityX = 0;  // Detener movimiento horizontal cuando no está corriendo
            }
        }
    }

    public void setFacingLeft(boolean left) {
        if (!isDashing) {
            this.facingLeft = left;
        }
    }

    public boolean isDashing() {
        return isDashing;
    }

    private boolean dashAnimationChanged = false;
    public void update(float delta, Array<Plataforma> plataformas, Array<Enemigo> enemigos) {
        if (isDead) {
            currentAnimation = deadAnimation;
            respawnTimer -= delta;
            bloodAnimationTime += delta;

            // Ocultar sangre después de que termine la animación
            if (bloodAnimation.isAnimationFinished(bloodAnimationTime)) {
                showingBlood = false;
            }

            if (respawnTimer <= 0) {
                respawn();
            }
            return;
        }
        if (!movimientoHabilitado || isInDialogue) return;

        // Verificar colisión con proyectiles
        for (Enemigo enemigo : enemigos) {
            if (enemigo.getProyectiles() == null){
                return;
            }
            for (Disparo projectile : enemigo.getProyectiles()) {
                if (projectile.collidesWith(this)) {
                    if (isBlocking) {
                        // Bloquear el proyectil
                        sonidoBloqueo.play();
                        stamina -= BLOCK_STAMINA_COST;
                        if (stamina < 0) stamina = 0;
                        projectile.dispose();
                        enemigo.getProyectiles().removeValue(projectile, true);
                    } else {
                        if(!isFrenzyActive()) {
                            die();
                        }
                    }
                    break;
                }
            }
        }

        updateStamina(delta);

        if (frenzyActive) {
            frenzyTimer -= delta;
            if (frenzyTimer <= 0) {
                endFrenzyMode();
            }
        }

        for (Enemigo enemigo : enemigos) {
            if (!enemigo.isDead() && enemigo.checkCollisionsWithSamurai(x, y, getWidth(), getHeight())) {
                if (isDashing) {
                    // Si el samurai está en dash, mata al enemigo
                    enemigo.die();
                    numerodeasesinatos += 1;

                    if (!frenzyActive) {
                        hudManager.addCombo();
                        addFrenzyCharge(10); // +10% por enemigo eliminado
                    }
                } else if (enemigo.isDashing()) {
                    if(!frenzyActive) {
                        die();
                    }
                }
            }
        }

        // Aplicar velocidad del Dash
        if (isDashing) {
            velocityX = (facingLeft ? -DASH_SPEED : DASH_SPEED);
            dashTime -= delta;
            if (dashTime <= 0) {
                stopDash();
                dashAnimationChanged = false;
            }
        } else {
            velocityX = isRunning ? (facingLeft ? -RUN_SPEED : RUN_SPEED) : 0;
        }

        // Aplicar gravedad
        velocityY += GRAVITY * delta;

        // ---  COLISIÓN EN X (PAREDES) ---
        float nextX = x + velocityX * delta;
        boolean colisionX = false;
        Plataforma plataformaColisionX = null;

        for (Plataforma plataforma : plataformas) {
            if (plataforma.isCollidingWithPlayer(nextX, y, getWidth(), getHeight())) {
                colisionX = true;
                plataformaColisionX = plataforma;
                break;
            }
        }

        if (!colisionX) {
            x = nextX;
        } else {
            //  Mover solo hasta el borde de la pared
            if (velocityX > 0) {
                x = plataformaColisionX.getBounds().x - getWidth();
            } else {
                x = plataformaColisionX.getBounds().x + plataformaColisionX.getBounds().width;
            }

            //  Evita atravesar la pared con el cambio rápido de dirección
            velocityX = 0; // Detener el movimiento en esa dirección

        //  Aplica deslizamiento (reducir velocidad de caída sin quedarse pegado)
            velocityY = Math.max(velocityY, -150);
        }

        // ---  COLISIÓN EN Y (SUELO Y TECHO) ---
        float nextY = y + velocityY * delta;
        boolean colisionY = false;
        Plataforma plataformaColisionY = null;

        for (Plataforma plataforma : plataformas) {
            if (plataforma.isCollidingWithPlayer(x, nextY, getWidth(), getHeight())) {
                colisionY = true;
                plataformaColisionY = plataforma;
                break;
            }
        }

        if (colisionY && plataformaColisionY != null) {
            if (velocityY > 0) {
                y = plataformaColisionY.getBounds().y - getHeight();
            } else {
                y = plataformaColisionY.getBounds().y + plataformaColisionY.getBounds().height;
                isOnGround = true;
                velocityY = 0;
            }
        } else {
            y = nextY;
            isOnGround = false;
        }

        //  Evita atravesar el suelo si la velocidad es demasiado alta
        if (!isOnGround) {
            velocityY = Math.max(velocityY, -500);
        }


        // Selección de animación basada en el estado
        if (!isOnGround) {
            currentAnimation = jumpAnimation;  // Si no está en el suelo, mostrar la animación de salto
        } else if (isBlocking) {
            currentAnimation = blockAnimation;
        } else if (isDashing) {
            if (!dashAnimationChanged) {
                // Al empezar el dash, seleccionamos una animación de ataque aleatoria
                Random rand = new Random();
                currentAttackAnimation = attackAnimations[rand.nextInt(3)];
                currentAnimation = currentAttackAnimation;
                dashAnimationChanged = true;  // Marcamos que ya se cambió la animación
            }
        } else if (isRunning) {
            currentAnimation = (isSprinting || isFrenzyActive()) ? runFasterAnimation : runAnimation;
        } else {
            currentAnimation = idleAnimation;  // Si no está haciendo nada, mostrar la animación idle
        }

        if (isRunning && isOnGround && !isDashing) {
            boolean canSpawnNewParticle = (ultimaParticula == null || ultimaParticula.getCurrentFrame() >= 4 || ultimaParticula.isFinished());

            if (canSpawnNewParticle) {
                float offsetX = facingLeft ? 60 : 5;
                float offsetY = -5; // Ajustar para que salga desde los pies
                Particulas newParticle = new Particulas(texturadeparticula, x + offsetX, y + offsetY, facingLeft, 0.1f);
                particula.add(newParticle);
                ultimaParticula = newParticle;
            }
        }

        for (int i = particula.size - 1; i >= 0; i--) {
            particula.get(i).update(delta);
            if (particula.get(i).isFinished()) {
                particula.removeIndex(i);
            }
        }



        x += velocityX * delta;

    }


    public void jump() {
        if (isOnGround) {
            velocityY = JUMP_FORCE;
            isOnGround = false;
            currentAnimation = jumpAnimation;
        }
    }

    public void respawn() {
        isDead = false;
        respawnSound.play();
        x = gx; // Posición inicial X
        y = gy; // Posición inicial Y
        velocityX = 0;
        velocityY = 0;
        showingBlood = false;
    }


    public void InputMovimiento(){
        if (Gdx.input.isKeyPressed(Input.Keys.E) && !isDashing && isOnGround()) {
            if(stamina < 0.5f){
                isBlocking = false;
            } else {
                setRunning(false);
                isSprinting = false;
                isBlocking = true;
            }
        } else {
            isBlocking = false;
        }
        if (isBlocking) return;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            setRunning(true);
            setFacingLeft(false);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            setRunning(true);
            setFacingLeft(true);
        } else {
            setRunning(false);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && isOnGround()) {
            jump();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isDashing()) {
            startDash();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) && !isSprinting) {
            activateFrenzy();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && canSprint() && !isFrenzyActive()) {
            isSprinting = true;
            RUN_SPEED = 130 * 1.8f;
        }

    }

    public boolean isDead() {
        return isDead;
    }

    public void die() {
        if (!isDead) {
            isDead = true;
            numerodeasesinatos = 0;
            numerodemuertes += 1;
            deathSound.play();
            currentAnimation = deadAnimation;
            showingBlood = true;
            bloodAnimationTime = 0;
            frenzyCharge = 0;
            endFrenzyMode();
            stamina = 1.0f;
            respawnTimer = RESPAWN_DELAY;
        }
    }


    public void dispose() {
        attackTexture1.dispose();
        attackTexture2.dispose();
        attackTexture3.dispose();
        deathSound.dispose();
        respawnSound.dispose();
        bloodAnimationTexture.dispose();
        deathSound.dispose();
        sonidoDash.dispose();
        sonidoBloqueo.dispose();
    }

    public void setInDialogue(boolean inDialogue) {
        this.isInDialogue = inDialogue;
    }

    public void addFrenzyCharge(float amount) {
        if (!frenzyActive) {
            frenzyCharge = Math.min(100, frenzyCharge + amount);
        }
    }

    public void activateFrenzy() {
        if (frenzyCharge >= 100 && !frenzyActive) {
            frenzyActive = true;
            frenzyTimer = frenzyDuration;
            frenzyCharge = 0;

            RUN_SPEED = 300;
            DASH_SPEED = 600;
            JUMP_FORCE = 500;
        }
    }

    private void endFrenzyMode() {
        frenzyActive = false;

        RUN_SPEED = 130;
        DASH_SPEED = 400;
        JUMP_FORCE = 400;
    }

    public boolean isFrenzyActive() {
        return frenzyActive;
    }

    public float getFrenzyCharge() {
        return frenzyCharge;
    }

    public boolean isInDialogue() {
        return isInDialogue;
    }

    public int getNumerodemuertes() {
        return numerodemuertes;
    }

    public int getNumerodeasesinatos() {
        return numerodeasesinatos;
    }

}
