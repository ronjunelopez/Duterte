package com.ronscript.duterte.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ronscript.duterte.GameInputProcessor;
import com.ronscript.duterte.components.inputs.InputComponent;
import com.ronscript.duterte.utils.Mapper;

/**
 * @author Ron
 * @since 8/9/2016
 */
public class InputSystem extends IteratingSystem  {

    GameInputProcessor processor;

    public InputSystem(GameInputProcessor processor) {
        super(Family.all(InputComponent.class).get());
        this.processor = processor;
    }

    public void setProcesssor(GameInputProcessor processor) {
        this.processor = processor;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if(processor == null) {
            return;
        }

        InputComponent input = Mapper.input.get(entity);
        input.isTouched = processor.isTouched();
        input.screenPoint.set(processor.getScreenPoint());
    }
}
