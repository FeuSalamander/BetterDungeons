package me.feusalamander.betterdungeons.Manageurs;

import me.feusalamander.betterdungeons.BetterDungeons;
import me.feusalamander.betterdungeons.DirectionEnum;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import scala.Int;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class Room {
    private String name = null;
    private final String id;
    private boolean activated = false;
    private SmallRoom[][] schem;
    private int sizex = 0;
    private int sizey = 0;
    private String path = null;
    private String type = null;
    private final BetterDungeons main = BetterDungeons.main;
    private int modifiedX = 0;
    private int modifiedY = 0;
    public Room(final String id){
        this.id = id;
        if(id.equalsIgnoreCase("-1"))return;
        final ConfigurationSection section = main.getRoomsConf().getConfig().getConfigurationSection(id);
        assert section != null;
        name = section.getString("name");
        path = section.getString("path");
        activated = section.getBoolean("activated");
        useSchem(section);
        type = section.getString("type");
        issues();
        modifiedX = (sizex-1)*16;
        modifiedY = (sizey-1)*16;
    }
    private void issues(){
        if(name == null){
            main.getLogger().info("§4[BD] The Room with the id:"+id+" needs to have a valid name");
            unload();
        }
        if(path == null){
            main.getLogger().info("§4[BD] The Room with the id:"+id+" needs to have a valid path");
            unload();
        }
        if(type == null){
            main.getLogger().info("§4[BD] The Room with the id:"+id+" needs to have a valid type");
            unload();
        }
        if(sizex == 0||sizey == 0){
            main.getLogger().info("§4[BD] The Room with the id:"+id+" needs to have a valid size >=1");
            unload();
        }
    }
    private void unload(){
        main.getLoadedrooms().remove(this);
    }
    private void useSchem(ConfigurationSection section){
        for(Object i : section.getList("size")){
            main.getLogger().info(i+"");
        }

        List<int[]> list1 = Arrays.asList(section.getList("size"));
        sizex = list1.size();
        sizey = list1.get(0).length;
        int[][] list2 = list1.toArray(new int[sizex][sizey]);
        schem = new SmallRoom[sizex][sizey];
        for(int x = 0; x<sizex; x++){
            for(int y = 0; y<sizex; y++){
                int id = list2[x][y];
                if(id == 0){
                    schem[x][y] = null;
                    return;
                }
                List<Boolean> directions = new ArrayList<>(4);
                if(section.contains("doors."+id)){
                    for(String s : section.getStringList("doors."+id)){
                        directions.set(DirectionEnum.valueOf(s).ordinal(), true);
                    }
                }
                SmallRoom s = new SmallRoom(this, directions, id);
                schem[x][y] = s;
            }
        }
    }
    public boolean isActivated() {
        return activated;
    }
    public int getSizeX() {
        return sizex;
    }
    public int getSizeY() {
        return sizey;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public String getPath() {
        return path;
    }
    public int getModifiedX() {
        return modifiedX;
    }
    public int getModifiedY() {
        return modifiedY;
    }
    public List<Boolean> getDirections(int id) {
        for(int x = 0; x<getSizeX(); x++){
            for(int y = 0; y<getSizeY(); y++){
                if(schem[x][y].getId() == id){
                    return schem[x][y].getDirections();
                }
            }
        }
        return null;
    }
    public SmallRoom[][] getSchem(){
        return schem;
    }
}
