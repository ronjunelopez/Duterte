package com.ronscript.duterte.ai.fsm;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

/**
 * Copyright (C) 2016 Duterte on 9/3/2016
 * by Ron
 */
public enum PoliceState implements State<Police> {
    FOLLOW_LEADER() {
        @Override
        public void update(Police police) {
//            police.steer.setBehavior(police.steer.followLeader);
//            if(police.com.target != null) {
//                police.fsm.changeState(SEARCH_FOR_CRIMINAL);
//            }
        }
    },
    SEARCH_FOR_CRIMINAL() {
        @Override
        public void update(Police police) {
//            police.steer.setBehavior(police.steer.wander);
//            if(police.com.target != null) {
//                police.fsm.changeState(CHASE_AND_KILL_TARGET);
//            }
        }
    },
    CHASE_AND_KILL_TARGET() {
        @Override
        public void update(Police police) {

//            if(police.com.target == null) {
//                Gdx.app.log("", "no target");
//                police.fsm.changeState(SEARCH_FOR_CRIMINAL);
//            } else {
//                police.steer.pursue(police.getTarget());
//                police.lookAtTarget();
//            }
        }
    }
    ;

    @Override
    public void enter(Police police) {

    }

    @Override
    public void update(Police police) {

    }

    @Override
    public void exit(Police police) {

    }

    @Override
    public boolean onMessage(Police police, Telegram telegram) {

        return false;
    }
}
