package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.util.StringUtils;

public class NPCPhysicsComponent extends PhysicsComponent {

    private Entity.State state;

    public NPCPhysicsComponent() {
        this.boundingBoxLocation = BoundingBoxLocation.CENTER;
        initBoundingBox(0.4f, 0.15f);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void receiveMessage(String message) {
        String[] splitMessage = message.split(Component.MESSAGE_TOKEN);

        if( splitMessage.length == 2 ) {
            String messageType = splitMessage[0];
            String jsonContent = splitMessage[1];
            if (StringUtils.isEqualIgnoringCase(MESSAGE.INIT_START_POSITION, messageType)) {
                currentEntityPosition = json.fromJson(Vector2.class, jsonContent);
                nextEntityPosition.set(currentEntityPosition.x, currentEntityPosition.y);
            } else if (StringUtils.isEqualIgnoringCase(MESSAGE.CURRENT_STATE, messageType)) {
                state = json.fromJson(Entity.State.class, jsonContent);
            } else if (StringUtils.isEqualIgnoringCase(MESSAGE.CURRENT_DIRECTION, messageType)) {
                currentDirection = json.fromJson(Entity.Direction.class, jsonContent);
            }
        }
    }

    @Override
    public void update(Entity entity, MapManager mapManager, float delta) {
        updateBoundingBoxPosition(nextEntityPosition);

        if (Entity.State.IMMOBILE != state) {
            if (Entity.State.WALKING == state && !isCollisionWithMapLayer(entity, mapManager) && !isCollisionWithMapEntities(entity, mapManager)){
                setNextPositionToCurrent(entity);
            } else {
                updateBoundingBoxPosition(currentEntityPosition);
            }
            calculateNextPosition(delta);
        }
    }

    @Override
    protected boolean isCollisionWithMapEntities(Entity entity, MapManager mapManager) {
        if (isCollision(entity, mapManager.getPlayer())) {
            return true;
        }
        return super.isCollisionWithMapEntities(entity, mapManager);
    }
}
