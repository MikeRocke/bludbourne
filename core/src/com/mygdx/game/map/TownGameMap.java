package com.mygdx.game.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.*;

public class TownGameMap extends GameMap {
    private static final String TOWN_GUARD_WALKING = "scripts/town_guard_walking.json";
    private static final String TOWN_BLACKSMITH = "scripts/town_blacksmith.json";
    private static final String TOWN_MAGE = "scripts/town_mage.json";
    private static final String TOWN_INN_KEEPER = "scripts/town_innkeeper.json";
    private static final String TOWN_FOLK = "scripts/town_folk.json";


    public TownGameMap() {
        super(MapFactory.MapType.TOWN, "maps/town.tmx");
        for (Vector2 position : npcStartPositions) {
            EntityConfig entityConfig = Entity.loadEntityConfig(TOWN_GUARD_WALKING);
            mapEntities.add(initEntity(entityConfig, position));
        }
        mapEntities.add(initSpecialEntity(Entity.loadEntityConfig(TOWN_BLACKSMITH)));
        mapEntities.add(initSpecialEntity(Entity.loadEntityConfig(TOWN_MAGE)));
        mapEntities.add(initSpecialEntity(Entity.loadEntityConfig(TOWN_INN_KEEPER)));
        Array<EntityConfig> entityConfigs = Entity.loadEntityConfigs(TOWN_FOLK);
        for (EntityConfig entityConfig : entityConfigs) {
            mapEntities.add(initSpecialEntity(entityConfig));
        }
    }

    @Override
    public void updateMapEntities(MapManager mapManager, Batch batch, float delta) {
        for (int i = 0; i < mapEntities.size; i++) {
            mapEntities.get(i).update(mapManager, batch, delta);
        }
    }

    private Entity initSpecialEntity(EntityConfig entityConfig) {
        Vector2 position = this.specialNpcStartPositions.getOrDefault(entityConfig.getEntityID(), new Vector2(0, 0));
        return initEntity(entityConfig, position);
    }

    private Entity initEntity(EntityConfig entityConfig, Vector2 position) {
        Entity entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC);
        entity.setEntityConfig(entityConfig);
        entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entity.getEntityConfig()));
        entity.sendMessage(Component.MESSAGE.INIT_START_POSITION, json.toJson(position));
        entity.sendMessage(Component.MESSAGE.INIT_STATE, json.toJson(entity.getEntityConfig().getState()));
        entity.sendMessage(Component.MESSAGE.INIT_DIRECTION, json.toJson(entity.getEntityConfig().getDirection()));
        return entity;
    }
}
