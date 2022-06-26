package com.mygdx.game.map;

import java.util.EnumMap;
import java.util.Map;

public class MapFactory {
    private static final Map<MapType, GameMap> MAPS = new EnumMap<>(MapType.class);
    public enum MapType {
        TOP_WORLD, TOWN, CASTLE_OF_DOOM
    }

    public static GameMap getMap(MapType mapType) {
        GameMap gameMap = switch (mapType) {
            case TOP_WORLD -> new TopWorldGameMap();
            case TOWN -> new TownGameMap();
            case CASTLE_OF_DOOM -> new CastleOfDoomGameMap();
        };
        if (MAPS.get(mapType) == null) {
            MAPS.put(mapType, gameMap);
        }
        return gameMap;
    }
}
