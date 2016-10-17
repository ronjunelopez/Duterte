package com.ronscript.duterte.utils;

import com.badlogic.ashley.core.ComponentMapper;
import com.ronscript.duterte.components.GroupComponent;
import com.ronscript.duterte.components.ai.fsm.StateMachineComponent;
import com.ronscript.duterte.components.ai.msg.MessagingComponent;
import com.ronscript.duterte.components.ai.steer.BehaviorComponent;
import com.ronscript.duterte.components.ai.steer.SteeringComponent;
import com.ronscript.duterte.components.game.objects.BulletComponent;
import com.ronscript.duterte.components.game.objects.ExpirationComponent;
import com.ronscript.duterte.components.game.objects.HealthBarComponent;
import com.ronscript.duterte.components.game.objects.InventoryComponent;
import com.ronscript.duterte.components.game.objects.LifespanComponent;
import com.ronscript.duterte.components.game.objects.WeaponComponent;
import com.ronscript.duterte.components.game.objects.character.CharacterComponent;
import com.ronscript.duterte.components.game.objects.character.CriminalComponent;
import com.ronscript.duterte.components.game.objects.character.PoliceComponent;
import com.ronscript.duterte.components.game.objects.character.VisionComponent;
import com.ronscript.duterte.components.game.objects.character.pc.PlayerComponent;
import com.ronscript.duterte.components.game.objects.character.stats.CombatComponent;
import com.ronscript.duterte.components.game.objects.character.stats.DamageComponent;
import com.ronscript.duterte.components.game.objects.character.stats.HealthComponent;
import com.ronscript.duterte.components.game.objects.character.stats.RegenerationComponent;
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
import com.ronscript.duterte.components.properties.FixedRotationComponent;
import com.ronscript.duterte.components.properties.SizeComponent;
import com.ronscript.duterte.components.properties.TransformComponent;

/**
 * @author Ron
 * @since 8/8/2016
 */
public class Mapper {

    // Properties
    public static ComponentMapper<SpriteComponent> sprite = ComponentMapper.getFor(SpriteComponent.class);
    public static ComponentMapper<TransformComponent> transform = ComponentMapper.getFor(TransformComponent.class);
    public static ComponentMapper<SizeComponent> size = ComponentMapper.getFor(SizeComponent.class);
    public static ComponentMapper<FixedRotationComponent> fixedRotation = ComponentMapper.getFor(FixedRotationComponent.class);
    // Physics
    public static ComponentMapper<PhysicsComponent> physics = ComponentMapper.getFor(PhysicsComponent.class);
    public static ComponentMapper<CollisionComponent> collision = ComponentMapper.getFor(CollisionComponent.class);
    // States & Animations
    public static ComponentMapper<AnimationComponent> animation = ComponentMapper.getFor(AnimationComponent.class);
    public static ComponentMapper<StateComponent> state = ComponentMapper.getFor(StateComponent.class);
    // Gravity
    public static ComponentMapper<GravityComponent> gravity = ComponentMapper.getFor(GravityComponent.class);
    // Motion
    public static ComponentMapper<MovementComponent> movement = ComponentMapper.getFor(MovementComponent.class);
    // Group
    public static ComponentMapper<AttachmentComponent> attachment = ComponentMapper.getFor(AttachmentComponent.class);
    public static ComponentMapper<GroupComponent> group = ComponentMapper.getFor(GroupComponent.class);
    public static ComponentMapper<AlignmentComponent> alignment = ComponentMapper.getFor(AlignmentComponent.class);
    // Input & User controls
    public static ComponentMapper<PlayerComponent> player = ComponentMapper.getFor(PlayerComponent.class);
    public static ComponentMapper<InputComponent> input = ComponentMapper.getFor(InputComponent.class);

    // Movement AI
    public static ComponentMapper<SteeringComponent> steering = ComponentMapper.getFor(SteeringComponent.class);
    public static ComponentMapper<BehaviorComponent> behavior = ComponentMapper.getFor(BehaviorComponent.class);

    public static ComponentMapper<StateMachineComponent> stateMachine = ComponentMapper.getFor(StateMachineComponent.class);
    public static ComponentMapper<MessagingComponent> messaging = ComponentMapper.getFor(MessagingComponent.class);

    // Game object components

    public static ComponentMapper<InventoryComponent> inventory = ComponentMapper.getFor(InventoryComponent.class);
    public static ComponentMapper<CharacterComponent> character = ComponentMapper.getFor(CharacterComponent.class);
    public static ComponentMapper<CriminalComponent> enemy = ComponentMapper.getFor(CriminalComponent.class);
    public static ComponentMapper<PoliceComponent> police = ComponentMapper.getFor(PoliceComponent.class);
    public static ComponentMapper<ExpirationComponent> timer = ComponentMapper.getFor(ExpirationComponent.class);
    public static ComponentMapper<BulletComponent> bullet = ComponentMapper.getFor(BulletComponent.class);
    public static ComponentMapper<LifespanComponent> lifespan = ComponentMapper.getFor(LifespanComponent.class);
    public static ComponentMapper<WeaponComponent> weapon = ComponentMapper.getFor(WeaponComponent.class);
    public static ComponentMapper<CombatComponent> combat = ComponentMapper.getFor(CombatComponent.class);

    public static ComponentMapper<VisionComponent> vision = ComponentMapper.getFor(VisionComponent.class);

    // Character stats & effects
    public static ComponentMapper<HealthComponent> health = ComponentMapper.getFor(HealthComponent.class);
    public static ComponentMapper<DamageComponent> damage = ComponentMapper.getFor(DamageComponent.class);
    public static ComponentMapper<RegenerationComponent> regeneration = ComponentMapper.getFor(RegenerationComponent.class);

    // GUI
    public static ComponentMapper<HealthBarComponent> healthBar = ComponentMapper.getFor(HealthBarComponent.class);
}
