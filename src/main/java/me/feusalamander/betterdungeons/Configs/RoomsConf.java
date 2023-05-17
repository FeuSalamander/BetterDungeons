package me.feusalamander.betterdungeons.Configs;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static me.feusalamander.betterdungeons.BetterDungeons.main;

public class RoomsConf {
    private final File f;
    private YamlConfiguration config;
    private Set<String> rooms;
    public RoomsConf(){
        f = new File(main.getDataFolder(), "Rooms.yml");
        File folder = new File(main.getDataFolder(), "rooms");
        if(!f.exists()) main.saveResource("Rooms.yml", false);
        if(!folder.exists()) {try{folder.mkdir();}catch (Exception e){e.printStackTrace();}}
        config = YamlConfiguration.loadConfiguration(f);
        rooms = config.getKeys(false);
    }
    public Set<String> getRooms(){
        return rooms;
    }
    public YamlConfiguration getConfig(){
        return config;
    }
}
