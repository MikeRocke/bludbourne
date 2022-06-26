package com.mygdx.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static com.mygdx.game.util.StringUtils.isEqualIgnoringCase;

public class PlayerGraphicsComponent extends GraphicsComponent {


    @Override
    public void dispose() {

    }

    @Override
    public void receiveMessage(String message) {
        String[] splitMessage = message.split(MESSAGE_TOKEN);

        if (splitMessage.length == 2) {
            String messageType = splitMessage[0];
            String jsonContent = splitMessage[1];
            if (isEqualIgnoringCase(MESSAGE.CURRENT_POSITION, messageType)) {
                currentPosition = json.fromJson(Vector2.class, jsonContent);
            } else if (isEqualIgnoringCase(MESSAGE.INIT_START_POSITION, messageType)) {
                currentPosition = json.fromJson(Vector2.class, jsonContent);
            } else if (isEqualIgnoringCase(MESSAGE.CURRENT_STATE, messageType)) {
                currentState = json.fromJson(Entity.State.class, jsonContent);
            } else if (isEqualIgnoringCase(MESSAGE.CURRENT_DIRECTION, messageType)) {
                currentDirection = json.fromJson(Entity.Direction.class, jsonContent);
            } else if (isEqualIgnoringCase(MESSAGE.LOAD_ANIMATIONS, messageType)) {
                EntityConfig entityConfig = json.fromJson(EntityConfig.class, jsonContent);
                for (EntityConfig.AnimationConfig animationConfig : entityConfig.getAnimationConfig()) {
                    Array<String> textureNames = animationConfig.getTexturePaths();
                    Array<GridPoint2> points = animationConfig.getGridPoints();
                    Entity.AnimationType animationType = animationConfig.getAnimationType();
                    Float frameDuration = animationConfig.getFrameDuration();

                    Animation<TextureRegion> animation = null;
                    if (textureNames.size == 1) {
                        animation = loadAnimation(textureNames.first(), points, frameDuration);
                    } else if (textureNames.size == 2) {
                        animation = loadAnimation(textureNames.first(), textureNames.get(1), points, frameDuration);
                    }
                    animations.put(animationType, animation);
                }

            }
        }
    }

    @Override
    public void update(Entity entity, MapManager mapManager, Batch batch, float delta) {
        updateAnimations(delta);

        Camera camera = mapManager.getCamera();
        camera.position.set(currentPosition.x, currentPosition.y, 0f);
        camera.update();

        batch.begin();
        batch.draw(currentFrame, currentPosition.x, currentPosition.y, 1, 1);
        batch.end();
    }
}
