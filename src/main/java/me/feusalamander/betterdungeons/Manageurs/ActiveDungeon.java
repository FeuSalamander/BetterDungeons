package me.feusalamander.betterdungeons.Manageurs;

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
import me.feusalamander.betterdungeons.BetterDungeons;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ActiveDungeon {
    BetterDungeons main = BetterDungeons.main;
    private double dungeonLoc = 992;
    private Location playerSpawn;
    private final List<Player> players;
    private final Floor floor;
    private final World world = BetterDungeons.main.getWorld();
    private EditSession editSession;
    private int editCount = 0;
    private Room start;
    private Room end;
    private final Random rand = new Random();
    private Room[][] matrix;
    private Location lastLoc;

    public ActiveDungeon(List<Player> players, Floor floor){
        this.players = players;
        this.floor = floor;
        createDungeon();
    }
    private void createDungeon(){
        main.getActivedungeons().add(this);
        while (main.getUsedLocations().contains(dungeonLoc)){
            dungeonLoc = dungeonLoc + 992;
        }
        main.getUsedLocations().add(dungeonLoc);
        lastLoc = new Location(world, 0, 50, dungeonLoc);
        editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(world));
        createRooms();
        editSession.close();
        for(Player p : players){
            p.teleport(playerSpawn);
            Bukkit.broadcastMessage(playerSpawn.getX()+" "+ playerSpawn.getZ());
        }
    }
    public void unload(){
        Location loc = BetterDungeons.config.getLobby();
        for(Player p : players){
            p.teleport(loc);
        }
        main.getUsedLocations().remove(dungeonLoc);
        for(int i = 1; i<= editCount; i++){
            editSession.undo(editSession);
        }

    }
    private void roomQuery(){
        List<Room> rooms = new ArrayList<>();
        start = null;
        end = null;
        for(Integer id : floor.getRooms()){
            for(Room room : main.getLoadedrooms()){
                if(room.getId().equalsIgnoreCase(String.valueOf(id))&&room.isActivated()){
                    if(room.getType().equalsIgnoreCase("start")){
                        start = room;
                    }else if(room.getType().equalsIgnoreCase("end")){
                        end = room;
                    }else {
                        rooms.add(room);
                    }
                    break;
                }
            }
        }
        if(start == null){
            unload();
            for(Player p : this.players){
                p.sendMessage("§4The dungeon needs to have a valid start/end room");
            }
            return;
        }
    }
    private void createRooms(){
        roomQuery();
        int size = floor.getSize();
        matrix = new Room[size][size];
        boolean cl = rand.nextBoolean();
        int rand1 = rand.nextInt(2);
        if(cl){matrix[rand1*(size-1)][rand.nextInt(size)] = start;}else{matrix[rand.nextInt(size)][rand1*(size-1)] = start;}
        build();
    }
    private void build(){
        for(Room[] line : matrix){
            for(Room box : line){
                if(box == null){
                    useSchematic(lastLoc, "rooms/null.schematic");
                }else {
                    if(box.getType().equalsIgnoreCase("start"))playerSpawn = new Location(world, lastLoc.getX(), 50, lastLoc.getZ());
                    useSchematic(lastLoc, box.getPath());
                }
                if(lastLoc.getX() < (floor.getSize()-1)*32){
                    lastLoc.set(lastLoc.getX()+32, 50, lastLoc.getZ());
                }else{
                    lastLoc.set(0, 50, lastLoc.getZ()+32);
                }
            }
        }
    }
    private void useSchematic(Location loc, String path){
        File schematic = new File(main.getDataFolder(), path);
        if(!schematic.exists())schematic = new File(main.getDataFolder(), "rooms/null.schematic");
        ClipboardFormat format = ClipboardFormats.findByFile(schematic);
        assert format != null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(schematic))) {
            Clipboard clipboard = reader.read();
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()))
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
            editCount++;
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
