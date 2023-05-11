package me.feusalamander.betterguns.betterdungeons;

import me.feusalamander.betterguns.betterdungeons.Configs.Config;
import me.feusalamander.betterguns.betterdungeons.Configs.FloorsConf;
import me.feusalamander.betterguns.betterdungeons.join.JoinMenu;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class BetterDungeons extends JavaPlugin {
    public JoinMenu gui;
    public Config config;
    public static BetterDungeons main;
    public FloorsConf floorsConf;
    private List<Floor> loadedfloors;

    @Override
    public void onEnable() {
        getLogger().info("Better Dungeons by FeuSalamander is loading !");
        main = this;
        saveDefaultConfig();
        floorsConf = new FloorsConf();
        config = new Config(getConfig());
        gui = new JoinMenu(config);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    private void loadFloors(){
        for(String floorkey : floorsConf.getFloors()){
            final ConfigurationSection section = floorsConf.getConfig().getConfigurationSection(floorkey);
            assert section != null;
            String name = section.getName();
            final Floor floor = new Floor(Integer.parseInt(name));
            this.loadedfloors.add(floor);
        }
    }
    public List<Floor> getLoadedfloors(){
        return loadedfloors;
    }
}
