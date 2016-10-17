package com.ronscript.duterte.components.game.objects;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.ronscript.duterte.utils.EntityManager;

/**
 * @author Ron
 * @since 8/9/2016
 */
public class WeaponComponent implements Component {

    public WeaponType type = WeaponType.MELEE;
    public EntityManager factory;

    public float aoe; // 0.5 for melee, more than 0.5 for other weapon
    public float aoeAngleCovered;
    public float aoeAngle;
//    public Ammo[] // 0 for melee, 1 or more for other weapon
    public boolean use = false;
    public Vector2 target = new Vector2();
    public Entity hit;
}
