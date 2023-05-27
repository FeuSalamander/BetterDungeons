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
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.feusalamander.betterdungeons.BetterDungeons;
import me.feusalamander.betterdungeons.DirectionEnum;
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
    private final List<Room> rooms = new ArrayList<>();
    private ActiveRoom[][] matrix;
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
        matrix = new ActiveRoom[size][size];
        boolean cl = rand.nextBoolean();
        int rand1 = rand.nextInt(2);
        int rotation = 0;
        if(cl){spawnX = rand1*(size-1);spawnY = rand.nextInt(size);if(spawnX == 0){rotation = -90;} else {rotation = 90;}} else{spawnY = rand1*(size-1);if(spawnY == 0) rotation = 180;spawnX = rand.nextInt(size);}
        addActiveRoom(spawnX, spawnY, start, rotation, 1);
        createPath();
        build();
    }
    private void createPath() {
        List<int[]> roomList = getDoors(spawnX, spawnY);
        List<int[]> roomList2 = new ArrayList<>(roomList);
        int crash = 0;
        boolean enabled = true;
        while (enabled && crash <= 30) {
            for (int[] coord : roomList) {
                addSizedRoom(coord[0], coord[1]);
            }

            roomList.clear();
            for (int[] r : roomList2) {
                roomList.addAll(getDoors(r[0], r[1]));
            }

            roomList2.clear();
            roomList2.addAll(roomList);

            for (int[] coord : roomList) {
                addSizedRoom(coord[0], coord[1]);
            }

            enabled = false;
            for (ActiveRoom[] line : matrix) {
                for (ActiveRoom casSe : line) {
                    if (casSe == null) {
                        enabled = true;
                        break;
                    }
                }
            }

            crash++;
        }
    }
    private void addSizedRoom(int X, int Y) {
        List<Room> roomList = new ArrayList<>(rooms);
        Room room = null;
        boolean possible = false;
        int multiplication = 0;

        while (!possible) {
            multiplication = 1;
            room = roomList.get(rand.nextInt(roomList.size()));
            if (room.getSizeX() == 1 && room.getSizeY() == 1) {
                addActiveRoom(X, Y, room, 0, 1);
                return;
            }
            possible = true;

            for (int i = 0; i < room.getSizeX(); i++) {
                for (int i2 = 0; i2 < room.getSizeY(); i2++) {
                    if (!isValidPosition(X + i, Y + i2)) {
                        possible = false;
                        break;
                    }
                    if (matrix[X + i][Y + i2] != null) {
                        possible = false;
                        break;
                    }
                }
                if (!possible) {
                    break;
                }
            }

            if (!possible) {
                multiplication = -1;
                possible = true;

                for (int i = 0; i < room.getSizeX(); i++) {
                    for (int i2 = 0; i2 < room.getSizeY(); i2++) {
                        if (!isValidPosition(X - i, Y - i2)) {
                            possible = false;
                            break;
                        }
                        if (matrix[X - i][Y - i2] != null) {
                            possible = false;
                            break;
                        }
                    }
                    if (!possible) {
                        break;
                    }
                }
            }

            rooms.remove(room);
        }
        addActiveRoom(X, Y, room, 0, multiplication);
        for (int i = 0; i < room.getSizeX(); i++) {
            for (int i2 = 0; i2 < room.getSizeY(); i2++) {
                int newX = X + (multiplication * i);
                int newY = Y + (multiplication * i2);
                if (matrix[newX][newY] == null) {
                    addActiveRoom(newX, newY, main.getPlaceholderRoom(), 0, 1);
                }
            }
        }
    }
    private void build(){
        for(ActiveRoom[] colon : matrix){
            for(ActiveRoom box : colon){
                if(box == null){
                    useSchematic(lastLoc, "rooms/null.schem", 0);
                }else if (!box.getRoom().equals(main.getPlaceholderRoom())){
                    if(box.getRoom().getType().equalsIgnoreCase("start")){
                        playerSpawn = new Location(world, lastLoc.getX(), 53, lastLoc.getZ(), 180-box.getRotation(), 0);
                    }
                    if(box.getRoom().getSizeX()>1||box.getRoom().getSizeY()>1){
                        Location newLoc = lastLoc.clone();
                        newLoc.add(box.getModifiedX(), 0, box.getModifiedY());
                        useSchematic(newLoc, box.getRoom().getPath(), box.getRotation());
                    }else {
                        useSchematic(lastLoc, box.getRoom().getPath(), box.getRotation());
                    }
                }
                if(lastLoc.getZ() < ((floor.getSize()-1)*32)+dungeonLoc){
                    lastLoc.set(lastLoc.getX(), 50, lastLoc.getZ()+32);
                }else{
                    lastLoc.set(lastLoc.getX()+32, 50, dungeonLoc);
                }
            }
        }
    }
    private void useSchematic(Location loc, String path, int rotation){
        File schematic = new File(main.getDataFolder(), path);
        if(!schematic.exists())schematic = new File(main.getDataFolder(), "rooms/null.schem");
        ClipboardFormat format = ClipboardFormats.findByFile(schematic);
        assert format != null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(schematic))) {
            Clipboard clipboard = reader.read();
            ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
            AffineTransform transform = new AffineTransform().rotateY(rotation);
            clipboardHolder.setTransform(transform);
            Operation operation = clipboardHolder.createPaste(editSession)
                    .to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()))
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
            editCount++;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private boolean isValidPosition(int X, int Y) {
        return X >= 0 && X < floor.getSize() && Y >= 0 && Y < floor.getSize();
    }
    private void addActiveRoom(int X, int Y, Room room, int rotation, int ratio){
        ActiveRoom activeRoom = new ActiveRoom(X, Y, room, rotation, ratio);
        matrix[X][Y] = activeRoom;
    }
    private List<int[]> getDoors(int x,int y){
        ActiveRoom room = matrix[x][y];
        List<int[]> accessibleCoordinates = new ArrayList<>();
        if (room.isAccessible(DirectionEnum.NORTH)) {
            if(isValidPosition(x, y-1)){
                accessibleCoordinates.add(new int[]{x, y-1});
            }
        }
        if (room.isAccessible(DirectionEnum.SOUTH)) {
            if(isValidPosition(x, y+1)){
                accessibleCoordinates.add(new int[]{x, y+1});
            }
        }
        if (room.isAccessible(DirectionEnum.EAST)) {
            if(isValidPosition(x+1, y)){
                accessibleCoordinates.add(new int[]{x+1, y});
            }
        }
        if (room.isAccessible(DirectionEnum.WEST)) {
            if(isValidPosition(x-1, y)){
                accessibleCoordinates.add(new int[]{x-1, y});
            }
        }
        return accessibleCoordinates;
    }
    public List<Player> getPlayers() {
        return players;
    }
    public Floor getFloor() {
        return floor;
    }
    //private void createPath() {
    //        int pathLength = (floor.getSize() * floor.getSize()) / 2;
    //        int curX = spawnX;
    //        int curY = spawnY;
    //        int newX = spawnX;
    //        int newY = spawnY;
    //        int crash = 0;
    //
    //        while (pathLength > 0) {
    //            if (crash > 20) {
    //                break;
    //            }
    //
    //            boolean negative = rand.nextBoolean();
    //            boolean XorY = rand.nextBoolean();
    //            int nextCord = negative ? -1 : 1;
    //
    //            if (XorY && isValidPosition(curX + nextCord, curY)) {
    //                newX = curX + nextCord;
    //            } else if (!XorY && isValidPosition(curX, curY + nextCord)) {
    //                newY = curY + nextCord;
    //            }
    //
    //            if (matrix[newX][newY] == null) {
    //                curX = newX;
    //                curY = newY;
    //                addSizedRoom(newX, newY);
    //                pathLength--;
    //                crash = 0;
    //            } else {
    //                newX = curX;
    //                newY = curY;
    //                crash++;
    //            }
    //        }
    //    }
}
