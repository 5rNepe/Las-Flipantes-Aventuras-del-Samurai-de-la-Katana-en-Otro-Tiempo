package com.tfg.samuraidash.Niveles.lobby;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
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

public class Lobby implements Screen {
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture, backgroundnube, backgroundnube2, backgroundnube3, backgroundnube4, backgroundnube5, overlayTexture;
    private OrthographicCamera camera;
    private TiledMap map; // El mapa cargado
    private TiledMapRenderer mapRenderer; // El renderizador para dibujar el mapa
    private Samurai samurai;
    private Array<Plataforma> plataformas; // Cambiado a Rectangle para almacenar las colisiones
    private Array<NPC> npcs;
    private Array<Enemigo> enemigos;
    private float backgroundX = 0;
    private float backgroundSpeed = 10f;
    private float backgroundWidth = 640f;
    private float backgroundHeight = 480f;
    private Array<Cinematica> cinematicas;
    private MapObjects objects;
    private MenuNiveles menuNiveles;
    private MenuPausa menuPausa;
    private Music musicaFondo;



    @Override
    public void show() {
        batch = new SpriteBatch();
        overlayTexture = new Texture(Gdx.files.internal("Overlays/6.png"));
        font = new BitmapFont(Gdx.files.internal("fonts/fuente.fnt"));
        backgroundTexture = new Texture("Fondos/FondoLobby/1.png");
        backgroundnube = new Texture("Fondos/FondoLobby/2.png");
        backgroundnube2 = new Texture("Fondos/FondoLobby/3.png");
        backgroundnube3 = new Texture("Fondos/FondoLobby/4.png");
        backgroundnube4 = new Texture("Fondos/FondoLobby/5.png");
        backgroundnube5 = new Texture("Fondos/FondoLobby/6.png");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);

