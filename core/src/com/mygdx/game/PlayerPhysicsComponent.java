package com.mygdx.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.map.GameMap;

import static com.mygdx.game.util.StringUtils.isEqualIgnoringCase;

public class PlayerPhysicsComponent extends PhysicsComponent {

    private Entity.State state;
    private Vector3 mouseSelectCoordinates;
    private boolean isMouseSelectEnabled = false;
    private Ray selectionRay;
    private float selectRayMaximumDistance = 32.0f;

    public PlayerPhysicsComponent() {
        super();
        mouseSelectCoordinates = new Vector3(0, 0, 0);
        selectionRay = new Ray(new Vector3(), new Vector3());
    }

    @Override
    public void dispose() {

    }

    @Override
    public void receiveMessage(String message) {
        String[] splitMessage = message.split(Component.MESSAGE_TOKEN);

        //todo: would be nice to have pattern matching in java
        if (splitMessage.length == 2) {
            String messageType = splitMessage[0];
            if (isEqualIgnoringCase(MESSAGE.INIT_START_POSITION, messageType)) {
                currentEntityPosition = json.fromJson(Vector2.class, splitMessage[1]);
                nextEntityPosition.set(currentEntityPosition.x, currentEntityPosition.y);
            } else if (isEqualIgnoringCase(MESSAGE.CURRENT_STATE, messageType)) {
                state = json.fromJson(Entity.State.class, splitMessage[1]);
            } else if (isEqualIgnoringCase(MESSAGE.CURRENT_DIRECTION, messageType)) {
                currentDirection = json.fromJson(Entity.Direction.class, splitMessage[1]);
            } else if (isEqualIgnoringCase(MESSAGE.INIT_SELECT_ENTITY, messageType)) {
                mouseSelectCoordinates = json.fromJson(Vector3.class, splitMessage[1]);
                isMouseSelectEnabled = true;
            }
        }
    }

    @Override
    public void update(Entity entity, MapManager mapManager, float delta) {
        updateBoundingBoxPosition(nextEntityPosition);
        updatePortalLayerActivation(mapManager);

        if (isMouseSelectEnabled) {
            selectMapEntityCandidate(mapManager);
            isMouseSelectEnabled = false;
        }

        if (Entity.State.WALKING == state && !isCollisionWithMapLayer(entity, mapManager) && !isCollisionWithMapEntities(entity, mapManager)) {
            setNextPositionToCurrent(entity);
            Camera camera = mapManager.getCamera();
            camera.position.set(currentEntityPosition.x, currentEntityPosition.y, 0f);
            camera.update();
        } else {
            updateBoundingBoxPosition(currentEntityPosition);
        }

        calculateNextPosition(delta);
    }

    private boolean updatePortalLayerActivation(MapManager mapManager) {
        MapLayer portalLayer = mapManager.getPortalLayer();

        if (portalLayer != null) {
            for (MapObject object : portalLayer.getObjects()) {
                if (object instanceof RectangleMapObject r) {
                    if (boundingBox.overlaps(r.getRectangle())) {
                        String mapName = object.getName();
                        if (mapName == null) {
                            return false;
                        } else {
                            mapManager.setClosestStartPositionFromScaledUnits(currentEntityPosition);
                            mapManager.loadMap(mapName);

                            currentEntityPosition.x = mapManager.getPlayerStartUnitScaled().x;
                            currentEntityPosition.y = mapManager.getPlayerStartUnitScaled().y;
                            nextEntityPosition.x = mapManager.getPlayerStartUnitScaled().x;
                            nextEntityPosition.y = mapManager.getPlayerStartUnitScaled().y;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void selectMapEntityCandidate(MapManager mapManager) {
        Array<Entity> currentEntities = mapManager.getCurrentMapEntities();

        mapManager.getCamera().unproject(mouseSelectCoordinates);
        mouseSelectCoordinates.x /= GameMap.UNIT_SCALE;
        mouseSelectCoordinates.y /= GameMap.UNIT_SCALE;

        for (Entity mapEntity : currentEntities) {
            mapEntity.sendMessage(MESSAGE.ENTITY_DESELECTED);
            Rectangle entityBox = mapEntity.getCurrentBoundingBox();

            if (entityBox.contains(mouseSelectCoordinates.x, mouseSelectCoordinates.y)) {
                selectionRay.set(this.boundingBox.x, this.boundingBox.y, 0.0f, entityBox.x, entityBox.y, 0.0f);

                float distance = selectionRay.origin.dst(selectionRay.direction);
                if (distance <= selectRayMaximumDistance) {
                    mapEntity.sendMessage(MESSAGE.ENTITY_SELECTED);
                }
            }
        }
    }
}
