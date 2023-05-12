package me.feusalamander.betterdungeons;

import me.feusalamander.betterdungeons.Commands.CmdExecutor;
import me.feusalamander.betterdungeons.Commands.Completer;
import me.feusalamander.betterdungeons.Configs.FloorsConf;
import me.feusalamander.betterdungeons.join.GuiListener;
import me.feusalamander.betterdungeons.join.JoinMenu;
import me.feusalamander.betterdungeons.Configs.Config;
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
    private final List<String> types = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("Better Dungeons by FeuSalamander is loading !");
        main = this;
        saveDefaultConfig();
        floorsConf = new FloorsConf();
        config = new Config(getConfig());
        loadFloors();
        gui = new JoinMenu(config);
        Objects.requireNonNull(getCommand("BD")).setTabCompleter(new Completer());
        Objects.requireNonNull(getCommand("BD")).setExecutor(new CmdExecutor());
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
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
    public List<String> getTypes(){
        return types;
    }
}
