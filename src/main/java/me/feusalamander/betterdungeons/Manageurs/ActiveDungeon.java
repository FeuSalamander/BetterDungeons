package me.feusalamander.betterdungeons.Manageurs;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import me.feusalamander.betterdungeons.BetterDungeons;
import me.feusalamander.betterdungeons.DirectionEnum;
import me.feusalamander.betterdungeons.DungeonBuild;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ActiveDungeon {
    BetterDungeons main = BetterDungeons.main;
    private final DungeonBuild build = main.getDungeonBuild();
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
                if(room.getId().equalsIgnoreCase(String.valueOf(id))){
                    if(room.isActivated()){
                        if(room.getType().equalsIgnoreCase("start")){
                            start = room;
                        }else if(room.getType().equalsIgnoreCase("end")){
                            end = room;
                        }else {
                            rooms.add(room);
                        }
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
        addActiveRoom(spawnX, spawnY, start, rotation, 1, 1, 1);
        createPath();
        build.build(this);
    }
    private void createPath() {
        List<int[]> roomList = getDoors(spawnX, spawnY, 1, 1);
        List<int[]> roomList2 = new ArrayList<>(roomList);
        int crash = 0;
        while (crash <= floor.getSize()* floor.getSize()) {
            for (int[] coord : roomList) {
                int X = coord[0];
                int Y = coord[1];
                int oldX = coord[2];
                int oldY = coord[3];
                if(matrix[X][Y] == null){
                    addSizedRoom(X, Y, oldX, oldY);
                }
            }

            roomList.clear();
            for (int[] r : roomList2) {
                ActiveRoom r2 = matrix[r[0]][r[1]];
                if(r2.getRoom() == null)return;
                roomList.addAll(getDoors(r[0], r[1], r2.getXm(), r2.getYm()));
            }
            roomList2.clear();
            roomList2.addAll(roomList);
            crash++;
        }
    }
    private void addSizedRoom(int X, int Y, int oldX, int oldY) {
        List<Room> roomList = new ArrayList<>(rooms);
        Room room;
        boolean possible = false;
        while (!possible) {
            room = roomList.get(rand.nextInt(roomList.size()));
            if (room.getSizeX() == 1 && room.getSizeY() == 1) {
                addActiveRoom(X, Y, room, 0, 1, 1, 1);
                int crash = 0;
                while (crash < 4) {
                    boolean con = true;
                    for (int[] i : getDoors(X, Y, 1, 1)) {
                        if (i[0] == oldX && i[1] == oldY) {
                            con = false;
                            break;
                        }
                    }
                    if (!con)
                        break;
                    matrix[X][Y].setRotation(matrix[X][Y].getRotation() + 90);
                    crash++;
                }
                return;
            }
            int tries = 1;
            int[] xmValues = { 1, -1, 1, -1 };
            int[] ymValues = { 1, 1, -1, -1 };
            int xm = 1;
            int ym = 1;
            while (tries < 5) {
                possible = true;
                xm = xmValues[tries - 1];
                ym = ymValues[tries - 1];
                for (int x = 0; x < room.getSizeX(); x++) {
                    for (int y = 0; y < room.getSizeY(); y++) {
                        int newX = X + xm * x;
                        int newY = Y + ym * y;
                        if (!isValidPosition(newX, newY) || matrix[newX][newY] != null) {
                            possible = false;
                            break;
                        }
                    }
                    if (!possible)
                        break;
                }
                if (possible)
                    tries = 5;
                tries++;
            }
            if (possible) {
                rooms.remove(room);
                addActiveRoom(X, Y, room, 0, xm, ym, 1);
                for (int x = 0; x < room.getSizeX(); x++) {
                    for (int y = 0; y < room.getSizeY(); y++) {
                        SmallRoom sm = room.getSchem()[x][y];
                        if(sm.getId() < 1)return;
                        int newX = X + xm * x;
                        int newY = Y + ym * y;
                        if(matrix[newX][newY] == null) addActiveRoom(newX, newY, room, 0, xm, ym, sm.getId());
                        //rotate en fonction de xm et ym
                    }
                }
                checkDoors(X, Y, xm, ym, oldX, oldY);
            }
        }
    }
    private void addActiveRoom(int X, int Y, Room room, int rotation, int xm, int ym, int id){
        ActiveRoom activeRoom = new ActiveRoom(X, Y, room, rotation, xm, ym, id);
        matrix[X][Y] = activeRoom;
    }
    private boolean isValidPosition(int X, int Y) {
        return X >= 0 && X < floor.getSize() && Y >= 0 && Y < floor.getSize();
    }
    private void checkDoors(int X, int Y, int xm, int ym, int oldX, int oldY){
        int crash = 0;
        while (crash < 4){
            Bukkit.broadcastMessage("§cList of "+X+" "+Y+" doit avoir "+oldX+" "+oldY);
            for(int[] i : getDoors(X, Y, xm, ym)){
                Bukkit.broadcastMessage("Doors de "+X+" "+Y+": "+i[0]+" "+i[1]);
            }
            boolean con = true;
            for(int[] i : getDoors(X, Y, xm, ym)){
                if(i[0] == oldX&&i[1] == oldY){
                    con = false;
                    break;
                }
            }
            if(!con)break;
            matrix[X][Y].setRotation(matrix[X][Y].getRotation()+90);
            //rotate les cases dans la matrices
            crash++;
        }
    }
    public List<int[]> getDoors(int x, int y, int xm, int ym){
        ActiveRoom room = matrix[x][y];
        List<ActiveRoom> rooms = new ArrayList<>();
        for (int i = 0; i < room.getRoom().getSizeX(); i++) {
            for (int i2 = 0; i2 < room.getRoom().getSizeY(); i2++) {
                ActiveRoom room1 = matrix[x+(xm*i)][y+(ym*i2)];
                if(room1 != null&&room1.getRoom().equals(room.getRoom())){
                    rooms.add(matrix[x+(xm*i)][y+(ym*i2)]);
                }
            }
        }
        List<int[]> accessibleCoordinates = new ArrayList<>();
        for(ActiveRoom r : rooms){
            if(r == null)return accessibleCoordinates;
            if (r.isAccessible(DirectionEnum.NORTH)) {
                if(isValidPosition(x, y-1)){
                    accessibleCoordinates.add(new int[]{x, y-1, x, y});
                }
            }
            if (r.isAccessible(DirectionEnum.SOUTH)) {
                if(isValidPosition(x, y+1)){
                    accessibleCoordinates.add(new int[]{x, y+1, x, y});
                }
            }
            if (r.isAccessible(DirectionEnum.EAST)) {
                if(isValidPosition(x+1, y)){
                    accessibleCoordinates.add(new int[]{x+1, y, x, y});
                }
            }
            if (r.isAccessible(DirectionEnum.WEST)) {
                if(isValidPosition(x-1, y)){
                    accessibleCoordinates.add(new int[]{x-1, y, x, y});
                }
            }
        }
        return accessibleCoordinates;
    }
    public Floor getFloor() {
        return floor;
    }
    public EditSession getEditSession() {
        return editSession;
    }
    public void addEditCount(){
        editCount++;
    }
    public ActiveRoom[][] getMatrix() {
        return matrix;
    }
    public void setPlayerSpawn(Location playerSpawn) {
        this.playerSpawn = playerSpawn;
    }
    public Location getLastLoc() {
        return lastLoc;
    }
    public World getWorld() {
        return world;
    }
    public double getDungeonLoc() {
        return dungeonLoc;
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
