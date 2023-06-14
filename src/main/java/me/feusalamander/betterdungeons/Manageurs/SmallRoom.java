package me.feusalamander.betterdungeons.Manageurs;

import java.util.List;

public class SmallRoom {
    private final List<Boolean> directions;
    private final int id;
    private final Room room;
    public SmallRoom(Room room, List<Boolean> directions, int id){
        this.directions = directions;
        this. id = id;
        this.room = room;
    }
    public int getId() {
        return id;
    }
    public List<Boolean> getDirections() {
        return directions;
    }
}
