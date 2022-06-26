package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

public class Entity {

    private static final String TAG = Entity.class.getSimpleName();
    private Json json;
    private EntityConfig entityConfig;

    public enum Direction {
        UP,RIGHT,DOWN,LEFT;

        public static Direction getRandomNext() {
            Direction[] values = Direction.values();
            int randomIndex = MathUtils.random(values.length - 1);
            return values[randomIndex];
        }

        public Direction getOpposite() {
            return switch (this) {
                case UP -> DOWN;
                case RIGHT -> LEFT;
                case DOWN -> UP;
                case LEFT -> RIGHT;
            };
        }
    }

    public enum State {
        IDLE, WALKING, IMMOBILE;

        public static State getRandomNext() {
            State[] values = State.values();
            int randomIndex = MathUtils.random(values.length - 2); //dirty hack to ignore IMMOBILE as last in array
            return values[randomIndex];
        }
    }

    public enum AnimationType {
        WALK_LEFT,
        WALK_RIGHT,
        WALK_UP,
        WALK_DOWN,
        IDLE,
        IMMOBILE;
    }

    public static final int FRAME_WIDTH = 16;
    public static final int FRAME_HEIGHT = 16;
    private static final int MAX_COMPONENTS = 5;
    private Array<Component> components;
    private InputComponent inputComponent;
    private GraphicsComponent graphicsComponent;
    private PhysicsComponent physicsComponent;

    public static EntityConfig loadEntityConfig(String configFile) {
        return new Json().fromJson(EntityConfig.class, Gdx.files.internal(configFile));
    }
    public static Array<EntityConfig> loadEntityConfigs(String configFile) {
        Array<EntityConfig> toReturn = new Array<>();
        Json myJson = new Json();
        ArrayList<JsonValue> arrayList = myJson.fromJson(ArrayList.class, Gdx.files.internal(configFile)); //dirty cast
        for (JsonValue jsonValue : arrayList) {
            EntityConfig entityConfig = myJson.readValue(EntityConfig.class, jsonValue);
            toReturn.add(entityConfig);
        }
        return toReturn;
    }

    public Entity(InputComponent inputComponent, PhysicsComponent physicsComponent, GraphicsComponent graphicsComponent) {
        this.entityConfig = new EntityConfig();
        this.json = new Json();
        this.components = new Array<>(MAX_COMPONENTS);
        this.inputComponent = inputComponent;
        this.physicsComponent = physicsComponent;
        this.graphicsComponent = graphicsComponent;

        this.components.addAll(inputComponent, physicsComponent, graphicsComponent);
    }

    public EntityConfig getEntityConfig() {
        return entityConfig;
    }

    public void setEntityConfig(EntityConfig entityConfig) {
        this.entityConfig = entityConfig;
    }

    public void sendMessage(Component.MESSAGE messageType, String... args) {
        StringBuilder fullMessageBuilder = new StringBuilder(messageType.toString());

        for (String arg : args) {
            fullMessageBuilder = fullMessageBuilder.append(Component.MESSAGE_TOKEN).append(arg);
        }

        for (Component component : components) {
            component.receiveMessage(fullMessageBuilder.toString());
        }
    }

    public void update(MapManager mapManager, Batch batch, float delta) {
        this.inputComponent.update(this, delta);
        this.physicsComponent.update(this, mapManager, delta);
        this.graphicsComponent.update(this, mapManager, batch, delta);
    }

    public void dispose() {
        for (Component component : components) {
            component.dispose();
        }
    }

    public Rectangle getCurrentBoundingBox() {
        return this.physicsComponent.getBoundingBox();
    }

}
