package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.map.GameMap;

public abstract class PhysicsComponent implements Component {
    private static String TAG = PhysicsComponent.class.getSimpleName();

    protected BoundingBoxLocation boundingBoxLocation;
    protected Vector2 nextEntityPosition;
    protected Vector2 currentEntityPosition;
    protected Entity.Direction currentDirection;
    protected Json json;
    protected Rectangle boundingBox;
    private Vector2 velocity;


    public enum BoundingBoxLocation {
        BOTTOM_LEFT, BOTTOM_CENTER, CENTER

    }

    PhysicsComponent() {
        this.nextEntityPosition = new Vector2(0, 0);
        this.currentEntityPosition = new Vector2(0, 0);
        this.json = new Json();
        this.boundingBox = new Rectangle();
        this.velocity = new Vector2(2f, 2f);
        this.boundingBoxLocation = BoundingBoxLocation.BOTTOM_LEFT;
    }

    public abstract void update(Entity entity, MapManager mapManager, float delta);

    protected boolean isCollisionWithMapEntities(Entity entity, MapManager mapManager) {
        Array<Entity> entities = mapManager.getCurrentEntities();

        for (Entity mapEntity : entities) {
            Rectangle target = mapEntity.getCurrentBoundingBox();
            if (!mapEntity.equals(entity) && target.overlaps(boundingBox)) {
                entity.sendMessage(MESSAGE.COLLISION_WITH_ENTITY);
                return true;
            }
        }
        return false;
    }

    protected boolean isCollision(Entity source, Entity target) {
        if (!source.equals(target) && source.getCurrentBoundingBox().overlaps(target.getCurrentBoundingBox())) {
            source.sendMessage(MESSAGE.COLLISION_WITH_ENTITY);
            return true;
        } else {
            return false;
        }
    }

    protected boolean isCollisionWithMapLayer(Entity entity, MapManager mapManager) {
        MapLayer collisionLayer = mapManager.getCollisionLayer();
        if (collisionLayer != null) {
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject r && boundingBox.overlaps(r.getRectangle())) {
                    entity.sendMessage(MESSAGE.COLLISION_WITH_MAP);
                    return true;
                }
            }
        }
        return false;
    }

    public void setNextPositionToCurrent(Entity entity) {
        this.currentEntityPosition.x = nextEntityPosition.x;
        this.currentEntityPosition.y = nextEntityPosition.y;

        entity.sendMessage(MESSAGE.CURRENT_POSITION, json.toJson(currentEntityPosition));
    }

    public void calculateNextPosition(float deltaTime) {
        if (currentDirection != null && deltaTime > 0) {
            float testX = this.currentEntityPosition.x;
            float testY = this.currentEntityPosition.y;

            velocity.scl(deltaTime);

            switch (currentDirection) {
                case LEFT -> testX -= velocity.x;
                case RIGHT -> testX += velocity.x;
                case UP -> testY += velocity.y;
                case DOWN -> testY -= velocity.y;
            }

            nextEntityPosition.x = testX;
            nextEntityPosition.y = testY;

            velocity.scl(1/deltaTime);
        }
    }

    protected void initBoundingBox(float percentageWidthReduced, float percentageHeightReduced) {
        float width;
        float height;

        float originalWidth = Entity.FRAME_WIDTH;
        float originalHeight = Entity.FRAME_HEIGHT;

        float widthReductionAmount = 1.0f - percentageWidthReduced;
        float heightReductionAmount = 1.0f - percentageHeightReduced;

        if (widthReductionAmount > 0 && widthReductionAmount < 1) {
            width = Entity.FRAME_WIDTH * widthReductionAmount;
        } else {
            width = Entity.FRAME_WIDTH;
        }

        if (heightReductionAmount > 0 && heightReductionAmount < 1) {
            height = Entity.FRAME_HEIGHT * heightReductionAmount;
        } else {
            height = Entity.FRAME_HEIGHT;
        }

        if (width == 0 || height == 0) {
            Gdx.app.debug(TAG, "Width or height are 0 [width="+width+", height="+height+"]");
        }

        float minX, minY;
        if (GameMap.UNIT_SCALE > 0) {
            minX = nextEntityPosition.x / GameMap.UNIT_SCALE;
            minY = nextEntityPosition.y / GameMap.UNIT_SCALE;
        } else {
            minX = nextEntityPosition.x;
            minY = nextEntityPosition.y;
        }

        boundingBox.setWidth(width);
        boundingBox.setHeight(height);

        switch (boundingBoxLocation) {
            case BOTTOM_LEFT -> boundingBox.set(minX, minY, width, height);
            case BOTTOM_CENTER -> boundingBox.setCenter(minX + originalWidth/2, minY + originalHeight / 4);
            case CENTER -> boundingBox.setCenter(minX + originalWidth/2, minY + originalHeight / 2);
        }
    }

    protected void updateBoundingBoxPosition(Vector2 position) {
        float minX, minY;
        if (GameMap.UNIT_SCALE > 0) {
            minX = position.x / GameMap.UNIT_SCALE;
            minY = position.y / GameMap.UNIT_SCALE;
        } else {
            minX = position.x;
            minY = position.y;
        }


        switch (boundingBoxLocation) {
            case BOTTOM_LEFT -> boundingBox.set(minX, minY, boundingBox.getWidth(), boundingBox.getHeight());
            case BOTTOM_CENTER -> boundingBox.setCenter(minX + Entity.FRAME_WIDTH/2.0f, minY + Entity.FRAME_HEIGHT / 4.0f);
            case CENTER -> boundingBox.setCenter(minX + Entity.FRAME_WIDTH/2.0f, minY + Entity.FRAME_HEIGHT / 2.0f);
        }
    }


    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public Vector2 getCurrentPosition() {
        return currentEntityPosition;
    }
}
