package me.feusalamander.betterdungeons.Gui;

import me.feusalamander.betterdungeons.ActiveDungeon;
import me.feusalamander.betterdungeons.BetterDungeons;
import me.feusalamander.betterdungeons.Floor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("deprecation")
public class GuiListener implements Listener {
    private BetterDungeons main = BetterDungeons.main;
    @EventHandler
    private void onClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        ItemStack item = e.getCurrentItem();
        if(item == null||!item.hasItemMeta()){
            return;
        }
        String name = item.getItemMeta().getDisplayName();
        if(title.equalsIgnoreCase(main.config.getJoinGuiName())){
            e.setCancelled(true);
            mainGui(name, p);
            return;
        }
        for(String s : main.getTypes()){
            if(s.contains(title)){
                e.setCancelled(true);
                onCLickFloor(name, p);
                return;
            }
        }
    }
    private void mainGui(String name, Player p){
        if(name.equalsIgnoreCase("§c§lExit")){
            p.closeInventory();
            return;
        }
        for(String type : main.getTypes()){
            String[] split = type.split(":");
            String name1 = split[0];
            if(name1.equalsIgnoreCase(name)){
                TypeMenu menu = main.getTypeMenus().get(main.getTypes().indexOf(type));
                p.openInventory(menu.getInv());
                break;
            }
        }
    }
    private void onCLickFloor(String name, Player p){
        if(name.equalsIgnoreCase("§aReturn")){
            p.openInventory(main.gui.getGui());
            return;
        }
        for(Floor floor : main.getLoadedfloors()){
            if(name.equalsIgnoreCase(floor.getItemTitle())||name.equalsIgnoreCase(floor.getName())){
                new ActiveDungeon(List.of(p), floor);
            }
        }
    }
}
