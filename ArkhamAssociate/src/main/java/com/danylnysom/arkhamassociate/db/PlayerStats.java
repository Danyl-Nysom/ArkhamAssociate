package com.danylnysom.arkhamassociate.db;

/**
 * Constants relating to the storage of individual player stats in a single integer
 */
public interface PlayerStats {

    public static final int SANITY_SHIFT = 0;
    public static final int STAMINA_SHIFT = SANITY_SHIFT + 3;
    public static final int SPEED_SHIFT = STAMINA_SHIFT + 3;
    public static final int SNEAK_SHIFT = SPEED_SHIFT + 3;
    public static final int FIGHT_SHIFT = SNEAK_SHIFT + 3;
    public static final int WILL_SHIFT = FIGHT_SHIFT + 3;
    public static final int LORE_SHIFT = WILL_SHIFT + 3;
    public static final int LUCK_SHIFT = LORE_SHIFT + 3;
    public static final int FOCUS_SHIFT = LUCK_SHIFT + 3;

    public static final int STAT_MASK = 0b111;
}
