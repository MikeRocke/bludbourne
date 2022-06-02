package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.util.StringUtils;

import java.util.Hashtable;
import java.util.Vector;

public class MapManager {
    private static final String TAG = MapManager.class.getSimpleName();

    //TODO: should these be hashtables? why not hashMap? or concurrent hash map?
    private Hashtable<String, String> mapTable;
    private Hashtable<String, Vector2> playerStartLocationTable;

    private static final String TOP_WORLD = "TOP_WORLD";
    private static final String TOWN = "TOWN";
    private static final String CASTLE_OF_DOOM = "CASTLE_OF_DOOM";

    private static final String MAP_COLLISION_LAYER = "MAP_COLLISION_LAYER";
    private static final String MAP_SPAWNS_LAYER = "MAP_SPAWNS_LAYER";
    private static final String MAP_PORTAL_LAYER = "MAP_PORTAL_LAYER";

    private static final String PLAYER_START = "PLAYER_START";

    private Vector2 playerStartPositionRect;
    private Vector2 closestPlayerStartPosition;
    private Vector2 convertedUnits;

    private Vector2 playerStart;
    private TiledMap currentMap;
    private String currentMapName;
    private MapLayer collisionLayer;
    private MapLayer portalLayer;
    private MapLayer spawnsLayer;

    public static final float UNIT_SCALE = 1/16f;

    public MapManager() {
        playerStart = new Vector2(0, 0);

        mapTable = new Hashtable<>();
        mapTable.put(TOP_WORLD, "maps/topworld.tmx");
        mapTable.put(TOWN, "maps/town.tmx");
        mapTable.put(CASTLE_OF_DOOM, "maps/castle_of_doom.tmx");

        playerStartLocationTable = new Hashtable<>();
        playerStartLocationTable.put(TOP_WORLD, playerStart.cpy());
        playerStartLocationTable.put(TOWN, playerStart.cpy());
        playerStartLocationTable.put(CASTLE_OF_DOOM, playerStart.cpy());

        playerStartPositionRect = new Vector2(0, 0);
        closestPlayerStartPosition = new Vector2(0, 0);
        convertedUnits = new Vector2(0, 0);
    }

    public void loadMap(String mapName) {
        playerStart.set(0, 0);

        String mapFullPath = mapTable.get(mapName);
        if (StringUtils.isEmpty(mapFullPath)) {
            Gdx.app.error(TAG, "Map is invalid " + mapName);
            return;
        } else {
            if (currentMap != null) {
                this.currentMap.dispose();
            }

            Utility.loadMapAsset(mapFullPath);

            if (Utility.isAssetLoaded(mapFullPath)) {
                this.currentMap = Utility.getMapAsset(mapFullPath);
                this.currentMapName = mapName;
            } else {
                Gdx.app.debug(TAG, "Map not loaded");
                return;
            }

            MapLayers layers = currentMap.getLayers();
            this.collisionLayer = layers.get(MAP_COLLISION_LAYER);
            this.portalLayer = layers.get(MAP_PORTAL_LAYER);
            this.spawnsLayer = layers.get(MAP_SPAWNS_LAYER);
            if (spawnsLayer != null) {
                Vector2 start = playerStartLocationTable.get(currentMapName);
                if (start.isZero()) {
                    setClosestStartPosition(playerStart);
                    start = playerStartLocationTable.get(currentMapName);
                }

                playerStart.set(start.x, start.y);
            }
        }
    }

    public TiledMap getCurrentMap() {
        if (currentMap == null) {
            currentMapName = TOWN;
            loadMap(currentMapName);
        }
        return currentMap;
    }

    public MapLayer getCollisionLayer() {
        return collisionLayer;
    }

    public MapLayer getPortalLayer() {
        return portalLayer;
    }

    public Vector2 getPlayerStartUnitScaled() {

        Vector2 scaled = this.playerStart.cpy();
        scaled.set(playerStart.x * UNIT_SCALE, playerStart.y * UNIT_SCALE);
        return scaled;
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

        playerStartLocationTable.put(currentMapName, closestPlayerStartPosition.cpy());
    }
}
