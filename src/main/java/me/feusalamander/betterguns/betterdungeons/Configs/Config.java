package me.feusalamander.betterguns.betterdungeons.Configs;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    private final String joinGuiName;
    public Config(FileConfiguration config){
        joinGuiName = config.getString("Join.Gui.name");
    }
    public String getJoinGuiName(){
        return joinGuiName;
    }
}
