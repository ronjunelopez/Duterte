package com.ronscript.duterte.components.ai.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;

/**
 * Copyright (C) 2016 Duterte on 9/4/2016
 * by Ron
 */
public class StateMachineComponent<E, S extends State<E>> implements Component {

    public StateMachine<E, S> fsm = new DefaultStateMachine<E, S>();

}
