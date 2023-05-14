package me.feusalamander.betterdungeons;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.*;
import org.bukkit.entity.Player;

import javax.print.PrintException;
import java.io.*;
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
        double loc = 992;
        while (main.getUsedLocations().contains(loc)){
            loc = loc + 992;
        }
        main.getUsedLocations().add(loc);
        spawn = new Location(world, 0, 50, loc);
        createRooms();
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
        //destroy the map
    }
    private void createRooms(){
        useSchematic();
    }
    private void useSchematic(){
        File spawnSchematic = new File(main.getDataFolder(), "spawn.schem");
        ClipboardFormat format = ClipboardFormats.findByFile(spawnSchematic);
        assert format != null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(spawnSchematic))) {
            Clipboard clipboard = reader.read();
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), 999999999)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(spawn.getX(), spawn.getY(), spawn.getZ()))
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
