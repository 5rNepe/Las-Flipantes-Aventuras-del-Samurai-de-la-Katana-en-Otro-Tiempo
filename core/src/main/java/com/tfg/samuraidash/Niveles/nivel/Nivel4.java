package com.tfg.samuraidash.Niveles.nivel;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;
import com.tfg.samuraidash.*;

import java.util.ArrayList;
import java.util.List;

public class Nivel4 implements Screen {
    // Variables para dibujar elementos en pantalla
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture, overlayTexture;
    private OrthographicCamera camera;

    // Variables para el mapa del nivel
    private TiledMap map; // El mapa cargado
    private TiledMapRenderer mapRenderer; // El renderizador para dibujar el mapa
    private MapObjects objects; // Objetos del mapa

    // Personajes y elementos del juego
    private Samurai samurai; // Nuestro personaje
    private Array<Plataforma> plataformas; // Plataformas con las que choca el samurai
    private Array<NPC> npcs; // Personajes no jugadores
    private Array<Enemigo> enemigos; // Enemigos del nivel

    // Variables para el fondo se mueva
    private float backgroundX = 0;
    private float backgroundSpeed = 10f;
    private float backgroundWidth = 640f;
    private float backgroundHeight = 480f;

    // Array de las cinematicas del nivel
    private Array<Cinematica> cinematicas;

    // Interfaz de usuario
    private HUDManager hudManager;
    private MenuPausa menuPausa;

    // Efectos visuales y transiciones
    private Texture circleTexture; // Textura para el efecto de círculo
    private float circleRadius = 0;
    private float maxCircleRadius;
    private boolean isDeathTransition = false; // Indica si estamos en transición de muerte
    private boolean isTransitionIn = false; // Indica la fase de la transición
    private float transitionDuration = 0.5f; // Duración de cada fase de la transición
    private float transitionTimer = 0; // Temporizador para la transición

    // Variables de estado del juego
    private boolean victoria = false; // Indica si hemos ganado
    int victoriaaa = 0; // Sirve para evitar errores en la victoria
    int cine = 0; // Sirve para evitar errores en la cinematica

    // Sonidos y música
    private Sound tpSonido; // Sonido de teletransporte
    private Music musicaFondo; // Música del nivel

    @Override
    public void show() {
        // Inicializamos todos los elementos del juego cuando se muestra la pantalla
        batch = new SpriteBatch();
        overlayTexture = new Texture(Gdx.files.internal("Overlays/1.png")); // Textura de overlay
        font = new BitmapFont(Gdx.files.internal("fonts/fuente.fnt")); // Fuente para texto
        backgroundTexture = new Texture("Fondos/fondoanochecer.png"); // Fondo del nivel

        // Configuramos la cámara
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);

        // Preparamos la transición de círculo
        circleTexture = new Texture(Gdx.files.internal("circle.png")); // Cargo textura de circulo
        maxCircleRadius = (float) Math.sqrt(camera.viewportWidth * camera.viewportWidth +
            camera.viewportHeight * camera.viewportHeight) / 2;

