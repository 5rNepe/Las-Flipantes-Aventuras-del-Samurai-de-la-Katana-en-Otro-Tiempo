package com.tfg.samuraidash.Niveles.tutorial;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.utils.ScreenUtils;
import com.tfg.samuraidash.*;

import java.util.ArrayList;
import java.util.List;

public class NivelTutorial implements Screen {
    // Variables para dibujar elementos en pantalla
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture, backgroundnube, backgroundnube2, overlayTexture;
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
    private MenuPausa menuPausa;

    // Sonidos y música
    private Music musicaFondo; // Música del nivel

    @Override
    public void show() {
        // Inicializamos todos los elementos del juego cuando se muestra la pantalla
        batch = new SpriteBatch();
        overlayTexture = new Texture(Gdx.files.internal("Overlays/1.png")); // Textura de overlay
        font = new BitmapFont(Gdx.files.internal("fonts/fuente.fnt")); // Fuente para texto
        backgroundTexture = new Texture("Fondos/fondoanochecer.png"); // Fondo del nivel
        backgroundnube = new Texture("Fondos/nubefondoanochecer.png"); // Fondo del nivel
        backgroundnube2 = new Texture("Fondos/nubefondoanochecer2.png"); // Fondo del nivel

        // Configuramos la cámara
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);