        // Cargar el mapa de Tiled
        map = new TmxMapLoader().load("Lobby Principal.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);
        objects = map.getLayers().get("CapaObjetos").getObjects();

        npcs = new Array<>();
        cinematicas = new Array<>();
        samurai = new Samurai(1408, 4384);
        enemigos = new Array<>();
        enemigos.add(new Enemigo(0, 0, 100, samurai, 0, "EnemigoIdle.png", 1,"EnemigoRun.png",2, "EnemigoAttack.png",3, "EnemigoDead.png",4));
        plataformas = new Array<>();

        // Cargar las colisiones
        loadCollisionsFromTiles();

        menuNiveles = new MenuNiveles();
        menuPausa = new MenuPausa(this);

        loadNPCs("niveles/Lobby/Lobby_NPC.txt");

        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica/lobby.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.3f);
        musicaFondo.play();

    }

    @Override
    public void render(float delta) {
        if (delta > 0.1f) {
            delta = 0.1f;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            musicaFondo.pause();
            menuPausa.toggle();
        }

        if (!menuPausa.isVisible()) {
            if (samurai.getX() < 4550) {
                musicaFondo.play();
            }

            updateCamera();

            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            ScreenUtils.clear(0, 0, 0, 1);

            batch.setProjectionMatrix(camera.combined);

            batch.begin();
            backgroundX -= backgroundSpeed * delta;
            if (backgroundX <= -backgroundWidth) {
                backgroundX = 0;  // Reiniciar la posición del fondo
            }
            batch.setColor(1, 1, 1, 1);
            batch.draw(
                backgroundTexture,
                camera.position.x - camera.viewportWidth / 2,  // Desplazamiento de fondo en X
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );
            batch.draw(
                backgroundTexture,
                camera.position.x - camera.viewportWidth / 2,  // Colocar al lado del primer fondo
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );
            batch.draw(
                backgroundnube,
                camera.position.x - camera.viewportWidth / 2,  // Desplazamiento de fondo en X
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );
            batch.draw(
                backgroundnube,
                camera.position.x - camera.viewportWidth / 2,  // Colocar al lado del primer fondo
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );
            batch.draw(
                backgroundnube2,
                camera.position.x - camera.viewportWidth / 2 + backgroundX,  // Desplazamiento de fondo en X
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );
            batch.draw(
                backgroundnube2,
                camera.position.x - camera.viewportWidth / 2 + backgroundX + backgroundWidth,  // Colocar al lado del primer fondo
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );
            batch.draw(
                backgroundnube3,
                camera.position.x - camera.viewportWidth / 2 + backgroundX,  // Desplazamiento de fondo en X
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );

            batch.draw(
                backgroundnube3,
                camera.position.x - camera.viewportWidth / 2 + backgroundX + backgroundWidth,  // Colocar al lado del primer fondo
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );
            batch.draw(
                backgroundnube4,
                camera.position.x - camera.viewportWidth / 2 + backgroundX,  // Desplazamiento de fondo en X
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );
            batch.draw(
                backgroundnube4,
                camera.position.x - camera.viewportWidth / 2 + backgroundX + backgroundWidth,  // Colocar al lado del primer fondo
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );
            batch.draw(
                backgroundnube5,
                camera.position.x - camera.viewportWidth / 2 + backgroundX,  // Desplazamiento de fondo en X
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );

            batch.draw(
                backgroundnube5,
                camera.position.x - camera.viewportWidth / 2 + backgroundX + backgroundWidth,  // Colocar al lado del primer fondo
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );
            batch.end();

            // Dibujar el mapa
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
                    npc.interact();  // Llama al método de interacción cuando el jugador está cerca
                }

                npc.render(batch);  // Dibujar el NPC y su bocadillo
            }

            // Renderizar al samurai
            samurai.InputMovimiento();

            samurai.update(delta, plataformas, enemigos);
            samurai.render(batch, samurai.getX(), samurai.getY(), camera);

            batch.setColor(1, 1, 1, 0.1f);
            batch.draw(
                overlayTexture,
                camera.position.x - camera.viewportWidth / 2,  // Desplazamiento de fondo en X
                camera.position.y - camera.viewportHeight / 2,  // Centrado con la cámara en Y
                backgroundWidth, backgroundHeight  // Tamaño del fondo
            );

            checkTriggers(samurai);

            menuNiveles.render(batch);
            batch.end();
        }

        batch.begin();
        menuPausa.render(batch);
        batch.end();

    }

    public void updateCamera() {
        // Hacer que la cámara esté siempre centrada en el jugador
        camera.position.set(samurai.getX() + 80, samurai.getY() + 80, 0);  // Centrar la cámara en el jugador
        camera.update();  // Actualizar la cámara para aplicar los cambios
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        menuPausa.resize(width, height);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        map.dispose();
        backgroundTexture.dispose();
        backgroundnube.dispose();
        backgroundnube2.dispose();
        backgroundnube3.dispose();
        backgroundnube4.dispose();
        backgroundnube5.dispose();
        menuNiveles.dispose();
        menuPausa.dispose();
        musicaFondo.dispose();
    }

    // Método para cargar las colisiones desde la capa de objetos en el mapa de Tiled
    private void loadCollisionsFromTiles() {
        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
                int tileWidth = tileLayer.getTileWidth();
                int tileHeight = tileLayer.getTileHeight();

                for (int x = 0; x < tileLayer.getWidth(); x++) {
                    for (int y = 0; y < tileLayer.getHeight(); y++) {
                        TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                        if (cell != null) {
                            TiledMapTile tile = cell.getTile();
                            if (tile != null && tile.getObjects().getCount() > 0) {
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

    private void loadNPCs(String filePath) {
        FileHandle file = Gdx.files.internal(filePath);
        String[] lines = file.readString().split("\n");
        for (String line : lines) {
            String[] data = line.split(",");
            if(data[6] != null) {
                npcs.add(new NPC(samurai, Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2], Integer.parseInt(data[3]), 0.2f, Animation.PlayMode.LOOP, data[4], new Array<>(data[5].split(";")), camera, data[6]));
            } else {
                npcs.add(new NPC(samurai, Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2], Integer.parseInt(data[3]), 0.2f, Animation.PlayMode.LOOP, data[4], new Array<>(data[5].split(";")), camera));
            }

        }
    }

    public void checkTriggers(Samurai samurai) {
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                if (samurai.getBounds().overlaps(rect)) {
                    String name = object.getName();
                    if (name != null && name.equals("niveles")) {
                        samurai.setX(4566);
                        menuNiveles.mostrar();
                        musicaFondo.stop();
                        return;
                    }
                }
            }
        }
    }
}
