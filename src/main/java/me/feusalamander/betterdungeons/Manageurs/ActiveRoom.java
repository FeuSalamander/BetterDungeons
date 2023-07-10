package me.feusalamander.betterdungeons.Manageurs;

import me.feusalamander.betterdungeons.DirectionEnum;

public class ActiveRoom {
    private int X;
    private int Y;
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
    public int[] getModified(){
        int fRotation = (int) Math.toDegrees(Math.atan2(ym, -xm));
        if (fRotation < 0) {
            fRotation += 360;
        }
        int rRotation = (fRotation +this.rotation)%360;
        int[] last = new int[2];
        int angle = rRotation % 360;
        int signX = (angle == 90 || angle == 180) ? -1 : 1;
        int signY = (angle == 180 || angle == 270) ? -1 : 1;
        last[0] = signX * room.getModifiedX();
        last[1] = signY * room.getModifiedY();
        return last;
    }
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
    public void setRotation() {
        this.rotation+=90;
    }
    public void setRotationn(ActiveDungeon dungeon) {
        this.rotation+=90;
        dungeon.getMatrix()[X][Y] = this;
    }
    public void setLoc(int x, int y){
        this.X = x;
        this.Y = y;
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
