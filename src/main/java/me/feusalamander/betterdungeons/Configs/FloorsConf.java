package me.feusalamander.betterdungeons.Configs;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Set;

import static me.feusalamander.betterdungeons.BetterDungeons.main;

public class FloorsConf {
    private final File f;
    private YamlConfiguration config;
    private Set<String> floors;
    public FloorsConf(){
        f = new File(main.getDataFolder(), "Floors.yml");
        if(!f.exists()) main.saveResource("Floors.yml", false);
        config = YamlConfiguration.loadConfiguration(f);
        floors = config.getKeys(false);
    }
    public Set<String> getFloors(){
        return floors;
    }
    public YamlConfiguration getConfig(){
        return config;
    }
}
