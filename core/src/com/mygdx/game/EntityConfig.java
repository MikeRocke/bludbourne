package com.mygdx.game;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

import static com.mygdx.game.Entity.*;

public class EntityConfig {
    private String entityID;
    private Array<AnimationConfig> animationConfig;
    private State state;
    private Direction direction;

    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Array<AnimationConfig> getAnimationConfig() {
        return animationConfig;
    }

    public void addAnimationConfig(AnimationConfig config) {
        this.animationConfig.add(config);
    }

    static class AnimationConfig {

        private Array<String> texturePaths;
        private Array<GridPoint2> gridPoints;
        private AnimationType animationType;
        private Float frameDuration;

        public Array<String> getTexturePaths() {
            return texturePaths;
        }

        public void setTexturePaths(Array<String> texturePaths) {
            this.texturePaths = texturePaths;
        }

        public Array<GridPoint2> getGridPoints() {
            return gridPoints;
        }

        public void setGridPoints(Array<GridPoint2> gridPoints) {
            this.gridPoints = gridPoints;
        }

        public AnimationType getAnimationType() {
            return animationType;
        }

        public void setAnimationType(AnimationType animationType) {
            this.animationType = animationType;
        }

        public Float getFrameDuration() {
            return frameDuration;
        }

        public void setFrameDuration(Float frameDuration) {
            this.frameDuration = frameDuration;
        }
    }
}
