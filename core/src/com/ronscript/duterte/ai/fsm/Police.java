package com.ronscript.duterte.ai.fsm;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.ronscript.duterte.ai.steer.SteeringAgent;
import com.ronscript.duterte.components.game.objects.character.PoliceComponent;
import com.ronscript.duterte.utils.Mapper;

/**
 * Copyright (C) 2016 Duterte on 9/3/2016
 * by Ron
 */
public class Police implements Telegraph {

    public PoliceComponent com;
    public StateMachine fsm;
    public SteeringAgent steer;
    public Entity entity;

    Vector2 toTarget = new Vector2();

    public Police(PoliceComponent com, StateMachine fsm, SteeringAgent steer, Entity entity ) {
        this.com = com;
        this.fsm = fsm;
        this.steer = steer;
        this.fsm.setInitialState(PoliceState.SEARCH_FOR_CRIMINAL);
        this.entity = entity;
    }

    public SteeringAgent getTarget() {
        return Mapper.steering.get(com.target).agent;
    }

    public void lookAtTarget() {
        steer.getBehavior().setEnabled(false);
        steer.getBody().setLinearVelocity(0,0);
        Vector2 policePos = Mapper.transform.get(entity).position;
        Vector2 criminalPos = Mapper.transform.get(com.target).position;
        Mapper.vision.get(entity).facingAngle = toTarget.set(criminalPos).sub(policePos).nor().angle();
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return fsm.handleMessage(msg);
    }


}
