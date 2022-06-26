package com.mygdx.game.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.game.Entity;
import com.mygdx.game.MapManager;

public class CastleOfDoomGameMap extends GameMap {
    public CastleOfDoomGameMap() {
        super(MapFactory.MapType.CASTLE_OF_DOOM, "maps/castle_of_doom.tmx");
    }

    @Override
    public void updateMapEntities(MapManager mapManager, Batch batch, float delta) {
        for (Entity mapEntity : mapEntities) {
            mapEntity.update(mapManager, batch, delta);
        }
    }
}
