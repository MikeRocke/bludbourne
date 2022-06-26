package com.mygdx.game.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.game.Entity;
import com.mygdx.game.MapManager;

public class TopWorldGameMap extends GameMap {
    public TopWorldGameMap() {
        super(MapFactory.MapType.TOP_WORLD, "maps/topworld.tmx");
    }

    @Override
    public void updateMapEntities(MapManager mapManager, Batch batch, float delta) {
        for (Entity mapEntity : mapEntities) {
            mapEntity.update(mapManager, batch, delta);
        }
    }
}
