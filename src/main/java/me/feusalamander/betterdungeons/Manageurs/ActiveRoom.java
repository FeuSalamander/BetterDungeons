package me.feusalamander.betterdungeons.Manageurs;

import me.feusalamander.betterdungeons.DirectionEnum;
import org.bukkit.Bukkit;

import java.util.List;

public class ActiveRoom {
    private final int X;
    private final int Y;
    private final Room room;
    private int rotation;
    private final int xm;
    private final int ym;
    private final int id;
    public ActiveRoom(int X, int Y, Room room, int id, int rotation, int xm, int ym){
        this.X = X;
        this.Y = Y;
        this.room = room;
        this.rotation = rotation;
        this.xm = xm;
        this.ym = ym;
        this.id = id;
    }
    public int getX() {return X;}
    public int getY() {return Y;}
    public Room getRoom() {return room;}
    public int getRotation() {return rotation;}
    public int getModifiedX(){return room.getModifiedX()*xm;}
    public int getModifiedY(){return room.getModifiedY()*ym;}
    public boolean isAccessible(DirectionEnum direction) {
        int index = (direction.ordinal() + rotation / 90) % 4;
        if (index<0)index += 4;
        if(room == null)return false;
        return room.getDirections(id).get(index);
    }
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
    public int getXm() {
        return xm;
    }
    public int getYm() {
        return ym;
    }
    public int getId() {
        return id;
    }
}
