package me.feusalamander.betterguns.betterdungeons;

import me.feusalamander.betterguns.betterdungeons.Commands.CmdExecutor;
import me.feusalamander.betterguns.betterdungeons.Commands.Completer;
import me.feusalamander.betterguns.betterdungeons.Configs.Config;
import me.feusalamander.betterguns.betterdungeons.Configs.FloorsConf;
import me.feusalamander.betterguns.betterdungeons.join.JoinMenu;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BetterDungeons extends JavaPlugin {
    public JoinMenu gui;
    public Config config;
    public static BetterDungeons main;
    public FloorsConf floorsConf;
    private final List<Floor> loadedfloors = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("Better Dungeons by FeuSalamander is loading !");
        main = this;
        saveDefaultConfig();
        floorsConf = new FloorsConf();
        config = new Config(getConfig());
        gui = new JoinMenu(config);
        loadFloors();
        Objects.requireNonNull(getCommand("BD")).setTabCompleter(new Completer());
        Objects.requireNonNull(getCommand("BD")).setExecutor(new CmdExecutor());
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
            final Floor floor = new Floor(name);
            this.loadedfloors.add(floor);
        }
    }
    public List<Floor> getLoadedfloors(){
        return loadedfloors;
    }
}
