package me.feusalamander.betterdungeons.Manageurs;

import me.feusalamander.betterdungeons.DirectionEnum;
import org.bukkit.Bukkit;

import java.util.List;

public class ActiveRoom {
    private final int X;
    private final int Y;
    private final Room room;
    private int rotation;
    private final int ratio;
    private final List<DirectionEnum> presetDoor;
    public ActiveRoom(int X, int Y, Room room, int rotation, int ratio, List<DirectionEnum> presetDoor){
        this.X = X;
        this.Y = Y;
        this.room = room;
        this.rotation = rotation;
        this.ratio =ratio;
        this.presetDoor = presetDoor;
    }
    public int getX() {return X;}
    public int getY() {return Y;}
    public Room getRoom() {return room;}
    public int getRotation() {return rotation;}
    public int getModifiedX(){return room.getModifiedX()*ratio;}
    public int getModifiedY(){return room.getModifiedY()*ratio;}
    public boolean isAccessible(DirectionEnum direction) {
        if(presetDoor != null&&presetDoor.contains(direction))return true;
        int index = (direction.ordinal() + rotation / 90) % 4;
        if (index<0){
            index += 4;
        }
        if(room == null)return false;
        return room.getDirections().get(index);
    }
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
    public int getRatio() {
        return ratio;
    }
}
