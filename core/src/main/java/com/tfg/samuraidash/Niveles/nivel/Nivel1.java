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

public class Nivel1 implements Screen {
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

    // Sonidos y música
    private Sound tpSonido; // Sonido de teletransporte
    private Music musicaFondo; // Música del nivel

    @Override
    public void show() {
        // Inicializamos todos los elementos del juego cuando se muestra la pantalla
        batch = new SpriteBatch();
        overlayTexture = new Texture(Gdx.files.internal("Overlays/6.png")); // Textura de overlay
        font = new BitmapFont(Gdx.files.internal("fonts/fuente.fnt")); // Fuente para texto
        backgroundTexture = new Texture("Fondos/city 1/6.png"); // Fondo del nivel

        // Configuramos la cámara
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);

        // Preparamos la transición de círculo
        circleTexture = new Texture(Gdx.files.internal("circle.png")); // Cargo textura de circulo
        maxCircleRadius = (float) Math.sqrt(camera.viewportWidth * camera.viewportWidth +
            camera.viewportHeight * camera.viewportHeight) / 2;

        // Cargar el mapa de Tiled
        map = new TmxMapLoader().load("Nivel1.tmx");  // Aquí cargamos el archivo .tmx
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);
        objects = map.getLayers().get("CapaObjetos").getObjects();

        // Inicializamos arrays y personaje
        npcs = new Array<>();
        enemigos = new Array<>();
        samurai = new Samurai(736, 12160); // Posición inicial del samurai
        plataformas = new Array<>();

        // Cargamos colisiones, enemigos y NPCs
        loadCollisionsFromTiles();
        loadEnemigos("niveles/Nivel1_1/Nivel1_Enemigos.txt");
        loadNPCs("niveles/Nivel1_1/Nivel1_NPC.txt");

        // Configuramos HUD y menú de pausa
        this.hudManager = new HUDManager(samurai, camera);
        samurai.setHUDManager(hudManager);
        menuPausa = new MenuPausa(this);

        // Cargamos sonidos
        tpSonido = Gdx.audio.newSound(Gdx.files.internal("Samurai/respawn.wav"));
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica/nivel1.mp3"));
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
                        // Aquí recargo los enemigos mientras la pantalla está completamente negra y tambien los fondos
                        enemigos.clear();
                        loadEnemigosAsync();
                        backgroundTexture = new Texture("Fondos/city 1/6.png");
                        overlayTexture = new Texture(Gdx.files.internal("Overlays/6.png"));
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

            //Actualizo los enemigos y los renderizo
            for (Enemigo enemy : enemigos) enemy.update(delta, samurai.getX(), samurai.getY(), samurai.getWidth(), samurai.getHeight());
            for (Enemigo e : enemigos) e.render(batch);

            //Llamo al movimiento del samurai para que funcionen los controles
            samurai.InputMovimiento();

            // Renderizar al samurai
            samurai.update(delta, plataformas, enemigos); // Pasamos las plataformas como colisiones
            samurai.render(batch, samurai.getX(), samurai.getY(), camera);

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

            // Dibujamos HUD y transición si es necesario
            batch.begin();
            //Renderizamos el hud si no estamos muertos, con el frenesi activo o en dialogo
            if(!samurai.isDead() && !samurai.isFrenzyActive() && !samurai.isInDialogue()) {
                hudManager.render(batch);
            }
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

    // Carga enemigos en segundo plano para mejor rendimiento
    private void loadEnemigosAsync() {
        new Thread(() -> {
            // Carga en segundo plano
            FileHandle file = Gdx.files.internal("niveles/Nivel1_1/Nivel1_Enemigos.txt");
            final String[] lines = file.readString().split("\n");

            // Vuelve al hilo principal para crear los enemigos
            Gdx.app.postRunnable(() -> {
                enemigos.clear();
                for (String line : lines) {
                    String[] data = line.split(",");
                    enemigos.add(new Enemigo(
                        Integer.parseInt(data[0]),
                        Integer.parseInt(data[1]),
                        Integer.parseInt(data[2]),
                        samurai,
                        Integer.parseInt(data[3]),
                        data[4],
                        Integer.parseInt(data[5]),
                        data[6],
                        Integer.parseInt(data[7]),
                        data[8],
                        Integer.parseInt(data[9]),
                        data[10],
                        Integer.parseInt(data[11])
                    ));
                }
            });
        }).start();
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
        tpSonido.dispose();
        musicaFondo.dispose();
    }

    // Carga enemigos desde archivo
    private void loadEnemigos(String filePath) {
        FileHandle file = Gdx.files.internal(filePath);
        String[] lines = file.readString().split("\n");
        for (String line : lines) {
            String[] data = line.split(",");
            enemigos.add(new Enemigo(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), samurai, Integer.parseInt(data[3]), data[4], Integer.parseInt(data[5]), data[6], Integer.parseInt(data[7]), data[8], Integer.parseInt(data[9]), data[10], Integer.parseInt(data[11])));
        }
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
                        backgroundTexture = new Texture("Fondos/2/3.png");
                        overlayTexture = new Texture(Gdx.files.internal("Overlays/10.png"));
                        samurai.setX(2784);
                        samurai.setY(7210);
                    } else if (name.equals("Muerte")) {
                        // El samurai muere
                        samurai.die();
                    } else if (name.equals("Victoria") && victoriaaa == 0) {
                        // El jugador completa el nivel
                        victoriaaa = 1;
                        isDeathTransition = true;
                        isTransitionIn = true;
                        transitionTimer = 0;
                        victoria = true;
                        saveGameProgress(1);
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
