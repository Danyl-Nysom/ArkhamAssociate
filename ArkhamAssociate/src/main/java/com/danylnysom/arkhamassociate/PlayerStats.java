package com.danylnysom.arkhamassociate;

/**
 * Created by Dylan on 18/11/13.
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
    public static final int SKILL_MASK = 0b111;
    public static final int FOCUS_MASK = 0b11;
}
