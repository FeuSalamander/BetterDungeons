package me.feusalamander.betterdungeons;

import me.feusalamander.betterdungeons.Commands.CmdExecutor;
import me.feusalamander.betterdungeons.Commands.Completer;
import me.feusalamander.betterdungeons.Configs.FloorsConf;
import me.feusalamander.betterdungeons.Gui.GuiListener;
import me.feusalamander.betterdungeons.Gui.JoinMenu;
import me.feusalamander.betterdungeons.Configs.Config;
import me.feusalamander.betterdungeons.Gui.Tools;
import me.feusalamander.betterdungeons.Gui.TypeMenu;
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
    private final List<TypeMenu> typeMenus = new ArrayList<>();
    private final List<ActiveDungeon> activeDungeons = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("Better Dungeons by FeuSalamander is loading !");
        main = this;
        saveDefaultConfig();
        floorsConf = new FloorsConf();
        config = new Config(getConfig());
        loadFloors();
        Objects.requireNonNull(getCommand("BD")).setTabCompleter(new Completer());
        Objects.requireNonNull(getCommand("BD")).setExecutor(new CmdExecutor());
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        loadGuis();
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
    private void loadGuis(){
        Tools tools = new Tools();
        gui = new JoinMenu(config, tools);
        for(String type : getTypes()){
            TypeMenu menu = new TypeMenu(tools, type);
            getTypeMenus().add(menu);
        }
    }
    public List<Floor> getLoadedfloors(){
        return loadedfloors;
    }
    public List<String> getTypes(){
        return types;
    }
    public List<TypeMenu> getTypeMenus() {
        return typeMenus;
    }
    public List<ActiveDungeon> getActivedungeons() {
        return activeDungeons;
    }
}
