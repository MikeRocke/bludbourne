package com.mygdx.game;

import com.badlogic.gdx.utils.Json;

public class EntityFactory {
    private static final Json JSON = new Json();

    public enum EntityType {
        PLAYER, DEMO_PLAYER, NPC
    }

    public static final String PLAYER_CONFIG_FILE = "scripts/player.json";

    public static Entity getEntity(EntityType type) {
        return switch (type) {
            case PLAYER -> initPlayer();
            case DEMO_PLAYER -> new Entity(
                    new NPCInputComponent(),
                    new PlayerPhysicsComponent(),
                    new PlayerGraphicsComponent()
            );
            case NPC -> new Entity(
                    new NPCInputComponent(),
                    new NPCPhysicsComponent(),
                    new NPCGraphicsComponent()
            );
        };
    }

    private static Entity initPlayer() {
        Entity entity = new Entity(
                new PlayerInputComponent(),
                new PlayerPhysicsComponent(),
                new PlayerGraphicsComponent()
        );
        entity.setEntityConfig(Entity.loadEntityConfig(EntityFactory.PLAYER_CONFIG_FILE));
        String encodedConfig = JSON.toJson(entity.getEntityConfig());
        entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, encodedConfig);
        return entity;
    }
}
