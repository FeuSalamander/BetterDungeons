package me.feusalamander.betterdungeons.Manageurs;

import me.feusalamander.betterdungeons.DirectionEnum;
import org.bukkit.Bukkit;

public class ActiveRoom {
    private final int X;
    private final int Y;
    private final Room room;
    private int rotation;
    private final int ratio;
    public ActiveRoom(int X, int Y, Room room, int rotation, int ratio){
        this.X = X;
        this.Y = Y;
        this.room = room;
        this.rotation = rotation;
        this.ratio =ratio;
    }

    public int getX() {return X;}
    public int getY() {return Y;}
    public Room getRoom() {return room;}
    public int getRotation() {return rotation;}
    public int getModifiedX(){return room.getModifiedX()*ratio;}
    public int getModifiedY(){return room.getModifiedY()*ratio;}
    public boolean isAccessible(DirectionEnum direction) {
        int index = (direction.ordinal() + rotation / 90) % 4;
        if (index<0){
            index += 4;
        }
        return room.getDirections().get(index);
    }
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
}
