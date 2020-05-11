package com.aionemu.gameserver.utils.stats.enums;

/**
 * @author ATracer
 */
public enum AGILITY {

    WARRIOR(100),
    GLADIATOR(100),
    TEMPLAR(110),
    SCOUT(100),
    ASSASSIN(100),
    RANGER(100),
    MAGE(95),
    SORCERER(100),
    SPIRIT_MASTER(100),
    PRIEST(100),
    CLERIC(90),
    CHANTER(90),
    ENGINEER(110),
    RIDER(100),
    GUNNER(110),
    ARTIST(100),
    BARD(100);
    private int value;

    private AGILITY(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
