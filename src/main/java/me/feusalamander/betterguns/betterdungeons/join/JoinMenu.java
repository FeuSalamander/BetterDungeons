package me.feusalamander.betterguns.betterdungeons.join;

import me.feusalamander.betterguns.betterdungeons.Configs.Config;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
@SuppressWarnings("deprecation")
public class JoinMenu {
    private final Inventory inv;
    public JoinMenu(Config config){
        inv = Bukkit.createInventory(null, 27, config.getJoinGuiName());
    }
    public Inventory getGui(){
        return inv;
    }
}
