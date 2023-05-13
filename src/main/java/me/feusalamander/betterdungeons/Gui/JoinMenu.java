package me.feusalamander.betterdungeons.Gui;

import me.feusalamander.betterdungeons.BetterDungeons;
import me.feusalamander.betterdungeons.Configs.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("deprecation")
public class JoinMenu {
    private final Tools t;
    private final Inventory inv;
    private final BetterDungeons main = BetterDungeons.main;
    public JoinMenu(Config config, Tools t){
        this.t = t;
        inv = Bukkit.createInventory(null, 54, config.getJoinGuiName());
        t.addPanes(inv);
        createGui();
    }
    private void createGui(){
        t.addItem(inv, 49, Material.BARRIER, "§c§lExit");
        t.addItem(inv, 53, Material.PAPER, "§aDungeon Rules and Tips");
        loadFloors();
    }
    private void loadFloors(){
        int size = main.getTypes().size();
        if(size == 1){
            String[] split = main.getTypes().get(0).split(":");
            String name = split[0];
            String skin = split[1];
            t.addHead(inv, 22, skin, name);
        } else if (size == 2) {
            int[] slots = {21, 23};
            for(int i = 0; i< size; i++){
                String[] split = main.getTypes().get(i).split(":");
                String name = split[0];
                String skin = split[1];
                t.addHead(inv, slots[i], skin, name);
            }
        } else if (size == 3) {
            int[] slots = {20, 22, 24};
            for(int i = 0; i< size; i++){
                String[] split = main.getTypes().get(i).split(":");
                String name = split[0];
                String skin = split[1];
                t.addHead(inv, slots[i], skin, name);
            }
        }else if (size > 3) {
            for(int i = 0; i< size; i++){
                String[] split = main.getTypes().get(i).split(":");
                String name = split[0];
                String skin = split[1];
                t.addHead(inv, 20+i, skin, name);
            }
        }
    }
    public Inventory getGui(){
        return inv;
    }
}
