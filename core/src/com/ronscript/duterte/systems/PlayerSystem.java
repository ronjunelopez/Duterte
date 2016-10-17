package com.ronscript.duterte.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ronscript.duterte.DebugDrawable;
import com.ronscript.duterte.GameWorld;
import com.ronscript.duterte.components.ai.steer.SteeringComponent;
import com.ronscript.duterte.components.game.objects.character.CharacterComponent;
import com.ronscript.duterte.components.game.objects.character.pc.PlayerComponent;
import com.ronscript.duterte.components.inputs.InputComponent;
import com.ronscript.duterte.components.properties.TransformComponent;
import com.ronscript.duterte.systems.game.objects.WeaponSystem;
import com.ronscript.duterte.utils.CameraManager;
import com.ronscript.duterte.utils.CharacterBehaviorBuilder;
import com.ronscript.duterte.utils.DebugShape;
import com.ronscript.duterte.utils.Mapper;
import com.ronscript.duterte.utils.WorldManager;

/**
 * @author Ron
 * @since 8/9/2016
 */
public class PlayerSystem extends IteratingSystem implements DebugDrawable {

    private static Family playerFamily = Family.all(PlayerComponent.class, CharacterComponent.class, InputComponent.class).get();
    Array<Vector2> path = new Array<Vector2>();
    Vector2 lastWorldPoint = new Vector2();
    private PlayerType playerType = PlayerType.SINGLE_PLAYER;
    private int selectedPlayerIndex = 0;
    public PlayerSystem() {
        super(playerFamily);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        updatePlayerCharacter(entity);
    }

    public void setSelectedPlayer(int index) {
        this.selectedPlayerIndex = index;
    }

    /**
     * Process the Player Input and updates the character
     * @param entity is a Player Character
     */
    private void updatePlayerCharacter(Entity entity) {
        TransformComponent transform = Mapper.transform.get(entity);

//        if(!GameWorld.debug) {
//            CameraManager.getCamera().position.set(transform.position.x, transform.position.y, 0);
//        }

        PlayerComponent player = Mapper.player.get(entity);
        CharacterComponent character = Mapper.character.get(entity);
        InputComponent input = Mapper.input.get(entity);


        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            character.currentState = CharacterComponent.MOVE_UP;
        } else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            character.currentState = CharacterComponent.MOVE_DOWN;
        } else if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            character.currentState = CharacterComponent.MOVE_LEFT;
        } else if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            character.currentState = CharacterComponent.MOVE_RIGHT;

        } else {
            character.currentState = CharacterComponent.IDLE;
        }

        WeaponSystem weaponSystem = getEngine().getSystem(WeaponSystem.class);

        Vector2 worldPoint = CameraManager.getScreenToWorldPoint2(input.screenPoint);


        if(Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            CameraManager.getCamera().position.add(1, 0, 0);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            CameraManager.getCamera().position.sub(1, 0, 0);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            CameraManager.getCamera().position.add(0, 1, 0);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            CameraManager.getCamera().position.sub(0, 1, 0);
        }

        //test path-finder

        if(!worldPoint.equals(lastWorldPoint)) {
            path.clear();
            GameWorld.navigator.createPath(transform.position, worldPoint, path);
            lastWorldPoint.set(worldPoint);
        }


        if(Mapper.steering.has(entity)) {
            SteeringComponent steering = Mapper.steering.get(entity);
//            agent.agent.facingLocation.getPosition().set(worldPoint);
            if(path.size > 1) {
                CharacterBehaviorBuilder bb = new CharacterBehaviorBuilder(Mapper.steering.get(entity).agent, WorldManager.getWorld());
                bb.createObstacleAvoidance();
                bb.createFollowPath(path);
                steering.agent.setBehavior(bb.get());
            }

        }

        if(!weaponSystem.hasWeapon(entity)) {
            return ;
        }

        if(Gdx.input.justTouched()) {

            weaponSystem.useWeapon(entity, worldPoint);
        } else {
            weaponSystem.haltWeapon(entity);
        }
    }

//    @Override
//    public void update(float deltaTime) {
//        ImmutableArray<Entity> entities = getEntities();
//        int size = entities.size();
//        switch (playerType) {
//            case SINGLE_PLAYER:
////                Gdx.app.log("", selectedPlayerIndex+"");
//                if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
//                    selectedPlayerIndex -= 1;
//                }
//                if(!(selectedPlayerIndex >= size) && Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
//                    selectedPlayerIndex += 1;
//                }
//                if(size > 0) {
//                    Entity selectedPlayer = getEntities().get(selectedPlayerIndex);
//                    updatePlayerCharacter(selectedPlayer);
//                }
//                break;
//            case MULTI_PLAYER:
//                super.update(deltaTime);
//                break;
//        }
//    }

    @Override
    public void debugDraw(ShapeRenderer shape, float deltaTime) {
        shape.begin();
        DebugShape.debugDrawPath(shape, path, false);
        shape.end();
    }


    enum PlayerType {
       SINGLE_PLAYER,
       MULTI_PLAYER
    }

}
