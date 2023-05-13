package me.feusalamander.betterdungeons.Gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("deprecation")
public class TypeMenu {
    private Inventory inv;
    private Tools t;
    private String  name;
    public TypeMenu(Tools t, String name){
        this.t = t;
        String[] split = name.split(":");
        this.name = split[0];
        inv = Bukkit.createInventory(null, 54, split[0]);
        createGui();
    }
    private void createGui(){
        t.addPanes(inv);
    }

    public Inventory getInv() {
        return inv;
    }

    public String getName() {
        return name;
    }
}
