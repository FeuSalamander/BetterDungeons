package me.feusalamander.betterdungeons.Manageurs;

public class ActiveRoom {
    private final int X;
    private final int Y;
    private final Room room;
    private final int rotation;
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
}
