package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

import java.util.EnumMap;

public class PlayerController implements InputProcessor {

    private static final String TAG = PlayerController.class.getSimpleName();

    enum MyKeys {
        LEFT, RIGHT, UP, DOWN, QUIT
    }

    enum MyMouse {
        SELECT, DO_ACTION
    }

    //TODO: why are these static?
    private static final EnumMap<MyKeys, Boolean> KEY_STATE = new EnumMap<>(MyKeys.class);
    private static final EnumMap<MyMouse, Boolean> MOUSE_STATE = new EnumMap<>(MyMouse.class);
    static {
        for (MyKeys value : MyKeys.values()) {
            KEY_STATE.put(value, false);
        }

        for (MyMouse value : MyMouse.values()) {
            MOUSE_STATE.put(value, false);
        }
    }

    private Entity player;
    private Vector3 lastMouseCoordinates;

    public PlayerController(Entity player) {
        this.player = player;
        this.lastMouseCoordinates = new Vector3();
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
        KEY_STATE.put(MyKeys.LEFT, true);
    }

    private void rightPressed() {
        KEY_STATE.put(MyKeys.RIGHT, true);
    }

    private void upPressed() {
        KEY_STATE.put(MyKeys.UP, true);
    }

    private void downPressed() {
        KEY_STATE.put(MyKeys.DOWN, true);
    }

    private void quitPressed() {
        KEY_STATE.put(MyKeys.QUIT, true);
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
        KEY_STATE.put(MyKeys.LEFT, false);
    }

    private void rightReleased() {
        KEY_STATE.put(MyKeys.RIGHT, false);
    }

    private void upReleased() {
        KEY_STATE.put(MyKeys.UP, false);
    }

    private void downReleased() {
        KEY_STATE.put(MyKeys.DOWN, false);
    }

    private void quitReleased() {
        KEY_STATE.put(MyKeys.QUIT, false);
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
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
        MOUSE_STATE.put(MyMouse.SELECT, true);
    }

    private void doActionMouseButtonPressed(int x, int y) {
        MOUSE_STATE.put(MyMouse.DO_ACTION, true);
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
        MOUSE_STATE.put(MyMouse.SELECT, false);
    }


    private void doActionMouseButtonReleased(int x, int y) {
        MOUSE_STATE.put(MyMouse.DO_ACTION, false);
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

    public void update(float delta) {
        if (KEY_STATE.get(MyKeys.LEFT)) {
            walk(Entity.Direction.LEFT, delta);
        } else if (KEY_STATE.get(MyKeys.RIGHT)) {
            walk(Entity.Direction.RIGHT, delta);
        } else if (KEY_STATE.get(MyKeys.UP)) {
            walk(Entity.Direction.UP, delta);
        } else if (KEY_STATE.get(MyKeys.DOWN)) {
            walk(Entity.Direction.DOWN, delta);
        } else if (KEY_STATE.get(MyKeys.QUIT)) {
            Gdx.app.exit();
        } else {
            player.setState(Entity.State.IDLE);
        }

        if (MOUSE_STATE.get(MyMouse.SELECT)) {
            MOUSE_STATE.put(MyMouse.SELECT, false);
        }
    }

    private void walk(Entity.Direction direction, float delta) {
        player.calculateNextPosition(direction, delta);
        player.setState(Entity.State.WALKING);
        player.setDirection(direction, delta);
    }

    public static void hide() {
        for (MyKeys value : MyKeys.values()) {
            KEY_STATE.put(value, false);
        }
    }

    public void dispose() {

    }
}
