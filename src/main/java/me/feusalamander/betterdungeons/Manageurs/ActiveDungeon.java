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
    private List<Room> rooms = new ArrayList<>();
    private Room[][] matrix;
    private Location lastLoc;
    private int spawnX;
    private int spawnY;

    public ActiveDungeon(List<Player> players, Floor floor){
        this.players = players;
        this.floor = floor;
        createDungeon();
    }
    private void createDungeon(){
        main.getActivedungeons().add(this);
        while (main.getUsedLocations().contains(dungeonLoc)){
            dungeonLoc += 992;
        }
        main.getUsedLocations().add(dungeonLoc);
        lastLoc = new Location(world, 0, 50, dungeonLoc);
        editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(world));
        createRooms();
        editSession.close();
        for(Player p : players){
            p.teleport(playerSpawn);
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
        if(cl){spawnX = rand1*(size-1);spawnY = rand.nextInt(size);}else{spawnY = rand1*(size-1);spawnX = rand.nextInt(size);}
        matrix[spawnX][spawnY] = start;
        Bukkit.broadcastMessage("spawn at: "+spawnX+ " " + spawnY);
        createPath();
        build();
    }
    private void createPath(){
        int pathLength = (int)(floor.getSize()*1.5);
        int curX = spawnX;
        int curY = spawnY;
        while(pathLength>-1){
            Room room = rooms.get(rand.nextInt(rooms.size()));
            boolean negative = rand.nextBoolean();
            boolean XorY = rand.nextBoolean();
            int nextCord;
            if(negative){
                nextCord = -1;
            }else{
                nextCord = 1;
            }
            if(XorY){
                if(curX+ nextCord < floor.getSize()&&curX+ nextCord >= 0){
                    curX += nextCord;
                }
            }else {
                if(curY+ nextCord < floor.getSize()&&curY+ nextCord >= 0){
                    curY += nextCord;
                }
            }
            if(matrix[curX][curY] == null){
                Bukkit.broadcastMessage(room.getName()+" at: "+curX+ " " + curY);
                matrix[curX][curY]= room;
                pathLength--;
            }
        }
    }
    private void build(){
        for(Room[] colon : matrix){
            for(Room box : colon){
                if(box == null){
                    useSchematic(lastLoc, "rooms/null.schem");
                }else {
                    if(box.getType().equalsIgnoreCase("start"))playerSpawn = new Location(world, lastLoc.getX(), 53, lastLoc.getZ());
                    useSchematic(lastLoc, box.getPath());
                }
                if(lastLoc.getZ() < ((floor.getSize()-1)*32)+dungeonLoc){
                    lastLoc.set(lastLoc.getX(), 50, lastLoc.getZ()+32);
                }else{
                    lastLoc.set(lastLoc.getX()+32, 50, dungeonLoc);
                }
            }
        }
    }
    private void useSchematic(Location loc, String path){
        File schematic = new File(main.getDataFolder(), path);
        if(!schematic.exists())schematic = new File(main.getDataFolder(), "rooms/null.schem");
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
