package com.danylnysom.arkhamassociate.etc;

import java.util.Random;

/**
 * Created by Dylan on 16/12/13.
 */
public class Examples {
    private static final String[] PLAYER_NAMES = {
            "Bob",
            "Dr. Jekyll",
            "Arnold",
            "Patricia",
            "Commander Jenkins",
    };

    private static final String[] GAME_NAMES = {
            "Friday Night Fun",
            "Cthulhu shall fall!",
            "New Years' Party",
            "a game",
            "THIS IS NOT A GAME",
            "This time we'll win!"
    };

    private static final Random random = new Random();

    public static String playerName() {
        return PLAYER_NAMES[random.nextInt(PLAYER_NAMES.length)];
    }

    public static String gameName() {
        return GAME_NAMES[random.nextInt(GAME_NAMES.length)];
    }
}
