package com.ronscript.duterte.utils;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.ronscript.duterte.GameInputProcessor;
import com.ronscript.duterte.systems.GroupSystem;
import com.ronscript.duterte.systems.InputSystem;
import com.ronscript.duterte.systems.PlayerSystem;
import com.ronscript.duterte.systems.ai.AutonomousSystem;
import com.ronscript.duterte.systems.ai.SteeringSystem;
import com.ronscript.duterte.systems.game.objects.BulletSystem;
import com.ronscript.duterte.systems.game.objects.CharacterSystem;
import com.ronscript.duterte.systems.game.objects.CriminalSystem;
import com.ronscript.duterte.systems.game.objects.DamageSystem;
import com.ronscript.duterte.systems.game.objects.ExpirationSystem;
import com.ronscript.duterte.systems.game.objects.HealthBarSystem;
import com.ronscript.duterte.systems.game.objects.HealthSystem;
import com.ronscript.duterte.systems.game.objects.PoliceSystem;
import com.ronscript.duterte.systems.game.objects.VisionSystem;
import com.ronscript.duterte.systems.game.objects.WeaponSystem;
import com.ronscript.duterte.systems.graphics.AnimationSystem;
import com.ronscript.duterte.systems.graphics.PhysicsDebugSystem;
import com.ronscript.duterte.systems.graphics.RenderingSystem;
import com.ronscript.duterte.systems.physics.CollisionSystem;
import com.ronscript.duterte.systems.physics.GravitySystem;
import com.ronscript.duterte.systems.physics.MovementSystem;
import com.ronscript.duterte.systems.physics.PhysicsSystem;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class SystemManager {
    private static SystemManager ourInstance = new SystemManager();
    private PooledEngine engine;
    private World world;
    private Batch batch;
    private Camera camera;
    private GameInputProcessor processor;
    private SystemManager() {
    }

    public static SystemManager getInstance() {
        return ourInstance;
    }

    public void init(PooledEngine engine, World world, SpriteBatch batch, Camera camera, GameInputProcessor processor) {
        this.engine = engine;
        this.world = world;
        this.batch = batch;
        this.camera = camera;
        this.processor = processor;
    }

    public PooledEngine getEngine() {
        return engine;
    }

    public void create() {
        // Game core system
        engine.addSystem(new GravitySystem());
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new InputSystem(processor));
        engine.addSystem(new PlayerSystem());


        engine.addSystem(new CollisionSystem());

        // Vision
        engine.addSystem(new VisionSystem(Logger.NONE));
        // Game object systems
        engine.addSystem(new HealthSystem());
        engine.addSystem(new DamageSystem());
        engine.addSystem(new HealthBarSystem());
        engine.addSystem(new BulletSystem());
        engine.addSystem(new ExpirationSystem());
        engine.addSystem(new WeaponSystem());
        engine.addSystem(new CharacterSystem());
        engine.addSystem(new PoliceSystem());
        engine.addSystem(new CriminalSystem());

        // AI systems
        engine.addSystem(new SteeringSystem());
        engine.addSystem(new AutonomousSystem());

        // Physics
        engine.addSystem(new PhysicsSystem(world));

        // Graphics
        engine.addSystem(new GroupSystem());
        engine.addSystem(new RenderingSystem(batch, camera));
        engine.addSystem(new PhysicsDebugSystem(world, camera));


    }

}
