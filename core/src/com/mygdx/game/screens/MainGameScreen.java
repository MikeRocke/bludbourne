package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Entity;
import com.mygdx.game.MapManager;
import com.mygdx.game.PlayerController;

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
    private static MapManager mapManager = new MapManager();
    private static Entity player;

    private PlayerController playerController;
    private TextureRegion currentPlayerFrame;
    private Sprite currentPlayerSprite;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;


    @Override
    public void show() {
        setupViewport(10, 10);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);

        mapRenderer = new OrthogonalTiledMapRenderer(mapManager.getCurrentMap(), MapManager.UNIT_SCALE);
        mapRenderer.setView(camera);

        player = new Entity();
        var playerStart = mapManager.getPlayerStartUnitScaled();
        player.init(playerStart.x, playerStart.y);

        currentPlayerSprite = player.getFrameSprite();

        playerController = new PlayerController(player);
        Gdx.input.setInputProcessor(playerController);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Lock and center the camera to the player's positon
        camera.position.set(currentPlayerSprite.getX(), currentPlayerSprite.getY(), 0f);
        camera.update();

        player.update(delta);
        currentPlayerFrame = player.getFrame();

        updatePortalLayerActivation(player.boundingBox);
        if (!isCollisionWithMapLayer(player.boundingBox)) {
            player.setNextPositionToCurrent();
        }

        playerController.update(delta);

        mapRenderer.setView(camera);
        mapRenderer.render();

        mapRenderer.getBatch().begin();
        mapRenderer.getBatch().draw(currentPlayerFrame, currentPlayerSprite.getX(), currentPlayerSprite.getY(), 1, 1);
        mapRenderer.getBatch().end();
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
        playerController.dispose();
        Gdx.input.setInputProcessor(null);
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
        MapLayer collisionLayer = mapManager.getCollisionLayer();
        if (collisionLayer != null) {
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject r && boundingBox.overlaps(r.getRectangle())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean updatePortalLayerActivation(Rectangle boundingBox) {
        MapLayer portalLayer = mapManager.getPortalLayer();

        if (portalLayer != null) {
            for (MapObject object : portalLayer.getObjects()) {
                if (object instanceof RectangleMapObject r) {
                    if (boundingBox.overlaps(r.getRectangle())) {
                        String mapName = object.getName();
                        if (mapName == null) {
                            return false;
                        } else {
                            mapManager.setClosestStartPositionFromScaledUnits(player.getCurrentPosition());
                            mapManager.loadMap(mapName);
                            var playerStart = mapManager.getPlayerStartUnitScaled();
                            player.init(playerStart.x, playerStart.y);
                            mapRenderer.setMap(mapManager.getCurrentMap());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
