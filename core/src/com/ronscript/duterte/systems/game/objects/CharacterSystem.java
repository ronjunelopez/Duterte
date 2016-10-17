package com.ronscript.duterte.systems.game.objects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ronscript.duterte.components.ai.steer.SteeringComponent;
import com.ronscript.duterte.components.game.objects.character.CharacterComponent;
import com.ronscript.duterte.components.game.objects.character.PoliceComponent;
import com.ronscript.duterte.components.game.objects.character.pc.PlayerComponent;
import com.ronscript.duterte.components.graphics.StateComponent;
import com.ronscript.duterte.components.inputs.InputComponent;
import com.ronscript.duterte.components.physics.PhysicsComponent;
import com.ronscript.duterte.utils.Mapper;
import com.ronscript.duterte.utils.SteeringUtils;

/**
 * Character Types
 *      Player-Character is controlled by a playerCharacter
 *      Non-Player-Character is controlled by a computer it self (Artificial Intelligence) with a friendly behavior
 *      Enemy-Character is also an AI but with a different behavior
 * @author Ron
 * @since 8/9/2016
 */
public class CharacterSystem extends IteratingSystem {


    static Family characterFamily = Family.all(CharacterComponent.class , PhysicsComponent.class).get();

    // Autonomous family at least has one of the following AI component
    private Family autonomousFamily = Family
            .one(SteeringComponent.class).get();

    private Family playerFamily = Family
            .all(PlayerComponent.class, CharacterComponent.class, InputComponent.class).get();


    public CharacterSystem() {
        super(characterFamily);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        updateCharacter(entity);
        updateNonPlayerCharacter(entity);
        updatePlayerCharacterMovement(entity);
    }

    private void updateNonPlayerCharacter(Entity entity) {
        if(autonomousFamily.matches(entity)) {

            StateComponent state = Mapper.state.get(entity);
            SteeringComponent steering = Mapper.steering.get(entity);
            SteeringUtils.SteerDirection direction;
            if(Mapper.vision.has(entity)) {
                direction = SteeringUtils.getDirection(Mapper.vision.get(entity).facingAngle);
            } else {
                direction = steering.agent.getDirection();
            }

            WeaponSystem weapon = getEngine().getSystem(WeaponSystem.class);

            switch (direction) {
                case NORTH: state.set(CharacterComponent.MOVE_UP); break;
                case SOUTH: state.set(CharacterComponent.MOVE_DOWN); break;
                case NORTH_EAST: left(entity, weapon, state);break;
                case EAST: left(entity, weapon, state);break;
                case SOUTH_EAST:left(entity, weapon, state);break;
                case NORTH_WEST: right(entity, weapon, state); break;
                case WEST: right(entity, weapon, state); break;
                case SOUTH_WEST: right(entity, weapon, state); break;
                case NONE: state.set(CharacterComponent.IDLE); break;
            }
        }
    }

    private void left(Entity owner, WeaponSystem weapon, StateComponent state) {
        if(weapon.hasWeapon(owner) && weapon.isUsing(owner)) {
            state.set(PoliceComponent.GUN_FIRE_LEFT);
        } else {
            state.set(CharacterComponent.MOVE_LEFT);
        }
    }

    private void right(Entity owner, WeaponSystem weapon, StateComponent state) {
        if(weapon.hasWeapon(owner) && weapon.isUsing(owner)) {
            state.set(PoliceComponent.GUN_FIRE_RIGHT);
        } else {
            state.set(CharacterComponent.MOVE_RIGHT);
        }
    }

    private void updatePlayerCharacterMovement(Entity entity) {
        // Check first if the entity is a member of playerCharacter family
//        if(playerFamily.matches(entity)) {
//            PlayerComponent player = Mapper.player.get(entity);
//            CharacterComponent character = Mapper.character.get(entity);
//            PhysicsComponent physics = Mapper.physics.get(entity);
//            Body body = physics.body;
//            Vector2 velocity = body.getLinearVelocity();
//            float speed =  player.MAX_LINEAR_ACCELERATION;
//
//            switch (character.currentState) {
//                case CharacterComponent.IDLE:
//                    velocity.y *= 0.8f;
//                    velocity.x *= 0.8f;
//                    break;
//                case CharacterComponent.MOVE_UP: velocity.y +=  speed; break;
//                case CharacterComponent.MOVE_DOWN: velocity.y -= speed; break;
//                case CharacterComponent.MOVE_RIGHT: velocity.x += speed; break;
//                case CharacterComponent.MOVE_LEFT: velocity.x -= speed; break;
//            }
//            AnimationComponent animation = Mapper.animation.get(entity);
//
//            if(!velocity.isZero()) {
//                animation.looping = true;
//                body.applyLinearImpulse(velocity, body.getWorldCenter(), true);
//                velocity.limit(player.MAX_LINEAR_SPEED);
//                body.setLinearVelocity(velocity);
//            } else {
//                animation.looping = false;
//            }
//
//            StateComponent state = Mapper.state.get(entity);
//            state.set(character.currentState);
//        }
    }

    private void updateCharacter(Entity entity) {
        CharacterComponent character = Mapper.character.get(entity);

    }



}
