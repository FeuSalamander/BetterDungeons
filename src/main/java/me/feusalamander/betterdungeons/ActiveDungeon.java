package me.feusalamander.betterdungeons;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.List;

public class ActiveDungeon {
    BetterDungeons main = BetterDungeons.main;
    private final List<Player> players;
    private final Floor floor;
    private final World world = BetterDungeons.main.getWorld();
    private Location spawn;

    public ActiveDungeon(List<Player> players, Floor floor){
        this.players = players;
        this.floor = floor;
        createDungeon();
    }
    private void createDungeon(){
        main.getActivedungeons().add(this);
        double loc = 1000;
        while (main.getUsedLocations().contains(loc)){
            loc = loc + 1000;
        }
        main.getUsedLocations().add(loc);
        spawn = new Location(world, 0, 50, loc);
        for(Player p : players){
            p.teleport(spawn);
        }
    }
    public void unload(){
        Location loc = BetterDungeons.config.getLobby();
        for(Player p : players){
            p.teleport(loc);
        }
        main.getUsedLocations().remove(spawn.getZ());
        Chunk chunk = spawn.getChunk();
        //destroy the map
    }
    public List<Player> getPlayers() {
        return players;
    }
    public Floor getFloor() {
        return floor;
    }
    public World getWorld(){
        return world;
    }
}