        // Cargar el mapa de Tiled
        map = new TmxMapLoader().load("MapaTutorial.tmx");  // Aquí cargamos el archivo .tmx
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);
        objects = map.getLayers().get("CapaObjetos").getObjects();

        // Inicializamos arrays y personaje
        npcs = new Array<>();
        cinematicas = new Array<>();
        samurai = new Samurai(1152, 14432); // Posición inicial del samurai
        enemigos = new Array<>();
        // Como el nivel no tiene enemigos pero samurai necesita que le pases minimo un enemigo creamos un enemigo fuera de pantalla
        enemigos.add(new Enemigo(0, 0, 100, samurai, 0, "EnemigoIdle.png", 1,"EnemigoRun.png",2, "EnemigoAttack.png",3, "EnemigoDead.png",4));
        plataformas = new Array<>();

        // Cargamos colisiones
        loadCollisionsFromTiles();

        //Creamos las cinematicas que usara el nivel a partir de sus propias clases
        Cinematica cine1 = new Cinematica(samurai);
        //Creo NPCs, sin textura para que sean dialogos
        npcs.add(new NPC(samurai, 150, 32, "TutorialNpcIdle.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "Creo que se esta haciendo de noche",
            "Deberia volver a casa",
            "Recogo las cosas y me voy"
        }), camera));
        npcs.add(new NPC(samurai, 150, 32, "TutorialNpcIdle.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "Todo recogido hora de volver."
        }), camera));
        //Creo una lista de strings para que sea texto por pantalla
        List<String> textos = new ArrayList<>();
        textos.add("Este es el principio de mi historia...");
        textos.add("Este es el principio de como comenzo...");
        textos.add("Aqui inicia todo lo que acabara en la nada.");
        //Añado a la cinematica las acciones, de caminar, los npcs como textos y la pantalla negra con los textos de la lista
        cine1.agregarAccion(new PantallaNegraConTexto(2f, textos));
        cine1.agregarAccion(new Caminar(samurai, 1f, false));  // Caminar durante 1 segundo
        cine1.agregarAccion(new Dialogar(npcs.get(0)));         // Iniciar el diálogo
        cine1.agregarAccion(new Caminar(samurai, 1f, true)); // Caminar hacia atrás durante 1 segundo
        cine1.agregarAccion(new Dialogar(npcs.get(1)));
        //Añado la cinematica al array de cinematicas
        cinematicas.add(cine1);


        //Repetimos lo anterior para otra cinematica
        Cinematica cine2 = new Cinematica(samurai);
        npcs.add(new NPC(samurai, 150, 32, "TutorialNpcIdle.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "Deberia ir a dormir",
            "En el arbol estaria bien."
        }), camera));
        cine1.agregarAccion(new Caminar(samurai, 0.0001f, false));
        cine2.agregarAccion(new Dialogar(npcs.get(2)));         // Iniciar el diálogo con el NPC
        cinematicas.add(cine2);

        //Repetimos lo anterior para otra cinematica
        Cinematica cine3 = new Cinematica(samurai);
        npcs.add(new NPC(samurai, 150, 32, "TutorialNpcIdle.png", 6, 0.1f, Animation.PlayMode.LOOP, "Samurai", new Array<String>(new String[]{
            "Hora de irse a dormir."
        }), camera));
        List<String> textos2 = new ArrayList<>();
        textos2.add("Era una noche como cualquier otra");
        textos2.add("pero entonces sucedio...");
        textos2.add("ZzZ zZz ZzZ");
        textos2.add("QUE ES ESE RUIDO?");
        cine1.agregarAccion(new Caminar(samurai, 0.0001f, false));
        cine3.agregarAccion(new Dialogar(npcs.get(3)));
        cine3.agregarAccion(new PantallaNegraConTexto(2f, textos2));
        Game game = (Game) Gdx.app.getApplicationListener();
        NivelTutorialDormir nuevoScreen = new NivelTutorialDormir();
        cine3.agregarAccion(new CambiarPantalla(game, nuevoScreen));
        cinematicas.add(cine3);

        //Inicio la primera cinematica nada mas iniciar el nivel
        cinematicas.get(0).iniciar();

        // Configuramos menú de pausa
        menuPausa = new MenuPausa(this);

        // Cargamos NPCs desde archivo
        loadNPCs("niveles/Tutorial/NivelTutorial_NPC.txt");

        // Cargamos sonidos
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica/tutorial1.mp3"));
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
            if (!cinematicas.get(2).isEnCinematica()) {
                musicaFondo.play();
            }

            // Actualizamos cámara
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
            batch.setColor(1, 1, 1, 1);
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
            batch.draw(
                backgroundnube,
                camera.position.x - camera.viewportWidth / 2 + backgroundX,  // Desplazamiento de fondo en X
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );

            // Dibujar el fondo (segundo fondo, al lado del primero)
            batch.draw(
                backgroundnube,
                camera.position.x - camera.viewportWidth / 2 + backgroundX + backgroundWidth,  // Colocar al lado del primer fondo
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );
            batch.draw(
                backgroundnube2,
                camera.position.x - camera.viewportWidth / 2 + backgroundX,  // Desplazamiento de fondo en X
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );

            // Dibujar el fondo (segundo fondo, al lado del primero)
            batch.draw(
                backgroundnube2,
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
                } else {
                    //Llamo al movimiento del samurai para que funcionen los controles
                    samurai.InputMovimiento();
                }
            }

            // Renderizar al samurai
            samurai.update(delta, plataformas, enemigos); // Pasamos las plataformas como colisiones
            samurai.render(batch, samurai.getX(), samurai.getY(), camera);

            // Dibujamos overlay, aqui abajo para que este por encima del samurai
            batch.setColor(1, 1, 1, 0.2f);
            batch.draw(
                overlayTexture,
                camera.position.x - camera.viewportWidth / 2,  // Desplazamiento de fondo en X
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );

            // Comprobamos triggers (zonas que tienen cinematicas o condiciones como victoria)
            checkTriggers(samurai);
            batch.end();
        }

        // Dibujamos menú de pausa si está activo
        batch.begin();
        menuPausa.render(batch);
        batch.end();
    }

    // Actualiza la posición de la cámara para seguir al samurai
    public void updateCamera() {
        // Hacer que la cámara esté siempre centrada en el jugador
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
        musicaFondo.dispose();
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

                    // Solo iniciar la cinemática si no está ya en ejecución
                    if (name.equals("cinematica1") && !cinematicas.get(1).isEnCinematica()) {
                        // Inicia la cinematica
                        cinematicas.get(1).iniciar();
                    } else if (name.equals("cinematica2") && !cinematicas.get(2).isEnCinematica()) {
                        // Inicia la cinematica
                        cinematicas.get(2).iniciar();
                        musicaFondo.stop();
                    } else if (name.equals("muerte")) {
                        // El samurai muere
                        samurai.die();
                    }
                }
            }
        }
    }




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
}
