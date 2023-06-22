package me.feusalamander.betterdungeons;

import me.feusalamander.betterdungeons.Commands.CmdExecutor;
import me.feusalamander.betterdungeons.Commands.Completer;
import me.feusalamander.betterdungeons.Configs.FloorsConf;
import me.feusalamander.betterdungeons.Configs.RoomsConf;
import me.feusalamander.betterdungeons.Gui.GuiListener;
import me.feusalamander.betterdungeons.Gui.JoinMenu;
import me.feusalamander.betterdungeons.Configs.Config;
import me.feusalamander.betterdungeons.Gui.Tools;
import me.feusalamander.betterdungeons.Gui.TypeMenu;
import me.feusalamander.betterdungeons.Manageurs.ActiveDungeon;
import me.feusalamander.betterdungeons.Manageurs.Floor;
import me.feusalamander.betterdungeons.Manageurs.Room;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BetterDungeons extends JavaPlugin {
    public JoinMenu gui;
    public static Config config;
    public static BetterDungeons main;
    private FloorsConf floorsConf;
    private RoomsConf roomsConf;
    private World world = null;
    private DungeonBuild build;
    private final List<Floor> loadedfloors = new ArrayList<>();
    private final List<Room> loadedrooms = new ArrayList<>();
    private final List<String> types = new ArrayList<>();
    private final List<TypeMenu> typeMenus = new ArrayList<>();
    private final List<ActiveDungeon> activeDungeons = new ArrayList<>();
    private final List<Double> usedLocations = new ArrayList<>();
    @SuppressWarnings("deprecation")
    @Override
    public void onEnable() {
        getLogger().info("Better Dungeons by FeuSalamander is loading !");
        main = this;
        saveDefaultConfig();
        floorsConf = new FloorsConf();
        roomsConf = new RoomsConf();
        config = new Config(getConfig());
        build = new DungeonBuild();
        createWorld();
        loadRooms();
        loadFloors();
        Objects.requireNonNull(getCommand("BD")).setTabCompleter(new Completer());
        Objects.requireNonNull(getCommand("BD")).setExecutor(new CmdExecutor());
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        loadGuis();
    }
    @Override
    public void onDisable() {
        for(ActiveDungeon activeDungeon : getActivedungeons()){
            activeDungeon.unload();
        }
    }
    private void loadFloors(){
        for(String floorkey : floorsConf.getFloors()){
            final ConfigurationSection section = floorsConf.getConfig().getConfigurationSection(floorkey);
            assert section != null;
            String id = section.getName();
            final Floor floor = new Floor(id);
            this.loadedfloors.add(floor);
        }
    }
    private void loadRooms(){
        for(String roomkey : roomsConf.getRooms()){
            final ConfigurationSection section = roomsConf.getConfig().getConfigurationSection(roomkey);
            assert section != null;
            String id = section.getName();
            final Room room = new Room(id);
            this.loadedrooms.add(room);
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
    private void createWorld(){
        WorldCreator creator = new WorldCreator("DungeonWorld");
        creator.generator(new VoidWorld());
        world = Bukkit.createWorld(creator);
        assert world != null;
        world.setPVP(false);
        world.setAutoSave(false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.NATURAL_REGENERATION, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setGameRule(GameRule.FALL_DAMAGE, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.UNIVERSAL_ANGER, false);
    }
    public List<Floor> getLoadedfloors(){
        return loadedfloors;
    }
    public List<Room> getLoadedrooms() {
        return loadedrooms;
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
    public World getWorld(){
        return world;
    }
    public List<Double> getUsedLocations() {
        return usedLocations;
    }
    public FloorsConf getFloorsConf() {
        return floorsConf;
    }
    public RoomsConf getRoomsConf(){
        return roomsConf;
    }
    public DungeonBuild getDungeonBuild(){
        return build;
    }
}
