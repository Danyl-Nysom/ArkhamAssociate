package com.danylnysom.arkhamassociate;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Dylan on 15/11/13.
 */
public class Game {
    private String name;
    private Date creation;
    private ArrayList<Player> players;

    public Game(String name) {
        this.name = name;
        this.creation = new Date();
        this.players = new ArrayList<Player>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Player> getPlayers() {
        players = new ArrayList<Player>();
        return players;
    }

    public Date getCreationTime() {
        return creation;
    }
}
