package me.feusalamander.betterdungeons.Configs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    private final String joinGuiName;
    private final Location lobby;
    public Config(FileConfiguration config){
        joinGuiName = config.getString("Join.Gui.name");
        lobby = new Location(Bukkit.getWorld("world"), config.getInt("Spawn.x"), config.getInt("Spawn.y"), config.getInt("Spawn.z"));
    }
    public String getJoinGuiName(){
        return joinGuiName;
    }

    public Location getLobby() {
        return lobby;
    }
}
