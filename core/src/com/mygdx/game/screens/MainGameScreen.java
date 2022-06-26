package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Component;
import com.mygdx.game.Entity;
import com.mygdx.game.EntityFactory;
import com.mygdx.game.MapManager;
import com.mygdx.game.map.GameMap;

public class MainGameScreen implements Screen {
    private static final String TAG = MainGameScreen.class.getSimpleName();

    private static class VIEWPORT {
        static float viewportWidth;
        static float viewportHeight;
        static float virtualWidth;
        static float virtualHeight;
        static float physicalWidth;
        static float physicalHeight;
        static float aspectRatio;
    }
    private static final MapManager MAP_MANAGER = new MapManager();
    private static Entity player;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Json json = new Json();

    @Override
    public void show() {
        setupViewport(10, 10);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);

        mapRenderer = new OrthogonalTiledMapRenderer(MAP_MANAGER.getCurrentTiledMap(), GameMap.UNIT_SCALE);
        mapRenderer.setView(camera);

        MAP_MANAGER.setCamera(camera);

        player = EntityFactory.getEntity(EntityFactory.EntityType.PLAYER);
        MAP_MANAGER.setPlayer(player);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);

        if (MAP_MANAGER.hasMapChanged()) {
            mapRenderer.setMap(MAP_MANAGER.getCurrentTiledMap());
            player.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(MAP_MANAGER.getPlayerStartUnitScaled()));
            camera.position.set(MAP_MANAGER.getPlayerStartUnitScaled().x, MAP_MANAGER.getPlayerStartUnitScaled().y, 0.0f);
            camera.update();
            MAP_MANAGER.setMapChanged(false);
        }


        mapRenderer.render();

        MAP_MANAGER.updateCurrentMapEntities(MAP_MANAGER, mapRenderer.getBatch(), delta);

        player.update(MAP_MANAGER, mapRenderer.getBatch(), delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        player.dispose();
        mapRenderer.dispose();
    }


    private void setupViewport(int width, int height) {
        VIEWPORT.virtualWidth = width;
        VIEWPORT.virtualHeight = height;

        VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
        VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

        VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
        VIEWPORT.physicalHeight = Gdx.graphics.getHeight();

        VIEWPORT.aspectRatio = VIEWPORT.virtualWidth / VIEWPORT.virtualHeight;

        float physicalAspectRatio = VIEWPORT.physicalWidth / VIEWPORT.physicalHeight;
        if (physicalAspectRatio >= VIEWPORT.aspectRatio) {
            VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * physicalAspectRatio;
            VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
        } else {
            VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
            VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * physicalAspectRatio;
        }
    }

    private boolean isCollisionWithMapLayer(Rectangle boundingBox) {
        MapLayer collisionLayer = MAP_MANAGER.getCollisionLayer();
        if (collisionLayer != null) {
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject r && boundingBox.overlaps(r.getRectangle())) {
                    return true;
                }
            }
        }
        return false;
    }


}
