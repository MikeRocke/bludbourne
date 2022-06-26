package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.util.StringUtils;

public class NPCInputComponent extends InputComponent implements InputProcessor {
    private static final String TAG = NPCInputComponent.class.getSimpleName();

    private float frameTime = 0.0f;

    @Override
    public void dispose() {

    }

    @Override
    public void receiveMessage(String message) {
        String[] splitMessage = message.split(MESSAGE_TOKEN);

        if(splitMessage.length == 1 ) {
            String messageType = splitMessage[0];
            if (StringUtils.isEqualIgnoringCase(MESSAGE.COLLISION_WITH_MAP, messageType)) {
                currentDirection = Entity.Direction.getRandomNext();
            } else if (StringUtils.isEqualIgnoringCase(MESSAGE.COLLISION_WITH_ENTITY, messageType)) {
                currentState = Entity.State.IDLE;
            }
        }

        if( splitMessage.length == 2 ) {
            String messageType = splitMessage[0];
            if (StringUtils.isEqualIgnoringCase(MESSAGE.INIT_STATE, messageType)) {
                currentState = json.fromJson(Entity.State.class, splitMessage[1]);
            } else if (StringUtils.isEqualIgnoringCase(MESSAGE.INIT_DIRECTION, messageType)) {
                currentDirection = json.fromJson(Entity.Direction.class, splitMessage[1]);
            }
        }
    }

    @Override
    public void update(Entity entity, float delta) {
        if (KEY_STATE.get(Keys.QUIT)) {
            Gdx.app.exit();
        }

        if (Entity.State.IMMOBILE == currentState) {
            entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.IMMOBILE));
        } else {
            frameTime += delta;
            if(frameTime > MathUtils.random(1,5) ){
                currentState = Entity.State.getRandomNext();
                currentDirection = Entity.Direction.getRandomNext();
                frameTime = 0.0f;
            }

            if (Entity.State.IDLE == currentState) {
                entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.IDLE));
            } else {
                if (KEY_STATE.get(Keys.LEFT)) {
                    walk(entity, Entity.Direction.LEFT);
                } else if (KEY_STATE.get(Keys.RIGHT)) {
                    walk(entity, Entity.Direction.RIGHT);
                } else if (KEY_STATE.get(Keys.UP)) {
                    walk(entity, Entity.Direction.UP);
                } else if (KEY_STATE.get(Keys.DOWN)) {
                    walk(entity, Entity.Direction.DOWN);
                }
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.Q) {
            KEY_STATE.put(Keys.QUIT, true);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

}
