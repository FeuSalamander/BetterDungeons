package me.feusalamander.betterdungeons.Manageurs;

import me.feusalamander.betterdungeons.BetterDungeons;
import me.feusalamander.betterdungeons.DirectionEnum;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import scala.Int;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class Room {
    private String name = null;
    private final String id;
    private boolean activated = false;
    private int sizex = 0;
    private int sizey = 0;
    private String path = null;
    private String type = null;
    private final BetterDungeons main = BetterDungeons.main;
    private int modifiedX = 0;
    private int modifiedY = 0;
    private List<Boolean> directions;
    public Room(final String id){
        this.id = id;
        if(id.equalsIgnoreCase("-1"))return;
        final ConfigurationSection section = main.getRoomsConf().getConfig().getConfigurationSection(id);
        assert section != null;
        name = section.getString("name");
        path = section.getString("path");
        activated = section.getBoolean("activated");
        String[] split = Objects.requireNonNull(section.getString("size")).split("x");
        sizex = Integer.parseInt(split[0]);
        sizey = Integer.parseInt(split[1]);
        type = section.getString("type");
        directions = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {directions.add(false);}
        for(String s : section.getStringList("doors")){
            directions.set(DirectionEnum.valueOf(s).ordinal(), true);
        }
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
    public List<Boolean> getDirections() {
        return directions;
    }
}
