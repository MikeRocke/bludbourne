package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.map.GameMap;
import com.mygdx.game.map.MapFactory;

public class MapManager {
    private static final String TAG = MapManager.class.getSimpleName();

    private Camera camera;
    private boolean mapChanged = false;
    private GameMap currentMap;
    private Entity player;


    public void loadMap(String mapName) {
        GameMap gameMap = MapFactory.getMap(MapFactory.MapType.valueOf(mapName));
        if (gameMap == null) {
            Gdx.app.error(TAG,"Map does not exist: " + mapName);
        } else {
            this.currentMap = gameMap;
            this.mapChanged = true;
        }

    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Entity getPlayer() {
        return player;
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }

    public Array<Entity> getCurrentMapEntities() {
        return currentMap.getMapEntities();
    }

    public Vector2 getPlayerStartUnitScaled() {
        return currentMap.getPlayerStartUnitScaled();
    }

    public MapLayer getCollisionLayer() {
        return currentMap.getCollisionLayer();
    }

    public MapLayer getPortalLayer() {
        return currentMap.getPortalLayer();
    }

    public void setClosestStartPositionFromScaledUnits(Vector2 currentEntityPosition) {
        currentMap.setClosestStartPositionFromScaledUnits(currentEntityPosition);
    }

    public Array<Entity> getCurrentEntities() {
        return currentMap.getMapEntities();
    }

    public TiledMap getCurrentTiledMap() {
        //todo: feels dodgy lazy loading a map here
        if (currentMap == null) {
            loadMap(MapFactory.MapType.TOWN.name());
        }
        return currentMap.getCurrentTiledMap();
    }

    public void updateCurrentMapEntities(MapManager mapManager, Batch batch, float delta) {
        this.currentMap.updateMapEntities(mapManager, batch, delta);
    }

    public boolean hasMapChanged() {
        return mapChanged;
    }

    public void setMapChanged(boolean mapChanged) {
        this.mapChanged = mapChanged;
    }
}
