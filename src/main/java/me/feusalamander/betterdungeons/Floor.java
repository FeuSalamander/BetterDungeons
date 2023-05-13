package me.feusalamander.betterdungeons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
@SuppressWarnings("deprecation")
public class Floor {
    private String name;
    private String id;
    private boolean activated;
    private String type;
    private int size;
    private List<Integer> rooms;
    private BetterDungeons main = BetterDungeons.main;

    public Floor(final String id){
        this.id = id;
        final ConfigurationSection section = main.floorsConf.getConfig().getConfigurationSection(id);
        name = section.getString("name");
        activated = section.getBoolean("activated");
        type = section.getString("type");
        size = section.getInt("size");
        rooms = section.getIntegerList("rooms");
        issues();
    }
    private void issues(){
        if(name.isEmpty()){
            main.getLogger().info("§4[BD] The Floor with the id:"+id+" needs to have a valid name");
            unload();
        }
        if(!type.contains(":")){
            main.getLogger().info("§4[BD] The Floor with the id:"+id+" needs to have a valid type");
            unload();
        }else {
            if(!main.getTypes().contains(type)){
                main.getTypes().add(type);
            }
        }
        if(size == 0){
            main.getLogger().info("§4[BD] The Floor with the id:"+id+" needs to have a valid size >=2");
            unload();
        }
        if(rooms.isEmpty()){
            main.getLogger().info("§4[BD] The Floor with the id:"+id+" needs to have valid rooms");
            unload();
        }
    }
    private void unload(){
        main.getLoadedfloors().remove(this);
    }
}
