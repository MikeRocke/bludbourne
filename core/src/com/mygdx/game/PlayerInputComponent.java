package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.util.StringUtils;

public class PlayerInputComponent extends InputComponent implements InputProcessor {
    private static final String TAG = PlayerInputComponent.class.getSimpleName();
    private final Vector3 lastMouseCoordinates;

    public PlayerInputComponent() {
        lastMouseCoordinates = new Vector3();
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void receiveMessage(String message) {
        String[] splitMessage = message.split(MESSAGE_TOKEN);

        if (splitMessage.length == 2) {
            String messageType = splitMessage[0];
            if (StringUtils.isEqualIgnoringCase(MESSAGE.CURRENT_DIRECTION, messageType)) {
                currentDirection = json.fromJson(Entity.Direction.class, splitMessage[1]);
            }
        }
    }

    @Override
    public void update(Entity entity, float delta) {
        if (KEY_STATE.get(Keys.LEFT)) {
            walk(entity, Entity.Direction.LEFT);
        } else if (KEY_STATE.get(Keys.RIGHT)) {
            walk(entity, Entity.Direction.RIGHT);
        } else if (KEY_STATE.get(Keys.UP)) {
            walk(entity, Entity.Direction.UP);
        } else if (KEY_STATE.get(Keys.DOWN)) {
            walk(entity, Entity.Direction.DOWN);
        } else if (KEY_STATE.get(Keys.QUIT)) {
            Gdx.app.exit();
        } else {
            entity.sendMessage(MESSAGE.CURRENT_STATE, json.toJson(Entity.State.IDLE));
            if (currentDirection == null) {
                entity.sendMessage(MESSAGE.CURRENT_DIRECTION, json.toJson(Entity.Direction.DOWN));
            }
        }

        if (MOUSE_STATE.get(Mouse.SELECT)) {
            entity.sendMessage(MESSAGE.INIT_SELECT_ENTITY, json.toJson(lastMouseCoordinates));
            MOUSE_STATE.put(Mouse.SELECT, false);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.LEFT) {
            this.leftPressed();
        } else if (keycode == Input.Keys.RIGHT) {
            this.rightPressed();
        } else if (keycode == Input.Keys.UP) {
            this.upPressed();
        } else if (keycode == Input.Keys.DOWN) {
            this.downPressed();
        } else if (keycode == Input.Keys.Q) {
            this.quitPressed();
        }
        return true;
    }

    private void leftPressed() {
        KEY_STATE.put(Keys.LEFT, true);
    }

    private void rightPressed() {
        KEY_STATE.put(Keys.RIGHT, true);
    }

    private void upPressed() {
        KEY_STATE.put(Keys.UP, true);
    }

    private void downPressed() {
        KEY_STATE.put(Keys.DOWN, true);
    }

    private void quitPressed() {
        KEY_STATE.put(Keys.QUIT, true);
    }


    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.LEFT) {
            this.leftReleased();
        } else if (keycode == Input.Keys.RIGHT) {
            this.rightReleased();
        } else if (keycode == Input.Keys.UP) {
            this.upReleased();
        } else if (keycode == Input.Keys.DOWN) {
            this.downReleased();
        } else if (keycode == Input.Keys.Q) {
            this.quitReleased();
        }
        return true;
    }

    private void leftReleased() {
        KEY_STATE.put(Keys.LEFT, false);
    }

    private void rightReleased() {
        KEY_STATE.put(Keys.RIGHT, false);
    }

    private void upReleased() {
        KEY_STATE.put(Keys.UP, false);
    }

    private void downReleased() {
        KEY_STATE.put(Keys.DOWN, false);
    }

    private void quitReleased() {
        KEY_STATE.put(Keys.QUIT, false);
    }



    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
            this.setClickedMouseCoordinates(screenX, screenY);

            if (button == Input.Buttons.LEFT) {
                this.selectMouseButtonPressed(screenX, screenY);
            } else {
                this.doActionMouseButtonPressed(screenX, screenY);
            }
        }

        return true;
    }

    private void setClickedMouseCoordinates(int x, int y) {
        lastMouseCoordinates.set(x, y, 0);
    }

    private void selectMouseButtonPressed(int x, int y) {
        MOUSE_STATE.put(Mouse.SELECT, true);
    }

    private void doActionMouseButtonPressed(int x, int y) {
        MOUSE_STATE.put(Mouse.DO_ACTION, true);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            this.selectMouseButtonReleased(screenX, screenY);
        }
        if (button == Input.Buttons.RIGHT) {
            this.doActionMouseButtonReleased(screenX, screenY);
        }
        return true;
    }

    private void selectMouseButtonReleased(int x, int y) {
        MOUSE_STATE.put(Mouse.SELECT, false);
    }


    private void doActionMouseButtonReleased(int x, int y) {
        MOUSE_STATE.put(Mouse.DO_ACTION, false);
    }

    public static void hide() {
        for (Keys value : Keys.values()) {
            KEY_STATE.put(value, false);
        }
    }

    @Override
    public boolean keyTyped(char character) {
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
