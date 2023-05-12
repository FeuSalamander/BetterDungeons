package me.feusalamander.betterdungeons.join;

import me.feusalamander.betterdungeons.BetterDungeons;
import me.feusalamander.betterdungeons.Configs.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class JoinMenu {
    private final Inventory inv;
    private final BetterDungeons main = BetterDungeons.main;
    public JoinMenu(Config config){
        inv = Bukkit.createInventory(null, 54, config.getJoinGuiName());
        addPanes();
        createGui();
    }
    public Inventory getGui(){
        return inv;
    }
    private void createGui(){
        addItem(49, Material.BARRIER, "§c§lExit", null);
        addItem(53, Material.PAPER, "§aDungeon Rules and Tips", null);
        loadFloors();
    }
    private void addItem(int slot, Material material, String name, List<String> lore){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if(lore != null)meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }
    private void addPanes(){
        for(int i = 0; i<inv.getSize(); i++){
            ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
    }
    private void loadFloors(){
        int size = main.getTypes().size();
        if(size == 1){
            String[] split = main.getTypes().get(0).split(":");
            String name = split[0];
            String skin = split[1];
            addHead(22, skin, name, null);
        } else if (size == 2) {
            int[] slots = {21, 23};
            for(int i = 0; i< size; i++){
                String[] split = main.getTypes().get(i).split(":");
                String name = split[0];
                String skin = split[1];
                addHead(slots[i], skin, name, null);
            }
        } else if (size == 3) {
            int[] slots = {20, 22, 24};
            for(int i = 0; i< size; i++){
                String[] split = main.getTypes().get(i).split(":");
                String name = split[0];
                String skin = split[1];
                addHead(slots[i], skin, name, null);
            }
        }else if (size > 3) {
            for(int i = 0; i< size; i++){
                String[] split = main.getTypes().get(i).split(":");
                String name = split[0];
                String skin = split[1];
                addHead(20+i, skin, name, null);
            }
        }
    }
    private void addHead(int slot, String skin, String name, List<String> lore){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        Bukkit.getUnsafe().modifyItemStack(item, "{SkullOwner:{Id:\"" + new UUID(skin.hashCode(), skin.hashCode()) + "\",Properties:{textures:[{Value:\"" + skin + "\"}]}}}");
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(name);
        if(lore != null)meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }
}