        // Cargar el mapa de Tiled
        map = new TmxMapLoader().load("Nivel4.tmx");  // Aquí cargamos el archivo .tmx
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);
        objects = map.getLayers().get("CapaObjetos").getObjects();

        // Inicializamos arrays y personaje
        npcs = new Array<>();
        enemigos = new Array<>();
        cinematicas = new Array<>();
        samurai = new Samurai(1600, 12000); // Posición inicial del samurai
        plataformas = new Array<>();

        // Cargamos colisiones
        loadCollisionsFromTiles();

        //Creamos las cinematicas que usara el nivel a partir de sus propias clases
        Cinematica cine2 = new Cinematica(samurai); //Creo cinematica
        //Creo NPCs, sin textura para que sean dialogos
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai?", new Array<String>(new String[]{
            "...Al fin te decides a mirarte al espejo."
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "Que... que eres?"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai?", new Array<String>(new String[]{
            "Lo que tu seras. El precio de cruzar tantos portales... de desafiar el tiempo."
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Narrador", new Array<String>(new String[]{
            "(Aprieta su katana, que gotea una sustancia negra)"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai?", new Array<String>(new String[]{
            "Los cientificos querian armas, pero el verdadero poder estaba en nosotros."
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai?", new Array<String>(new String[]{
            "En cada decision que tomaste... en cada vida que arrebataste."
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "No! Yo solo busque justicia... salvar mi hogar."
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai?", new Array<String>(new String[]{
            "Y por eso lo perdiste todo!"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Narrador", new Array<String>(new String[]{
            "(Observa las realidades colapsando)"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai?", new Array<String>(new String[]{
            "Mira! Cada mundo que pisaste quedo en llamas."
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai?", new Array<String>(new String[]{
            "Fuiste un titere... igual que esos ronin, igual que los cientificos."
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Narrador", new Array<String>(new String[]{
            "(Se acerca y te susurra)"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai?", new Array<String>(new String[]{
            "Pero yo... yo soy libre."
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Narrador", new Array<String>(new String[]{
            "(En un movimiento brutal, clava su katana en el abdomen del samurai)"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Narrador", new Array<String>(new String[]{
            "(La hoja perfora la armadura con un crujido metalico)"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Narrador", new Array<String>(new String[]{
            "(El samurai tose sangre, cayendo de rodillas)"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Narrador", new Array<String>(new String[]{
            "(El Samurai intenta hablar, pero solo escupe un hilo carmesi)"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Narrador", new Array<String>(new String[]{
            "(Su vision se nubla: las realidades colapsan en espirales de fuego y ruinas)"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Narrador", new Array<String>(new String[]{
            "(El Samurai? retira la hoja y lo empuja al vacio. Todo se oscurece...)"
        }), camera));
        //Creo una lista de strings para que sea texto por pantalla
        List<String> textos = new ArrayList<>();
        textos.add("Silencio. Frio. Luego... una luz dorada atraviesa las sombras.");
        textos.add("Algo caliente gotea en su rostro: lluvia? sangre?");
        textos.add("Los sonidos regresan distorsionados... pasos, voces lejanas.");
        textos.add("El samurai parpadea, enfocando con dificultad");
        textos.add("De fondo escucha: Otro mas! Este esta vivo!");
        textos.add("CONTINUARA");
        textos.add("Gracias por Jugar");
        textos.add("Juego desarrollado por Hugo para LechugaProductions");
        //Añado a la cinematica las acciones, de caminar, los npcs como textos y la pantalla negra con los textos de la lista
        cine2.agregarAccion(new Caminar(samurai, 2.6f, false));
        cine2.agregarAccion(new Dialogar(npcs.get(0)));         // Iniciar el diálogo con el NPC
        cine2.agregarAccion(new Dialogar(npcs.get(1)));
        cine2.agregarAccion(new Dialogar(npcs.get(2)));
        cine2.agregarAccion(new Dialogar(npcs.get(3)));
        cine2.agregarAccion(new Dialogar(npcs.get(4)));
        cine2.agregarAccion(new Dialogar(npcs.get(5)));
        cine2.agregarAccion(new Dialogar(npcs.get(6)));
        cine2.agregarAccion(new Dialogar(npcs.get(7)));
        cine2.agregarAccion(new Dialogar(npcs.get(8)));
        cine2.agregarAccion(new Dialogar(npcs.get(9)));
        cine2.agregarAccion(new Dialogar(npcs.get(10)));
        cine2.agregarAccion(new Dialogar(npcs.get(11)));
        cine2.agregarAccion(new Dialogar(npcs.get(12)));
        cine2.agregarAccion(new Dialogar(npcs.get(13)));
        cine2.agregarAccion(new Dialogar(npcs.get(14)));
        cine2.agregarAccion(new Dialogar(npcs.get(15)));
        cine2.agregarAccion(new Dialogar(npcs.get(16)));
        cine2.agregarAccion(new Dialogar(npcs.get(17)));
        cine2.agregarAccion(new Dialogar(npcs.get(18)));
        cine2.agregarAccion(new PantallaNegraConTexto(2f, textos));
        //Añado la cinematica al array de cinematicas
        cinematicas.add(cine2);


        //Repetimos lo anterior para otra cinematica
        Cinematica cine3 = new Cinematica(samurai);
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "Estoy... en el pueblo."
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "Que esta pasando?"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "Tengo que arreglar todo esto."
        }), camera));
        cine3.agregarAccion(new Dialogar(npcs.get(19)));         // Iniciar el diálogo con el NPC
        cine3.agregarAccion(new Dialogar(npcs.get(20)));
        cine3.agregarAccion(new Dialogar(npcs.get(21)));
        cinematicas.add(cine3);

        //Repetimos lo anterior para otra cinematica
        Cinematica cine4 = new Cinematica(samurai);
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "Esto no es lo que fue... ni lo que deberia ser."
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "Alguien lo corrompio."
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "(Las llamas retroceden ante tus pasos, como si te reconocieran)"
        }), camera));
        cine4.agregarAccion(new Dialogar(npcs.get(22)));         // Iniciar el diálogo con el NPC
        cine4.agregarAccion(new Dialogar(npcs.get(23)));
        cine4.agregarAccion(new Dialogar(npcs.get(24)));
        cinematicas.add(cine4);

        //Repetimos lo anterior para otra cinematica
        Cinematica cine5 = new Cinematica(samurai);
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "Guerra. Hambre. Locura. Era esto lo que queriais?"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "(Aprietas la empunadura de la katana)"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "No"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "(Pisas el cristal de la pantalla. Se rompe...)"
        }), camera));
        cine5.agregarAccion(new Dialogar(npcs.get(25)));         // Iniciar el diálogo con el NPC
        cine5.agregarAccion(new Dialogar(npcs.get(26)));
        cine5.agregarAccion(new Dialogar(npcs.get(27)));
        cine5.agregarAccion(new Dialogar(npcs.get(28)));
        cinematicas.add(cine5);

        //Repetimos lo anterior para otra cinematica
        Cinematica cine6 = new Cinematica(samurai);
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "Las realidades se estan desmoronando"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "Si no me doy prisa no habra vuelta atras"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "NPCS/accion.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "(Te pones a correr)"
        }), camera));
        cine6.agregarAccion(new Dialogar(npcs.get(29)));         // Iniciar el diálogo con el NPC
        cine6.agregarAccion(new Dialogar(npcs.get(30)));
        cine6.agregarAccion(new Dialogar(npcs.get(31)));
        cinematicas.add(cine6);

        // Cargamos NPCs y como el nivel no tiene enemigos pero samurai necesita que le pases minimo un enemigo creamos un enemigo fuera de pantalla
        loadNPCs("niveles/Nivel4/Nivel4_NPC.txt");
        enemigos = new Array<>();
        enemigos.add(new Enemigo(0, 0, 100, samurai, 0, "NPCS/accion.png", 1,"NPCS/accion.png",2, "NPCS/accion.png",3, "NPCS/accion.png",4));

        // Configuramos HUD y menú de pausa, ademas de pausar el tiempo, este nivel no tiene limite de tiempo
        this.hudManager = new HUDManager(samurai, camera);
        samurai.setHUDManager(hudManager);
        hudManager.pauseTimer();
        menuPausa = new MenuPausa(this);

        // Cargamos sonidos
        tpSonido = Gdx.audio.newSound(Gdx.files.internal("Samurai/respawn.wav"));
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica/nivel4.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.3f);
        musicaFondo.play();
    }

    @Override
    public void render(float delta) {
        // Este método se ejecuta constantemente para actualizar y dibujar el juego

        // Controlamos que el delta no sea demasiado grande
        // delta = tiempo en segundos desde el último frame (como un reloj interno del juego)
        if (delta > 0.1f) {
            delta = 0.1f;
        }

        // Comprobamos si se pulsa ESC para pausar
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            musicaFondo.pause();
            menuPausa.toggle();
        }

        // Si el juego no está pausado
        if (!menuPausa.isVisible()) {
            if (victoriaaa == 0) {
                musicaFondo.play();
            }
            // Manejo de la transición cuando el samurai muere
            if (samurai.isDead() && !isDeathTransition) {
                isDeathTransition = true;
                isTransitionIn = true;
                transitionTimer = 0;
            }

            // Lógica de la transición
            if (isDeathTransition) {
                transitionTimer += delta;

                if (isTransitionIn) {
                    // Fase 1: Círculo cerrándose
                    circleRadius = maxCircleRadius * (transitionTimer / transitionDuration);
                    if (transitionTimer >= transitionDuration) {
                        isTransitionIn = false;
                        transitionTimer = 0;
                        // Aquí recargo los fondos
                        backgroundTexture = new Texture("Fondos/fondoanochecer.png");
                        overlayTexture = new Texture(Gdx.files.internal("Overlays/1.png"));
                    }

                } else {
                    // Fase 2: Círculo abriéndose
                    if(victoria){
                        // Si hay victoria, cambiamos a pantalla de victoria
                        Game game = (Game) Gdx.app.getApplicationListener();
                        game.setScreen(new PantallaVictoria(hudManager.getLevelTimer(), samurai.getNumerodeasesinatos(), samurai.getNumerodemuertes(), hudManager.getComboMultiplierMax()));
                    }
                    circleRadius = maxCircleRadius * (1 - (transitionTimer / transitionDuration));
                    if (transitionTimer >= transitionDuration) {
                        isDeathTransition = false;
                    }
                }
            }

            // Actualizamos HUD y cámara
            hudManager.update(delta);
            updateCamera();

            // Limpiamos pantalla
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            ScreenUtils.clear(0, 0, 0, 1);

            // Dibujamos el fondo con efecto de movimiento
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            backgroundX -= backgroundSpeed * delta;
            if (backgroundX <= -backgroundWidth) {
                backgroundX = 0;  // Reiniciar la posición del fondo si sale de pantalla
            }
            batch.setColor(1,1,1,1);
            batch.draw(
                backgroundTexture,
                camera.position.x - camera.viewportWidth / 2 + backgroundX,  // Desplazamiento de fondo en X
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );

            // Dibujar el fondo (segundo fondo, al lado del primero)
            batch.draw(
                backgroundTexture,
                camera.position.x - camera.viewportWidth / 2 + backgroundX + backgroundWidth,  // Colocar al lado del primer fondo
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );
            batch.end();

            // Dibujamos el mapa
            mapRenderer.setView(camera);
            mapRenderer.render(); // Este renderiza todas las capas del mapa
            for (MapLayer layer : map.getLayers()) {
                layer.setVisible(true);
            }

            // Dibujamos NPCs
            batch.begin();
            for (NPC npc : npcs) {
                npc.update(samurai);  // Actualizar el NPC

                // Interacción con el NPC al presionar "E"
                if (Gdx.input.isKeyJustPressed(Input.Keys.E) && npc.isNearPlayer()) {
                    npc.interact();  // Llama al método para hablar cuando el jugador está cerca
                }

                npc.render(batch);  // Dibujar el NPC y su bocadillo
            }

            // Recorro el array de cinematicas
            for (Cinematica c : cinematicas) {
                //Si estoy en cinematica no permito movimiento
                if (c.isEnCinematica()) {
                    c.actualizar(delta); // Actualiza solo la que está activa
                    break; // Detenemos el bucle en cuanto encontramos la activa
                }
                else {
                    //Llamo al movimiento del samurai para que funcionen los controles
                    samurai.InputMovimiento();
                }
            }

            // Renderizar al samurai
            samurai.update(delta, plataformas, enemigos); // Pasamos las plataformas como colisiones
            samurai.render(batch, samurai.getX(), samurai.getY(), camera);

            //Actualizo los enemigos y los renderizo
            for (Enemigo enemy : enemigos) enemy.update(delta, samurai.getX(), samurai.getY(), samurai.getWidth(), samurai.getHeight());
            for (Enemigo e : enemigos) e.render(batch);

            // Dibujamos overlay, aqui abajo para que este por encima del samurai
            batch.setColor(1,1,1,0.1f);
            batch.draw(
                overlayTexture,
                camera.position.x - camera.viewportWidth / 2,  // Desplazamiento de fondo en X
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );

            // Comprobamos triggers (zonas que tienen cinematicas o condiciones como victoria)
            checkTriggers(samurai);
            batch.end();

            // Dibujamos transición si es necesario
            batch.begin();
            if (isDeathTransition) {
                // Dibujo el circulo negro que se va abriendo
                batch.setColor(0, 0, 0, 1);
                batch.draw(circleTexture,
                    camera.position.x - circleRadius,
                    camera.position.y - circleRadius,
                    circleRadius * 2,
                    circleRadius * 2);
                batch.setColor(1, 1, 1, 1);
            }
            batch.end();
        }

        // Dibujamos menú de pausa si está activo
        batch.begin();
        menuPausa.render(batch);
        batch.end();
    }


    // Actualiza la posición de la cámara para seguir al samurai
    public void updateCamera() {
        camera.position.set(samurai.getX() + 80, samurai.getY() + 80, 0);  // Centrar la cámara en el jugador
        camera.update();  // Actualizar la cámara para aplicar los cambios
    }

    @Override
    public void resize(int width, int height) {
        // Ajustamos la cámara al cambiar tamaño de ventana
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        backgroundWidth = width;
        backgroundHeight = height;
        // y el menu de pausa
        menuPausa.resize(width, height);
    }

    //Estos son metodos de LibGDX para esconder cosas, pausar la pantalla y salir de la pausa, en esta pantalla no los utilizo
    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    //Sirve para liberar los recursos, con esto cuando salimos de la pantalla los recursos que utilizamos salen de la memoria para no ocupar espacio en la memoria
    @Override
    public void dispose() {
        // Liberamos recursos cuando ya no se necesitan
        batch.dispose();
        font.dispose();
        map.dispose();
        backgroundTexture.dispose();
        menuPausa.dispose();
        overlayTexture.dispose();
        musicaFondo.dispose();
    }

    // Carga NPCs desde archivo
    private void loadNPCs(String filePath) {
        FileHandle file = Gdx.files.internal(filePath);
        String[] lines = file.readString().split("\n");
        for (String line : lines) {
            String[] data = line.split(",");
            if(data[6] != null) {
                npcs.add(new NPC(samurai, Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2], Integer.parseInt(data[3]), 0.1f, Animation.PlayMode.LOOP, data[4], new Array<>(data[5].split(";")), camera, data[6]));
            } else {
                npcs.add(new NPC(samurai, Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2], Integer.parseInt(data[3]), 0.1f, Animation.PlayMode.LOOP, data[4], new Array<>(data[5].split(";")), camera));
            }

        }
    }

    // Método para cargar las colisiones desde las capas en el mapa de Tiled
    private void loadCollisionsFromTiles() {
        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
                int tileWidth = tileLayer.getTileWidth();
                int tileHeight = tileLayer.getTileHeight();

                // Recorremos todas las celdas del mapa
                for (int x = 0; x < tileLayer.getWidth(); x++) {
                    for (int y = 0; y < tileLayer.getHeight(); y++) {
                        TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                        if (cell != null) {
                            TiledMapTile tile = cell.getTile();
                            if (tile != null && tile.getObjects().getCount() > 0) {
                                // Añadimos plataformas para las colisiones
                                for (MapObject object : tile.getObjects()) {
                                    if (object instanceof RectangleMapObject) {
                                        Rectangle rect = ((RectangleMapObject) object).getRectangle();
                                        float platX = x * tileWidth + rect.x;
                                        float platY = y * tileHeight + rect.y;
                                        float platWidth = rect.width > 0 ? rect.width : tileWidth;
                                        float platHeight = rect.height > 0 ? rect.height : tileHeight;

                                        boolean alreadyExists = false;
                                        for (Plataforma p : plataformas) {
                                            if (p.getBounds().x == platX && p.getBounds().y == platY) {
                                                alreadyExists = true;
                                                break;
                                            }
                                        }

                                        // Evitamos duplicados
                                        if (!alreadyExists) {
                                            plataformas.add(new Plataforma(platX, platY, platWidth, platHeight));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Comprueba si el samurai entra en zonas especiales del mapa
    public void checkTriggers(Samurai samurai) {
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                if (samurai.getBounds().overlaps(rect)) {
                    String name = object.getName();
                    if (name.equals("tp")){
                        // Teletransporte a otra zona
                        tpSonido.play();
                        backgroundTexture = new Texture("Fondos/city 6/7.png");
                        overlayTexture = new Texture(Gdx.files.internal("Overlays/9.png"));
                        samurai.setX(1184);
                        samurai.setY(10656);
                    } else if (name.equals("tp1")){
                        // Teletransporte a otra zona
                        tpSonido.play();
                        backgroundTexture = new Texture("Fondos/fondopartida.png");
                        overlayTexture = new Texture(Gdx.files.internal("Overlays/12.png"));
                        samurai.setX(1184);
                        samurai.setY(9376);
                    } else if (name.equals("tp2")){
                        // Teletransporte a otra zona
                        tpSonido.play();
                        backgroundTexture = new Texture("Fondos/nature_2/orig.png");
                        overlayTexture = new Texture(Gdx.files.internal("Overlays/3.png"));
                        samurai.setX(896);
                        samurai.setY(8352);
                    } else if (name.equals("tp3")){
                        // Teletransporte a otra zona
                        tpSonido.play();
                        backgroundTexture = new Texture("Fondos/city 5/6.png");
                        overlayTexture = new Texture(Gdx.files.internal("Overlays/19.png"));
                        samurai.setX(1216);
                        samurai.setY(6944);
                    } else if (name.equals("tp4")){
                        // Teletransporte a otra zona
                        tpSonido.play();
                        backgroundTexture = new Texture("Fondos/winter 3/4.png");
                        overlayTexture = new Texture(Gdx.files.internal("Overlays/6.png"));
                        samurai.setX(1312);
                        samurai.setY(5120);
                    } else if (name.equals("tp5")){
                        // Teletransporte a otra zona
                        tpSonido.play();
                        backgroundTexture = new Texture("Fondos/FondoLobby/1.png");
                        overlayTexture = new Texture(Gdx.files.internal("Overlays/21.png"));
                        samurai.setX(1520);
                        samurai.setY(3040);
                    } else if (name.equals("tp6")){
                        // Teletransporte a otra zona
                        tpSonido.play();
                        backgroundTexture = new Texture("Fondos/fondoanochecer.png");
                        overlayTexture = new Texture(Gdx.files.internal("Overlays/1.png"));
                        samurai.setX(4352);
                        samurai.setY(11488);
                    } else if (name.equals("tp7")){
                        // Teletransporte a otra zona
                        tpSonido.play();
                        backgroundTexture = new Texture("Fondos/city 7/6.png");
                        overlayTexture = new Texture(Gdx.files.internal("Overlays/22.png"));
                        samurai.setX(3424);
                        samurai.setY(10144);
                    } else if (name.equals("tp8")){
                        // Teletransporte a otra zona
                        tpSonido.play();
                        backgroundTexture = new Texture("Fondos/Ciudades2/city 1/12.png");
                        overlayTexture = new Texture(Gdx.files.internal("Overlays/5.png"));
                        samurai.setX(3776);
                        samurai.setY(8000);
                    } else if (name.equals("cinefinal") && cine == 0 && !cinematicas.get(0).isEnCinematica()) {
                        // Inicia la cinematica
                        musicaFondo.stop();
                        cine = 1;
                        cinematicas.get(0).iniciar();
                    } else if (name.equals("cine") && !cinematicas.get(1).isEnCinematica()) {
                        // Inicia la cinematica
                        cinematicas.get(1).iniciar();
                    } else if (name.equals("cine2") && !cinematicas.get(2).isEnCinematica()) {
                        // Inicia la cinematica
                        cinematicas.get(2).iniciar();
                    } else if (name.equals("cine3") && !cinematicas.get(3).isEnCinematica()) {
                        // Inicia la cinematica
                        cinematicas.get(3).iniciar();
                    } else if (name.equals("cine4") && !cinematicas.get(4).isEnCinematica()) {
                        // Inicia la cinematica
                        cinematicas.get(4).iniciar();
                    } else if (name.equals("Muerte")) {
                        // El samurai muere
                        samurai.die();
                    } else if (name.equals("Victoria") && victoriaaa == 0 && !cinematicas.get(0).isEnCinematica()) {
                        // El jugador completa el nivel
                        victoriaaa = 1;
                        isDeathTransition = true;
                        isTransitionIn = true;
                        transitionTimer = 0;
                        victoria = true;
                        saveGameProgress(4);
                        musicaFondo.stop();
                    }
                }
            }
        }
    }

    // Guarda el progreso del juego
    private void saveGameProgress(int nivelMaxCompletado) {
        Preferences prefs = Gdx.app.getPreferences("GameSettings");
        int selectedSlot = prefs.getInteger("selectedSlot", 0);

        FileHandle file = Gdx.files.local("partida" + (selectedSlot + 1) + ".json");
        Datos gameData = loadGameData(selectedSlot);
        if(gameData.getNivelMaxCompletado() > nivelMaxCompletado){
            // No hacemos nada si ya teníamos un nivel más alto completado
        } else {
            gameData.setNivelMaxCompletado(nivelMaxCompletado);
        }

        String json = new Json().toJson(gameData);
        file.writeString(json, false);
    }

    // Carga los datos de partida guardada
    private Datos loadGameData(int slot) {
        FileHandle file = Gdx.files.local("partida" + (slot + 1) + ".json");
        if (file.exists()) {
            String json = file.readString();
            return new Json().fromJson(Datos.class, json);
        }
        return null;
    }

}
