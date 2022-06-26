package com.mygdx.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Entity;
import com.mygdx.game.MapManager;
import com.mygdx.game.Utility;
import com.mygdx.game.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class GameMap {
    public static final float UNIT_SCALE = 1/16f;
    protected static final String COLLISION_LAYER = "MAP_COLLISION_LAYER";
    protected static final String SPAWNS_LAYER = "MAP_SPAWNS_LAYER";
    protected static final String PORTAL_LAYER = "MAP_PORTAL_LAYER";
    protected static final String PLAYER_START = "PLAYER_START";
    protected static final String NPC_START = "NPC_START";
    private static final String TAG = GameMap.class.getSimpleName();

    protected Json json;
    protected Vector2 playerStartPositionRect;
    protected Vector2 closestPlayerStartPosition;
    protected Vector2 convertedUnits;
    protected TiledMap currentTiledMap;
    protected Vector2 playerStart;
    protected Array<Vector2> npcStartPositions;
    protected Map<String, Vector2> specialNpcStartPositions;
    protected MapLayer collisionLayer;
    protected MapLayer portalLayer;
    protected MapLayer spawnsLayer;
    protected MapFactory.MapType currentMapType;
    protected Array<Entity> mapEntities;

    protected GameMap(MapFactory.MapType mapType, String fullMapPath) {
        this.currentMapType = mapType;
        this.json = new Json();
        this.mapEntities = new Array<>(10);
        this.playerStart = new Vector2(0, 0);
        this.playerStartPositionRect = new Vector2(0, 0);
        this.closestPlayerStartPosition = new Vector2(0, 0);
        this.convertedUnits = new Vector2(0, 0);

        if (StringUtils.isEmpty(fullMapPath)) {
            Gdx.app.error(TAG, "Map is invalid " + fullMapPath);
        } else {
            Utility.loadMapAsset(fullMapPath);

            if (Utility.isAssetLoaded(fullMapPath)) {
                this.currentTiledMap = Utility.getMapAsset(fullMapPath);


                MapLayers layers = this.currentTiledMap.getLayers();
                this.collisionLayer = layers.get(COLLISION_LAYER);
                this.portalLayer = layers.get(PORTAL_LAYER);
                this.spawnsLayer = layers.get(SPAWNS_LAYER);
                if (spawnsLayer != null) {
                    setClosestStartPosition(playerStart);
                }
                this.npcStartPositions = getNpcStartPositions();
                this.specialNpcStartPositions = getSpecialNpcStartPositions();
            } else {
                Gdx.app.debug(TAG, "Map not loaded");
            }
        }
    }

    public Array<Entity> getMapEntities() {
        return mapEntities;
    }

    public Vector2 getPlayerStart() {
        return playerStart;
    }

    public abstract void updateMapEntities(MapManager mapManager, Batch batch, float delta);

    public MapLayer getCollisionLayer() {
        return collisionLayer;
    }

    public MapLayer getPortalLayer() {
        return portalLayer;
    }

    public TiledMap getCurrentTiledMap() {
        return currentTiledMap;
    }

    public Vector2 getPlayerStartUnitScaled() {
        Vector2 scaled = this.playerStart.cpy();
        scaled.set(playerStart.x * UNIT_SCALE, playerStart.y * UNIT_SCALE);
        return scaled;
    }

    private Array<Vector2> getNpcStartPositions() {
        Array<Vector2> npcStartPositions = new Array<>();
        for (MapObject mapObject : spawnsLayer.getObjects()) {
            String mapObjectName = mapObject.getName();
            if (!StringUtils.isEmpty(mapObjectName) && StringUtils.isEqualIgnoringCase(NPC_START, mapObjectName)) {
                float x = ((RectangleMapObject)mapObject).getRectangle().x;
                float y = ((RectangleMapObject)mapObject).getRectangle().y;

                x *= UNIT_SCALE;
                y *= UNIT_SCALE;

                npcStartPositions.add(new Vector2(x, y));
            }
        }
        return npcStartPositions;
    }

    private Map<String, Vector2> getSpecialNpcStartPositions() {
        Map<String, Vector2> specialNpcStartPositions = new HashMap<>();
        for (MapObject mapObject : spawnsLayer.getObjects()) {
            String mapObjectName = mapObject.getName();
            if (!StringUtils.isEmpty(mapObjectName) && !StringUtils.isEqualIgnoringCase(NPC_START, mapObjectName) && !StringUtils.isEqualIgnoringCase(PLAYER_START, mapObjectName)) {
                float x = ((RectangleMapObject)mapObject).getRectangle().x;
                float y = ((RectangleMapObject)mapObject).getRectangle().y;

                x *= UNIT_SCALE;
                y *= UNIT_SCALE;

                specialNpcStartPositions.put(mapObjectName, new Vector2(x, y));
            }
        }

        return specialNpcStartPositions;
    }


    public void setClosestStartPositionFromScaledUnits(Vector2 position) {
        if (UNIT_SCALE > 0) {
            convertedUnits.set(position.x/UNIT_SCALE, position.y/UNIT_SCALE);
            setClosestStartPosition(convertedUnits);
        }
    }


    private void setClosestStartPosition(Vector2 position) {
        playerStartPositionRect.set(0, 0);
        closestPlayerStartPosition.set(0, 0);
        float shortestDistance = 0;
        for (MapObject object : spawnsLayer.getObjects()) {
            if (PLAYER_START.equals(object.getName()) && object instanceof RectangleMapObject r) {
                r.getRectangle().getPosition(playerStartPositionRect);
                float distance = position.dst2(playerStartPositionRect);

                if (distance < shortestDistance || shortestDistance == 0) {
                    closestPlayerStartPosition.set(playerStartPositionRect);
                    shortestDistance = distance;
                }
            }
        }

        playerStart = closestPlayerStartPosition.cpy();
    }
}
