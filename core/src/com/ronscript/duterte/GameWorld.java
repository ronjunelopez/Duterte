package com.ronscript.duterte;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ronscript.duterte.ai.pfa.AStarPathFinding;
import com.ronscript.duterte.ai.pfa.NavigationMap;
import com.ronscript.duterte.systems.graphics.PhysicsDebugSystem;
import com.ronscript.duterte.utils.BodyBuilder;
import com.ronscript.duterte.utils.BodyManager;
import com.ronscript.duterte.utils.CameraManager;
import com.ronscript.duterte.utils.Constants;
import com.ronscript.duterte.utils.EntityManager;
import com.ronscript.duterte.utils.ShapeRendererManager;
import com.ronscript.duterte.utils.SystemManager;
import com.ronscript.duterte.utils.WorldManager;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class GameWorld {

    public static NavigationMap navMap;
    public static AStarPathFinding navigator;
    public static Entity playerCharacter;
    public static boolean debug = true;
    public final ShapeRenderer shape;
    private final PooledEngine engine;
    private final World world;
    private SystemManager systemManager;
    private EntityManager entityManager;

    public GameWorld(SpriteBatch batch, Camera camera, Viewport viewport, GameInputProcessor inputProcessor) {
        engine = new PooledEngine();
        world = new World(new Vector2(0, 0), true);
        shape = new ShapeRenderer();
        shape.setAutoShapeType(true);
        ShapeRendererManager.init(shape);
        WorldManager.getInstance().init(world);
        BodyManager.registerWorld(world);
        systemManager = SystemManager.getInstance();
        entityManager = EntityManager.getInstance();

        CameraManager.getInstance().setup(camera, viewport);
        systemManager.init(engine, world, batch, camera, inputProcessor);
        entityManager.init(engine, world);

        Preferences prefs = Gdx.app.getPreferences("Duterte");
    }

    public PooledEngine getEngine() {
        return engine;
    }

    private void buildEntities() {
        TiledMap tiledMap = GameAssets.tiledMap;
        MapLayers mapLayers = tiledMap.getLayers();
        createCellNavigation(mapLayers.get("navigation"));
        float unitScale = Constants.PIXELS_TO_METERS;
        for(MapObject wallObject : mapLayers.get("walls").getObjects()) {

            if(wallObject instanceof TextureMapObject) {
                continue;
            }

            Body body;
            Shape shape;

            body = BodyBuilder.createStaticBody(world, 0, 0);

            if(wallObject instanceof RectangleMapObject) {
                shape = BodyBuilder.createMapRectangle(((RectangleMapObject) wallObject), unitScale);
            } else if(wallObject instanceof PolylineMapObject) {
                shape = BodyBuilder.createMapPolyline(((PolylineMapObject) wallObject), unitScale);
            } else {
                continue;
            }

            BodyBuilder.createWallFixture(body, shape, wallObject);

        }

        Vector2 playerSpawnPoint = new Vector2();
        Vector2 policeSpawnPoint = new Vector2();
        Vector2 criminalSpawnPoint = new Vector2();

        for(MapObject npc : mapLayers.get("NPC").getObjects()) {
            String type = npc.getProperties().get("type", "", String.class);
            if(type.equals("Player")) {
                Ellipse player = ((EllipseMapObject) npc).getEllipse();
                playerSpawnPoint.set(player.x * unitScale, player.y * unitScale);
            }
            if(type.equals("Enemy")) {
                Ellipse enemy = ((EllipseMapObject) npc).getEllipse();
                float enemyX = enemy.x * unitScale;
                float enemyY = enemy.y * unitScale;
                criminalSpawnPoint.set(enemyX, enemyY);
            }
            if(type.equals("Allied")) {
                Ellipse allied = ((EllipseMapObject) npc).getEllipse();
                policeSpawnPoint.set(allied.x * unitScale, allied.y * unitScale);
            }
        }

        spawnPlayer(playerSpawnPoint);
        spawnPolices(policeSpawnPoint, 50);
        spawnCriminals(criminalSpawnPoint, 100);
    }

    private void spawnPlayer(Vector2 playerSpawnPoint) {
        float x = playerSpawnPoint.x;
        float y = playerSpawnPoint.y;
        Entity player = entityManager.createPlayerCharacter(x, y);
//                Entity gun = entityManager.createGun(playerX, playerY, playerChar);
        Entity healthBg = entityManager.createHealthBackground(x, y, player);
        entityManager.createHealthBar(x, y, healthBg, player);
        entityManager.createInventory(player);
        GameWorld.playerCharacter = player;
    }

    private void spawnPolices (Vector2 policeSpawnPoint, int n) {
        float x = policeSpawnPoint.x;
        float y = policeSpawnPoint.y;
        for (int i = 0; i < n; i++) {
            Entity police = entityManager.createPoliceCharacter( x, y);
            entityManager.createGun(x, y, police);
        }
    }

    private void spawnCriminals(Vector2 criminalSpawnPoint, int n) {
        float x = criminalSpawnPoint.x;
        float y = criminalSpawnPoint.y;
        for (int i = 0; i < n; i++) {
            Entity enemyChar = entityManager.createDruggieCharacter(x, y);
            Entity healthBg = entityManager.createHealthBackground(x, y, enemyChar);
            entityManager.createHealthBar(x, y, healthBg, enemyChar);
        }
    }


    private void createCellNavigation(MapLayer layer) {
        TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
//        layer.setVisible(false);
        int columns = tiledLayer.getWidth();
        int rows = tiledLayer.getHeight();
        navMap = new NavigationMap(columns, rows);


        for (int y = 0; y < rows; y++) {
            for(int x = 0; x < columns;x++) {
                TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                if (cell != null) {
                    MapProperties properties  = cell.getTile().getProperties();
                    if(properties.containsKey("walkable")) {
                        navMap.getNodeAt(x, y).walkable = Boolean.valueOf(properties.get("walkable").toString());
                    }
                }
            }
        }

        Gdx.app.log("tes", navMap.toString());
        navigator = new AStarPathFinding(GameWorld.navMap);
    }

    public void build() {
        systemManager.create();
        buildEntities();
    }

    private void debug(float deltaTime) {
        if(debug) {
            engine.getSystem(PhysicsDebugSystem.class).setProcessing(true);
            for(EntitySystem system: engine.getSystems()) {
                if(system instanceof DebugDrawable) {
                    ((DebugDrawable) system).debugDraw(shape, deltaTime);
                }
            }
        } else {
            engine.getSystem(PhysicsDebugSystem.class).setProcessing(false);
        }
    }

    public void update(float deltaTime) {
        engine.update(deltaTime);
        entityManager.update();
        debug(deltaTime);
    }

}
