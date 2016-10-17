package com.ronscript.duterte.components.game.objects.character;

import com.badlogic.ashley.core.Entity;
import com.ronscript.duterte.utils.EntityManager;

/**
 * Copyright (C) 2016 Duterte on 8/27/2016
 * by Ron
 */
public class CharacterFactory {

    public static Entity create(EntityManager manager, CharacterType characters, float x, float y) {
        Entity character = null;
        switch (characters) {
            case PLAYER:
                character = manager.createPlayerCharacter(x, y);
                break;
            case NON_PLAYER:
        }
        return character;
    }

    enum CharacterType {
        PLAYER,         // can be controlled
        NON_PLAYER,     // is a autonomous, have agent behavior, cannot be hurt and cannot be controlled
        PLAYER_ENEMY,   // is a autonomous, have agent behavior, can be hurt by playerCharacter and police, attack only playerCharacter
        PLAYER_ALLIED   // is a autonomous, have agent behavior, can be hurt by enemy, attack only enemy
    }

    enum Characters {
        CIVILIAN,
            // [mood] = scared, peace, nervous
            // is an police by default
            // can accept drug offers
            // can turn into drug user
            // can ignore drug offers
            // slow flee after ignoring and change mood to nervous
            // fast flee after ignoring and change mood to scared
            // can talk to police in range
        DRUG_PUSHER,
            // [mood] = scared, peace, nervous, aggressive
            // is an playerCharacter enemy by default
            // can be turn into police
            // can flee
            // can surrender
            // can fight
            // drugs merchant
            // purse civilian
        DRUG_USER,
            // is an playerCharacter enemy by default
            // can flee
            // can surrender
            // can fight
            // can be turn into police by surrendering
            // can be shoot if mode is fighting
            // seeking for any drugs nearby
            // hide to police
        POLICE,
            // [mood] = aggressive, peace
            // is an playerCharacter police by default
            // can be turn into civilian, drug user or pusher
            // pursue drug user and kill or busted,
        VIGILANTE,
            // mood = aggressive, peace
            // is an enemy
            // kill criminals

        RIDING_IN_TANDEM,
            // is an enemy
            // rides a jeep or motorcycle
            // randomly attack
    }
}
