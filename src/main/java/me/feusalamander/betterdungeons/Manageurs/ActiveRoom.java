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
    public ActiveRoom(int X, int Y, Room room, int rotation, int xm, int ym, int id){
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
        if(room.getSizeX() == 1&&room.getSizeY() == 1){
            int index = (direction.ordinal() + rotation / 90) % 4;
            if (index<0)index += 4;
            return room.getDirections(id).get(index);
        }else{
            switch (direction) {
                case NORTH:
                    return xm < 0 && room.getDirections(id).get(2);
                case SOUTH:
                    return xm < 0 && room.getDirections(id).get(0);
                case WEST:
                    return ym < 0 && room.getDirections(id).get(1);
                case EAST:
                    return ym < 0 && room.getDirections(id).get(3);
                default:
                    return room.getDirections(id).get(direction.ordinal());
            }
        }
    }
    public void setRotation(ActiveRoom[][] matrix) {
        this.rotation+=90;
        matrix[X][Y] = this;
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
