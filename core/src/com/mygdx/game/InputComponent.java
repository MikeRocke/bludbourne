package com.mygdx.game;

import com.badlogic.gdx.utils.Json;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public abstract class InputComponent implements Component {
    protected Entity.Direction currentDirection = null;
    protected Entity.State currentState = null;
    protected final Json json;

    protected void walk(Entity entity, Entity.Direction direction) {
        entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.WALKING));
        entity.sendMessage(MESSAGE.CURRENT_DIRECTION, json.toJson(direction));
    }

    protected enum Keys {
        LEFT, RIGHT, UP, DOWN, QUIT
    }

    protected enum Mouse {
        SELECT, DO_ACTION
    }

    //not sure why static
    protected static final Map<Keys, Boolean> KEY_STATE = new EnumMap<>(Keys.class);
    protected static final Map<Mouse, Boolean> MOUSE_STATE = new EnumMap<>(Mouse.class);

    static {
        Arrays.stream(Keys.values()).forEach(it -> KEY_STATE.put(it, false));
        Arrays.stream(Mouse.values()).forEach(it -> MOUSE_STATE.put(it, false));
    }

    InputComponent() {
        json = new Json();
    }

    public abstract void update(Entity entity, float delta);
}
