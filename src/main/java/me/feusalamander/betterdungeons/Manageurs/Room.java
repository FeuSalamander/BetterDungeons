package me.feusalamander.betterdungeons.Manageurs;

import me.feusalamander.betterdungeons.BetterDungeons;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class Room {
    private final String name;
    private final String id;
    private final boolean activated;
    private final int sizex;
    private final int sizey;
    private final String path;
    private final String type;
    private final BetterDungeons main = BetterDungeons.main;

    public Room(final String id){
        this.id = id;
        final ConfigurationSection section = main.getRoomsConf().getConfig().getConfigurationSection(id);
        assert section != null;
        name = section.getString("name");
        path = section.getString("path");
        activated = section.getBoolean("activated");
        String[] split = Objects.requireNonNull(section.getString("size")).split("x");
        sizex = Integer.parseInt(split[0]);
        sizey = Integer.parseInt(split[1]);
        type = section.getString("type");
        issues();
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
}
