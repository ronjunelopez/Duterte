package com.ronscript.duterte.components.ai.steer;

import com.badlogic.ashley.core.Component;
import com.ronscript.duterte.ai.steer.SteeringAgent;

/**
 * Copyright (C) 2016 Duterte on 8/22/2016
 * by Ron
 */
public class SteeringComponent<S extends SteeringAgent> implements Component {
    public S agent;
}
