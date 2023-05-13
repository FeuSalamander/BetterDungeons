package me.feusalamander.betterdungeons.Gui;

import me.feusalamander.betterdungeons.BetterDungeons;
import me.feusalamander.betterdungeons.Floor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("deprecation")
public class TypeMenu {
    private final BetterDungeons main = BetterDungeons.main;
    private final Inventory inv;
    private final Tools t;
    private final String  name;
    public TypeMenu(Tools t, String name){
        this.t = t;
        String[] split = name.split(":");
        this.name = split[0];
        inv = Bukkit.createInventory(null, 54, split[0]);
        createGui();
    }
    private void createGui(){
        t.addPanes(inv);
        t.addHead(inv, 49, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTJiYTgxYjQ3ZDVlZTA2YjQ4NGVhOWJkZjIyOTM0ZTZhYmNhNWU0Y2VkN2JlMzkwNWQ2YWU2ZWNkNmZjZWEyYSJ9fX0=", "§aReturn");
        addFloors();
    }
    private void addFloors(){
        int[] slots = {11, 12, 13, 14, 15, 21, 22, 23};
        int i = 0;
        for(Floor floor : main.getLoadedfloors()){
            String name1 = floor.getType().split(":")[0];
            if(floor.isActivated()&&name1.equalsIgnoreCase(name)){
                if(floor.getItemTitle() != null){
                    t.addHead(inv, slots[i], floor.getItemSkin(), floor.getItemTitle(), floor.getItemLore());
                }else{
                    t.addHead(inv, slots[i], floor.getItemSkin(), floor.getName(), floor.getItemLore());
                }
                i++;
            }
        }
    }
    public Inventory getInv() {
        return inv;
    }

    public String getName() {
        return name;
    }
}