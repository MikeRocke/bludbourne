package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.util.EnumMap;

public abstract class GraphicsComponent implements Component {

    protected EnumMap<Entity.AnimationType, Animation<TextureRegion>> animations;
    protected ShapeRenderer shapeRenderer;
    protected float frameTime = 0f;
    protected Vector2 currentPosition;
    protected TextureRegion currentFrame;
    protected Json json;
    protected Entity.State currentState;
    protected Entity.Direction currentDirection;

    protected GraphicsComponent() {
        shapeRenderer = new ShapeRenderer();
        animations = new EnumMap<>(Entity.AnimationType.class);
        json = new Json();
        this.currentDirection = Entity.Direction.DOWN;
        this.currentState = Entity.State.WALKING;
        this.currentPosition = new Vector2(0, 0);
    }

    public abstract void update(Entity entity, MapManager mapManager, Batch batch, float delta);

    protected void updateAnimations(float delta) {
        frameTime = (frameTime + delta) % 5;

        Entity.AnimationType animationType = Entity.AnimationType.IMMOBILE;

        if (currentState == Entity.State.IDLE || currentState == Entity.State.WALKING) {
            switch (currentDirection) {
                case UP -> {
                    animationType = Entity.AnimationType.WALK_UP;
                }
                case RIGHT -> {
                    animationType = Entity.AnimationType.WALK_RIGHT;
                }
                case DOWN -> {
                    animationType = Entity.AnimationType.WALK_DOWN;
                }
                case LEFT -> {
                    animationType = Entity.AnimationType.WALK_LEFT;
                }
            }
        }
        Animation<TextureRegion> animation = animations.get(animationType);
        if (animation != null) {
            if (currentState == Entity.State.IDLE) {
                currentFrame = animation.getKeyFrames()[0];
            } else {
                currentFrame = animation.getKeyFrame(frameTime);
            }
        }
    }

    protected Animation<TextureRegion> loadAnimation(String firstTexture, String secondTexture, Array<GridPoint2> points, float frameDuration) {
        Utility.loadTextureAsset(firstTexture);
        Texture textureOne = Utility.getTextureAsset(firstTexture);

        Utility.loadTextureAsset(secondTexture);
        Texture textureTwo = Utility.getTextureAsset(secondTexture);

        TextureRegion[][] textureOneFrames = TextureRegion.split(textureOne, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT);
        TextureRegion[][] textureTwoFrames = TextureRegion.split(textureTwo, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT);

        Array<TextureRegion> animationKeyFrames = new Array<>(TextureRegion.class);
        GridPoint2 point = points.first();

        animationKeyFrames.add(textureOneFrames[point.x][point.y]);
        animationKeyFrames.add(textureTwoFrames[point.x][point.y]);

        return new Animation<>(frameDuration, animationKeyFrames, Animation.PlayMode.LOOP);
    }

    protected Animation<TextureRegion> loadAnimation(String textureName, Array<GridPoint2> points, float frameDuration) {
        Utility.loadTextureAsset(textureName);

        Texture texture = Utility.getTextureAsset(textureName);
        TextureRegion[][] textureFrames = TextureRegion.split(texture, Entity.FRAME_WIDTH, Entity.FRAME_HEIGHT);

        Array<TextureRegion> animationKeyFrames = new Array<>(false, points.size, TextureRegion.class);

        for (GridPoint2 point : points) {
            animationKeyFrames.add(textureFrames[point.x][point.y]);
        }

        return new Animation<TextureRegion>(frameDuration, animationKeyFrames, Animation.PlayMode.LOOP);
    }

}
