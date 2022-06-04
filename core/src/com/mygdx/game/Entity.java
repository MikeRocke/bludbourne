package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.UUID;

public class Entity {

    private static final String TAG = Entity.class.getSimpleName();
    private static final String DEFAULT_SPRITE_PATH = "sprites/characters/Warrior.png";
    private Vector2 velocity;
    private String entityId;
    private Direction currentDirection = Direction.LEFT;
    private Direction previousDirection = Direction.UP;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> walkDownAnimation;

    private Array<TextureRegion> walkLeftFrames;
    private Array<TextureRegion> walkRightFrames;
    private Array<TextureRegion> walkUpFrames;
    private Array<TextureRegion> walkDownFrames;

    protected Vector2 nextPlayerPosition;
    protected Vector2 currentPlayerPosition;
    protected State state = State.IDLE;
    protected float frameTime = 0f;
    protected Sprite frameSprite;
    protected TextureRegion currentFrame;

    public Rectangle boundingBox;
    public static final int FRAME_WIDTH = 16;
    public static final int FRAME_HEIGHT = 16;

    public enum Direction {
        UP,RIGHT,DOWN,LEFT
    }

    public enum State {
        IDLE, WALKING
    }

    public Entity() {
        initEntity();
    }
    public void initEntity() {
        this.entityId = UUID.randomUUID().toString();
        this.nextPlayerPosition = new Vector2();
        this.currentPlayerPosition = new Vector2();
        this.boundingBox = new Rectangle();
        this.velocity = new Vector2(2f, 2f);

        Utility.loadTextureAsset(DEFAULT_SPRITE_PATH);
        loadDefaultSprite();
        loadAllAnimations();
    }

    public void update(float delta) {
        frameTime = (frameTime + delta) % 5;
        setBoundingBoxSize(0f, 0.5f);
    }

    public void init(float startX, float startY) {
        this.currentPlayerPosition.x = startX;
        this.currentPlayerPosition.y = startY;

        this.nextPlayerPosition.x = startX;
        this.nextPlayerPosition.y = startY;
    }

    public void dispose() {
        Utility.unloadAsset(DEFAULT_SPRITE_PATH);
    }

    public void setState(State state) {
        this.state = state;
    }

    public Sprite getFrameSprite() {
        return frameSprite;
    }

    public TextureRegion getFrame() {
        return currentFrame;
    }

    public Vector2 getCurrentPosition() {
        return currentPlayerPosition;
    }

    public void setCurrentPosition(float x, float y) {
        frameSprite.setX(x);
        frameSprite.setY(y);
        this.currentPlayerPosition.x = x;
        this.currentPlayerPosition.y = y;
    }

    public void setDirection(Direction direction, float deltaTime) {
        this.previousDirection = this.currentDirection;
        this.currentDirection = direction;

        switch (direction) {
            case DOWN -> currentFrame = walkDownAnimation.getKeyFrame(frameTime);
            case LEFT -> currentFrame = walkLeftAnimation.getKeyFrame(frameTime);
            case UP -> currentFrame = walkUpAnimation.getKeyFrame(frameTime);
            case RIGHT -> currentFrame = walkRightAnimation.getKeyFrame(frameTime);
            default -> {}
        }
    }

    public void setNextPositionToCurrent() {
        setCurrentPosition(nextPlayerPosition.x, nextPlayerPosition.y);
    }

    public void calculateNextPosition(Direction currentDirection, float deltaTime) {
        float testX = this.currentPlayerPosition.x;
        float testY = this.currentPlayerPosition.y;

        velocity.scl(deltaTime);

        switch (currentDirection) {
            case LEFT -> testX -= velocity.x;
            case RIGHT -> testX += velocity.x;
            case UP -> testY += velocity.y;
            case DOWN -> testY -= velocity.y;
        }

        nextPlayerPosition.x = testX;
        nextPlayerPosition.y = testY;
        velocity.scl(1/deltaTime);
    }

    private void setBoundingBoxSize(float percentageWidthReduced, float percentageHeightReduced) {
        float width;
        float height;

        float widthReductionAmount = 1.0f - percentageWidthReduced;
        float heightReductionAmount = 1.0f - percentageHeightReduced;

        if (inRange(0, widthReductionAmount, 1)) {
            width = FRAME_WIDTH * widthReductionAmount;
        } else {
            width = FRAME_WIDTH;
        }

        if (inRange(0, heightReductionAmount, 1)) {
            height = FRAME_HEIGHT * heightReductionAmount;
        } else {
            height = FRAME_HEIGHT;
        }

        float minX;
        float minY;

        if (MapManager.UNIT_SCALE > 0) {
            minX = nextPlayerPosition.x / MapManager.UNIT_SCALE;
            minY = nextPlayerPosition.y / MapManager.UNIT_SCALE;
        } else {
            minX = nextPlayerPosition.x;
            minY = nextPlayerPosition.y;
        }

        boundingBox.set(minX, minY, width, height);
    }

    private static boolean inRange(float a, float b, float c) {
        return (a < b && b < c);
    }

    private void loadDefaultSprite() {
        Texture texture = Utility.getTextureAsset(DEFAULT_SPRITE_PATH);
        TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
        frameSprite = new Sprite(textureFrames[0][0].getTexture(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        currentFrame = textureFrames[0][0];
    }

    private void loadAllAnimations() {
        Texture texture = Utility.getTextureAsset(DEFAULT_SPRITE_PATH);
        TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
        int frameCount = 4;
        walkDownFrames = new Array<>(frameCount);
        walkLeftFrames = new Array<>(frameCount);
        walkRightFrames = new Array<>(frameCount);
        walkUpFrames = new Array<>(frameCount);

        for (int i = 0; i < frameCount; i++) {
            for (int j = 0; j < frameCount; j++) {
                TextureRegion region = textureFrames[i][j];
                if (region != null) {
                    if (i == 0) {
                        walkDownFrames.insert(j, region);
                    } else if (i == 1) {
                        walkLeftFrames.insert(j, region);
                    } else if (i == 2) {
                        walkRightFrames.insert(j, region);
                    } else {
                        walkUpFrames.insert(j, region);
                    }
                }
            }
        }

        walkDownAnimation = new Animation<>(0.25f, walkDownFrames, Animation.PlayMode.LOOP);
        walkLeftAnimation = new Animation<>(0.25f, walkLeftFrames, Animation.PlayMode.LOOP);
        walkRightAnimation = new Animation<>(0.25f, walkRightFrames, Animation.PlayMode.LOOP);
        walkUpAnimation = new Animation<>(0.25f, walkUpFrames, Animation.PlayMode.LOOP);
    }
}
