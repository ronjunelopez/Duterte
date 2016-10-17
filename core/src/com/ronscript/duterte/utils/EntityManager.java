package com.ronscript.duterte.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ronscript.duterte.GameAssets;
import com.ronscript.duterte.ai.fsm.Police;
import com.ronscript.duterte.ai.steer.PoliceSteeringAgent;
import com.ronscript.duterte.ai.steer.SteeringAgent;
import com.ronscript.duterte.components.GroupComponent;
import com.ronscript.duterte.components.ai.fsm.StateMachineComponent;
import com.ronscript.duterte.components.ai.msg.MessagingComponent;
import com.ronscript.duterte.components.ai.steer.BehaviorComponent;
import com.ronscript.duterte.components.ai.steer.SteeringComponent;
import com.ronscript.duterte.components.game.objects.BulletComponent;
import com.ronscript.duterte.components.game.objects.ExpirationComponent;
import com.ronscript.duterte.components.game.objects.HealthBarComponent;
import com.ronscript.duterte.components.game.objects.InventoryComponent;
import com.ronscript.duterte.components.game.objects.WeaponComponent;
import com.ronscript.duterte.components.game.objects.WeaponType;
import com.ronscript.duterte.components.game.objects.character.CharacterComponent;
import com.ronscript.duterte.components.game.objects.character.CriminalComponent;
import com.ronscript.duterte.components.game.objects.character.PoliceComponent;
import com.ronscript.duterte.components.game.objects.character.VisionComponent;
import com.ronscript.duterte.components.game.objects.character.pc.PlayerComponent;
import com.ronscript.duterte.components.game.objects.character.stats.DamageComponent;
import com.ronscript.duterte.components.game.objects.character.stats.HealthComponent;
import com.ronscript.duterte.components.graphics.AnimationComponent;
import com.ronscript.duterte.components.graphics.SpriteComponent;
import com.ronscript.duterte.components.graphics.StateComponent;
import com.ronscript.duterte.components.inputs.InputComponent;
import com.ronscript.duterte.components.physics.CollisionComponent;
import com.ronscript.duterte.components.physics.GravityComponent;
import com.ronscript.duterte.components.physics.MovementComponent;
import com.ronscript.duterte.components.physics.PhysicsComponent;
import com.ronscript.duterte.components.properties.AlignmentComponent;
import com.ronscript.duterte.components.properties.AttachmentComponent;
import com.ronscript.duterte.components.properties.SizeComponent;
import com.ronscript.duterte.components.properties.TransformComponent;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class EntityManager {
    private static EntityManager ourInstance = new EntityManager();
    public PooledEngine engine;
    public World world;
    public SteeringAgent playerAgent;
    private Array<Entity> entityToDestroy = new Array<Entity>();

    private EntityManager() {
    }

    public static EntityManager getInstance() {
        return ourInstance;
    }

    public static void safeDestroy(Entity entity) {
//        if(!entity.isScheduledForRemoval()) {
            getInstance().entityToDestroy.add(entity);
//        }
    }

    public void init(PooledEngine engine, World world) {
        this.engine = engine;
        this.world = world;
    }

    public Entity createGroup(Vector2 position, Vector2 scale, float rotation, Entity ... members) {
        Entity entity = engine.createEntity();

        GroupComponent group = engine.createComponent(GroupComponent.class);
        TransformComponent transform = engine.createComponent(TransformComponent.class);

        transform.position.set(position);
        transform.scale.set(scale);
        transform.rotation = rotation;

        for (Entity child: members) {
            group.members.add(child);
        }

        entity.add(group);
        entity.add(transform);

        engine.addEntity(entity);
        return entity;
    }

    public Entity createDruggieCharacter(float x, float y) {
        CharacterBuilder cb = new CharacterBuilder(this);

        Entity e = cb.createEntity();

        Body body = BodyBuilder.createDynamicBody(world, x, y);
        body.setFixedRotation(true);
        BodyBuilder.createCharacterFoot(body, e);
//        BodyBuilder.createCharacterVisionBody(body, 0.5f, e);
        body.setUserData(e);

        cb.setPosition(x, y);
        cb.setOriginY(0.2f);
        cb.setBody(body);
        cb.addAnimation(CharacterComponent.MOVE_DOWN, GameAssets.duterte_walk_front);
        cb.addAnimation(CharacterComponent.MOVE_UP, GameAssets.duterte_walk_back);
        cb.addAnimation(CharacterComponent.MOVE_LEFT, GameAssets.duterte_walk_left_side);
        cb.addAnimation(CharacterComponent.MOVE_RIGHT, GameAssets.duterte_walk_right_side);

        // extra components
        CriminalComponent criminal = engine.createComponent(CriminalComponent.class);
        SteeringComponent steering = engine.createComponent(SteeringComponent.class);
        SteeringAgent agent = new SteeringAgent(body, true, 1);
        // create behavior
        CharacterBehaviorBuilder bb = new CharacterBehaviorBuilder(agent, world);
        bb.createObstacleAvoidance();
        bb.createWander();
        // init speed & accel
        agent.setMaxLinearAcceleration(0.1f);
        agent.setMaxLinearSpeed(1);
        agent.setBehavior(bb.get());
        steering.agent = agent;
        e.add(criminal).add(steering);
        // build
        cb.build(EntityFlags.CHARACTER);
        return e;
    }

    /**
     // [mood] = aggressive, peace
     // is an playerCharacter police by default
     // can be turn into civilian, drug user or pusher
     // pursue drug user and kill
     // chase criminal
     */
    public Entity createPoliceCharacter(float x, float y) {
        final Entity entity = engine.createEntity();
        entity.flags = EntityFlags.CHARACTER;

        CharacterComponent character = engine.createComponent(CharacterComponent.class);
        VisionComponent vision = engine.createComponent(VisionComponent.class);
        PoliceComponent police = engine.createComponent(PoliceComponent.class);
//        CombatComponent combat = engine.createComponent(CombatComponent.class);

        SizeComponent size = engine.createComponent(SizeComponent.class);
        SpriteComponent sprite = engine.createComponent(SpriteComponent.class);
        PhysicsComponent physics = engine.createComponent(PhysicsComponent.class);
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        AnimationComponent animation = engine.createComponent(AnimationComponent.class);
        StateComponent state = engine.createComponent(StateComponent.class);

        SteeringComponent agent = engine.createComponent(SteeringComponent.class);
        BehaviorComponent behavior = engine.createComponent(BehaviorComponent.class);
        StateMachineComponent stateMachine = engine.createComponent(StateMachineComponent.class);
        MessagingComponent messaging = engine.createComponent(MessagingComponent.class);



        vision.distance = 4; // 4 meters
        animation.looping = true;
        animation.map.put(CharacterComponent.MOVE_DOWN, GameAssets.police_walk_front);
        animation.map.put(CharacterComponent.MOVE_UP, GameAssets.police_walk_back);
        animation.map.put(CharacterComponent.MOVE_LEFT, GameAssets.police_walk_left_side);
        animation.map.put(CharacterComponent.MOVE_RIGHT, GameAssets.police_walk_right_side);
        animation.map.put(PoliceComponent.GUN_FIRE_LEFT, GameAssets.police_firing_left_side);
        animation.map.put(PoliceComponent.GUN_FIRE_RIGHT, GameAssets.police_firing_right_side);
        transform.origin.y = 0.2f;
        transform.position.set(x,y);

        Body body = BodyBuilder.createDynamicBody(world, x, y);
        body.setFixedRotation(true);
        BodyBuilder.createCharacterFoot(body, entity);

        body.setUserData(entity);

//        PoliceSteeringAgent steer = new PoliceSteeringAgent(agents, body, leader);
        PoliceSteeringAgent steer = new PoliceSteeringAgent(world, body);

//        CharacterBehaviorBuilder bb = new CharacterBehaviorBuilder(steer, world);
//        bb.createObstacleAvoidance();
//        bb.createWander();
//        bb.createPursue(null);
//        steer.setBehavior(bb.get());
//        behavior.base = bb.get();
//        behavior.base = bb.createFollowPlayer(steer, playerAgent, agents);


        DefaultStateMachine dsm = new DefaultStateMachine();
        stateMachine.fsm = dsm;
        Police p = new Police(police, dsm, steer, entity);
        dsm.setOwner(p);
        messaging.telegraph = p;
        physics.body = body;
        agent.agent = steer;

        entity
                .add(vision)
                .add(character)
                .add(police)
//                .add(combat)
                .add(sprite)
                .add(animation)
                .add(state)
                .add(size)
                .add(physics).add(collision)
                .add(transform)
                .add(messaging)
                .add(agent)
                .add(behavior)
                .add(stateMachine);

        engine.addEntity(entity);
        return entity;
    }


    /**
     * Create a Player-Character or (PC) is a character controlled by a playerCharacter/user
     * @param x is for 2d spatial position
     * @param y is for 2d spatial position
     * @return playerCharacter-character entity
     */

    public Entity createPlayerCharacter(float x, float y) {
        Entity entity = engine.createEntity();
        entity.flags = EntityFlags.CHARACTER;

//        VisionComponent vision = engine.createComponent(VisionComponent.class);
//        CombatComponent combat = engine.createComponent(CombatComponent.class);
        PlayerComponent player = engine.createComponent(PlayerComponent.class);
        CharacterComponent character = engine.createComponent(CharacterComponent.class);
        InputComponent input = engine.createComponent(InputComponent.class);
        HealthComponent health = engine.createComponent(HealthComponent.class);
        SpriteComponent sprite = engine.createComponent(SpriteComponent.class);
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        SizeComponent size = engine.createComponent(SizeComponent.class);
        PhysicsComponent physics = engine.createComponent(PhysicsComponent.class);
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        AnimationComponent animation = engine.createComponent(AnimationComponent.class);
        StateComponent state = engine.createComponent(StateComponent.class);
        SteeringComponent steering = engine.createComponent(SteeringComponent.class);

        transform.origin.y = 0.2f;
        transform.position.set(x, y);
//        transform.scale.set(1.5F, 1.5F);
        animation.looping = true;
//        animation.map.put(PlayerComponent.IDLE, GameAssets.duterte_walk);
//        animation.map.put(PlayerComponent.MOVE_RIGHT, GameAssets.duterte_walk);
        animation.map.put(CharacterComponent.MOVE_DOWN, GameAssets.duterte_walk_front);
        animation.map.put(CharacterComponent.MOVE_UP, GameAssets.duterte_walk_back);
        animation.map.put(CharacterComponent.MOVE_LEFT, GameAssets.duterte_walk_left_side);
        animation.map.put(CharacterComponent.MOVE_RIGHT, GameAssets.duterte_walk_right_side);

        Body body = BodyBuilder.createDynamicBody(world, x, y);
//        body.setFixedRotation(true);
        BodyBuilder.createCharacterFoot(body, entity);
        body.setUserData(entity);
        physics.body = body;
        playerAgent =  new SteeringAgent(body, true, 1);
        playerAgent.setMaxLinearSpeed(1);
        playerAgent.setMaxLinearAcceleration(0.1f);
        playerAgent.setMaxAngularAcceleration(0.01f);
        playerAgent.setMaxAngularSpeed(0.1f);
        steering.agent = playerAgent;

        entity
                .add(steering)
//                .add(vision)
//                .add(combat)
                .add(player).add(input)
                .add(character).add(health)
                // Game objects
                .add(sprite).add(transform) // Renderable
                .add(size)
                .add(physics)
                .add(collision)
                .add(animation).add(state) // Animateable
        ; // Controller

        engine.addEntity(entity);
        return entity;
    }


    public Entity createHealthBackground (float x, float y, Entity owner) {
        Entity entity = engine.createEntity();

        AttachmentComponent attachment = engine.createComponent(AttachmentComponent.class);
        SpriteComponent sprite = engine.createComponent(SpriteComponent.class);
        AlignmentComponent alignment = engine.createComponent(AlignmentComponent.class);
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        SizeComponent size = engine.createComponent(SizeComponent.class);
        sprite.textureRegion = GameAssets.health_background;
        transform.position.set(x, y);
//        transform.scale.set(2,2);

        attachment.owner = owner;
        alignment.position.set(0, 1.3f);


        entity.add(sprite);
        entity.add(transform);
        entity.add(size);
        entity.add(alignment);
        entity.add(attachment);

        engine.addEntity(entity);
        return entity;
    }

    public Entity createHealthBar(float x, float y, Entity background, Entity parent) {
        Entity entity = engine.createEntity();

        AttachmentComponent attachment = engine.createComponent(AttachmentComponent.class);
        AlignmentComponent alignment = engine.createComponent(AlignmentComponent.class);
        HealthBarComponent healthBar = engine.createComponent(HealthBarComponent.class);
        SpriteComponent sprite = engine.createComponent(SpriteComponent.class);
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        SizeComponent size = engine.createComponent(SizeComponent.class);


        healthBar.background = background;
        attachment.owner = parent;
        alignment.position.set(0, 1.3f);
        sprite.textureRegion = GameAssets.health_bar;
//        bounds.width = 32;
//        bounds.height = 3;
        transform.position.set(x, y);
//        transform.scale.set(2,2);

//        entity.add(shape);
//        entity.add(bounds);
        entity.add(attachment);
        entity.add(healthBar);
        entity.add(sprite);
        entity.add(transform);
        entity.add(size);
        entity.add(alignment);

        engine.addEntity(entity);
        return entity;
    }


    public Entity createInventory(Entity owner, Entity ... items) {
        Entity entity = engine.createEntity();

        AttachmentComponent attachment = engine.createComponent(AttachmentComponent.class);
        InventoryComponent inventory = engine.createComponent(InventoryComponent.class);
        TransformComponent transform = engine.createComponent(TransformComponent.class);

        attachment.owner = owner;

        for (Entity item: items) {
            inventory.items.add(item);
        }

        entity.add(attachment);
        entity.add(inventory);
        entity.add(transform);

        engine.addEntity(entity);

        return entity;
    }

    public Entity createGun(float x, float y, Entity parent) {
        Entity entity = createSprite(null, x,y);
        entity.flags = EntityFlags.WEAPON;

        WeaponComponent weapon = engine.createComponent(WeaponComponent.class);
        DamageComponent damage = engine.createComponent(DamageComponent.class);

        MovementComponent movement = engine.createComponent(MovementComponent.class);
        AttachmentComponent attachment = engine.createComponent(AttachmentComponent.class);
        AlignmentComponent alignment = engine.createComponent(AlignmentComponent.class);


        damage.current = 10;
        weapon.type = WeaponType.RANGE;
        weapon.factory = this;
        attachment.owner = parent;

        entity.add(attachment);
        entity.add(weapon);
        entity.add(damage);
        entity.add(movement);
        entity.add(alignment);

        engine.addEntity(entity);

        return entity;
    }

    public Entity createBullet(float x, float y, Entity shadow) {
        Entity entity = createSprite(GameAssets.bullet, x, y);

        entity.flags = EntityFlags.BULLET;

        ExpirationComponent expiration = engine.createComponent(ExpirationComponent.class);
        GravityComponent gravity = engine.createComponent(GravityComponent.class);
        BulletComponent bullet = engine.createComponent(BulletComponent.class);
        MovementComponent movement = engine.createComponent(MovementComponent.class);
        DamageComponent damage = engine.createComponent(DamageComponent.class);


        bullet.shadow = shadow;
        gravity.vertical = 9.81f;
        gravity.scale = 0;
        expiration.removeOnFinished = true;
        expiration.start(2);

        entity.add(expiration);
        entity.add(bullet);
        entity.add(gravity);
        entity.add(movement);
        entity.add(damage);

        engine.addEntity(entity);

        return entity;
    }

    public Entity createExplosion(float x, float y) {
        Entity entity = engine.createEntity();

        SpriteComponent sprite = engine.createComponent(SpriteComponent.class);
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        SizeComponent size = engine.createComponent(SizeComponent.class);
        AnimationComponent animation = engine.createComponent(AnimationComponent.class);
        StateComponent state = engine.createComponent(StateComponent.class);
        ExpirationComponent expiration = engine.createComponent(ExpirationComponent.class);

        transform.position.set(x, y);
        animation.looping = true;
        animation.map.put(0, GameAssets.explosion);
        expiration.removeOnFinished = true;
        expiration.start(2);

        entity.add(sprite);
        entity.add(transform);
        entity.add(size);
        entity.add(animation);
        entity.add(state);
        entity.add(expiration);

        engine.addEntity(entity);

        return entity;
    }


//    public Entity createBulletShadow(float x, float y) {
//        Entity entity = engine.createEntity();
//
//        entity.flags = EntityFlags.BULLET_SHADOW;
//
//        ExpirationComponent timer = engine.createComponent(ExpirationComponent.class);
////        PhysicsComponent physics = engine.createComponent(PhysicsComponent.class);
//        SpriteComponent sprite = engine.createComponent(SpriteComponent.class);
//        TransformComponent transform = engine.createComponent(TransformComponent.class);
//        SizeComponent size = engine.createComponent(SizeComponent.class);
////        LifespanComponent lifespan = engine.createComponent(LifespanComponent.class);
//        MovementComponent movement = engine.createComponent(MovementComponent.class);
//
//
//        timer.removeOnFinished = true;
//        sprite.textureRegion = GameAssets.bullet_shadow;
//        transform.position.set(x, y);
////        transform.scale.set(2, 2);
////        physics.body = createCircleBody(x, y, 0.25f, true, Constants.CATEGORY_SENSOR, Constants.MASK_SENSOR, entity);
////        physics.body.setLinearVelocity(targetPoint.cpy().sub(x, y).nor().scl(speed));
//
//        entity.add(timer);
//        entity.add(sprite);
//        entity.add(size);
////        entity.add(lifespan);
//        entity.add(movement);
//        entity.add(transform);
//
//        engine.addEntity(entity);
//
//        return entity;
//    }

    public Entity createCoinLarge(float x, float y) {
        Entity entity = engine.createEntity();

        SpriteComponent sprite = engine.createComponent(SpriteComponent.class);
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        SizeComponent size = engine.createComponent(SizeComponent.class);
        AnimationComponent animation = engine.createComponent(AnimationComponent.class);
        StateComponent state = engine.createComponent(StateComponent.class);

//        sprite.textureRegion = GameAssets.coin_large.getKeyFrame(0);
        sprite.zIndex = -1;
        transform.position.set(x, y);
        animation.looping = true;
        animation.map.put(0, GameAssets.coin_large);

        entity.add(sprite);
        entity.add(transform);
        entity.add(size);
        entity.add(animation);
        entity.add(state);

        engine.addEntity(entity);

        return entity;
    }

    public Entity createStar(float x, float y) {
        Entity entity = engine.createEntity();

        SpriteComponent sprite = engine.createComponent(SpriteComponent.class);
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        SizeComponent size = engine.createComponent(SizeComponent.class);
        AnimationComponent animation = engine.createComponent(AnimationComponent.class);
        StateComponent state = engine.createComponent(StateComponent.class);

//        sprite.textureRegion = GameAssets.star.getKeyFrame(0);
        sprite.zIndex = -1;
        transform.position.set(x, y);
        animation.looping = true;
        animation.map.put(0, GameAssets.star);

        entity.add(sprite);
        entity.add(transform);
        entity.add(size);
        entity.add(animation);
        entity.add(state);

        engine.addEntity(entity);

        return entity;
    }


    public Entity createSprite(TextureRegion textureRegion, float x, float y) {
        Entity entity = engine.createEntity();

        SpriteComponent sprite = engine.createComponent(SpriteComponent.class);
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        SizeComponent size = engine.createComponent(SizeComponent.class);
//        AlignmentComponent alignment = engine.createComponent(AlignmentComponent.class);


        sprite.textureRegion = textureRegion;
        transform.position.set(x, y);
//        alignment.set(align);

        entity.add(sprite);
        entity.add(transform);
        entity.add(size);
//        entity.add(alignment);

        return entity;
    }

    public void update() {
        for(Entity entity: entityToDestroy) {
            engine.removeEntity(entity);
        }
        entityToDestroy.clear();
    }

}
