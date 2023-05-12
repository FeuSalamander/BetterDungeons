package me.feusalamander.betterdungeons.join;

import me.feusalamander.betterdungeons.BetterDungeons;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class GuiListener implements Listener {
    @EventHandler
    private void onClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        ItemStack item = e.getCurrentItem();
        if(item == null||!item.hasItemMeta()){
            return;
        }
        String name = item.getItemMeta().getDisplayName();
        if(title.equalsIgnoreCase(BetterDungeons.main.config.getJoinGuiName())){
            e.setCancelled(true);
            if(name.equalsIgnoreCase("§c§lExit")){
                p.closeInventory();
            }
        }
    }
}
