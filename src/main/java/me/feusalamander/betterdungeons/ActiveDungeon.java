package me.feusalamander.betterdungeons;

import org.bukkit.entity.Player;

import java.util.List;

public class ActiveDungeon {
    private final List<Player> players;
    private final Floor floor;
    public ActiveDungeon(List<Player> players, Floor floor){
        this.players = players;
        this.floor = floor;
    }
    public List<Player> getPlayers() {
        return players;
    }
    public Floor getFloor() {
        return floor;
    }
}
