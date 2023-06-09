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
    private DungeonBuild build = main.getDungeonBuild();
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
        addActiveRoom(spawnX, spawnY, start, rotation, 1, null);
        createPath();
        build.build(this);
    }
    private void createPath() {
        List<int[]> roomList = getDoors(spawnX, spawnY, 1);
        List<int[]> roomList2 = new ArrayList<>(roomList);
        int crash = 0;
        boolean enabled = true;
        while (enabled && crash <= floor.getSize()* floor.getSize()) {
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
                roomList.addAll(getDoors(r[0], r[1], matrix[r[0]][r[1]].getRatio()));
            }
            roomList2.clear();
            roomList2.addAll(roomList);
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
    private void addSizedRoom(int X, int Y, int oldX, int oldY) {
        List<Room> roomList = new ArrayList<>(rooms);
        Room room = null;
        boolean possible = false;
        int multiplication = 0;

        while (!possible) {
            multiplication = 1;
            room = roomList.get(rand.nextInt(roomList.size()));
            if (room.getSizeX() == 1 && room.getSizeY() == 1) {
                addActiveRoom(X, Y, room, 0, 1, null);
                int crash = 0;
                while (crash < 4){
                    Bukkit.broadcastMessage("§cList of "+X+" "+Y+" doit avoir "+oldX+" "+oldY);
                    for(int[] i : getDoors(X, Y, 1)){
                        Bukkit.broadcastMessage("Doors de "+X+" "+Y+": "+i[0]+" "+i[1]+"  "+i.length);
                    }
                    boolean con = true;
                    for(int[] i : getDoors(X, Y, 1)){
                        if(i[0] == oldX&&i[1] == oldY){
                            con = false;
                            break;
                        }
                    }
                    if(!con)break;
                    matrix[X][Y].setRotation(matrix[X][Y].getRotation()+90);
                    crash++;
                }
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
        addActiveRoom(X, Y, room, 0, multiplication, null);
        ActiveRoom mainRoom = matrix[X][Y];
        for (int i = 0; i < room.getSizeX(); i++) {
            for (int i2 = 0; i2 < room.getSizeY(); i2++) {
                int newX = X + (multiplication * i);
                int newY = Y + (multiplication * i2);
                if (matrix[newX][newY] == null) {
                    List<DirectionEnum> list = new ArrayList<>();
                    if(room.getDirections().get(0)&&newY == mainRoom.getY()-1){
                        list.add(DirectionEnum.NORTH);
                    }
                    if(room.getDirections().get(1)&&newX == mainRoom.getX()+1){
                        list.add(DirectionEnum.EAST);
                    }
                    if(room.getDirections().get(2)&&newY == mainRoom.getY()+1){
                        list.add(DirectionEnum.SOUTH);
                    }
                    if(room.getDirections().get(3)&&newX == mainRoom.getX()-1){
                        list.add(DirectionEnum.WEST);
                    }
                    addActiveRoom(newX, newY, null, 0, 1, list);
                }
            }
        }
        int crash = 0;
        while (crash < 4){
            boolean con = true;
            for(int[] i : getDoors(X, Y, multiplication)){
                if(i[0] == oldX&&i[1] == oldY){
                    con = false;
                    break;
                }
            }
            if(!con)break;
            matrix[X][Y].setRotation(matrix[X][Y].getRotation()+90);
            crash++;
        }
    }
    private void addActiveRoom(int X, int Y, Room room, int rotation, int ratio, List<DirectionEnum> preDoor){
        ActiveRoom activeRoom = new ActiveRoom(X, Y, room, rotation, ratio, preDoor);
        matrix[X][Y] = activeRoom;
    }
    private boolean isValidPosition(int X, int Y) {
        return X >= 0 && X < floor.getSize() && Y >= 0 && Y < floor.getSize();
    }
    public List<int[]> getDoors(int x, int y, int ratio){
        ActiveRoom room = matrix[x][y];
        List<ActiveRoom> rooms = new ArrayList<>();
        for (int i = 0; i < room.getRoom().getSizeX(); i++) {
            for (int i2 = 0; i2 < room.getRoom().getSizeY(); i2++) {
                rooms.add(matrix[x+(ratio*i)][y+(ratio*i2)]);
            }
        }
        List<int[]> accessibleCoordinates = new ArrayList<>();
        for(ActiveRoom r : rooms){
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
