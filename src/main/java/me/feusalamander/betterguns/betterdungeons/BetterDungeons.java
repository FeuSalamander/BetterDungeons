package me.feusalamander.betterguns.betterdungeons;

import me.feusalamander.betterguns.betterdungeons.Configs.Config;
import me.feusalamander.betterguns.betterdungeons.join.JoinMenu;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterDungeons extends JavaPlugin {
    public JoinMenu gui;
    public Config config;

    @Override
    public void onEnable() {
        getLogger().info("Better Dungeons by FeuSalamander is loading !");
        saveDefaultConfig();
        config = new Config(getConfig());
        gui = new JoinMenu(config);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
