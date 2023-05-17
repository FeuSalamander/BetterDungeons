package me.feusalamander.betterdungeons.Manageurs;

import me.feusalamander.betterdungeons.BetterDungeons;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
@SuppressWarnings("deprecation")
public class Floor {
    private final String name;
    private final String id;
    private final boolean activated;
    private final String type;
    private final int size;
    private final List<Integer> rooms;
    private final String itemSkin;
    private final String itemTitle;
    private final List<String> itemLore;
    private final BetterDungeons main = BetterDungeons.main;

    public Floor(final String id){
        this.id = id;
        final ConfigurationSection section = main.getFloorsConf().getConfig().getConfigurationSection(id);
        name = section.getString("name");
        activated = section.getBoolean("activated");
        type = section.getString("type");
        size = section.getInt("size");
        rooms = section.getIntegerList("rooms");
        itemSkin = section.getString("item.skin");
        itemTitle = section.getString("item.title");
        itemLore = section.getStringList("item.lore");
        issues();
    }
    private void issues(){
        if(name == null){
            main.getLogger().info("§4[BD] The Floor with the id:"+id+" needs to have a valid name");
            unload();
        }
        if(!type.contains(":")){
            main.getLogger().info("§4[BD] The Floor with the id:"+id+" needs to have a valid type");
            unload();
        }else {
            if(!main.getTypes().contains(type)){
                main.getTypes().add(type);
            }
        }
        if(size == 0){
            main.getLogger().info("§4[BD] The Floor with the id:"+id+" needs to have a valid size >=2");
            unload();
        }
        if(rooms == null){
            main.getLogger().info("§4[BD] The Floor with the id:"+id+" needs to have valid rooms");
            unload();
        }
        if(itemSkin == null){
            Bukkit.getConsoleSender().sendMessage("§4[BD] The Floor with the id:"+id+" needs to have valid item skin");
        }
    }
    private void unload(){
        main.getLoadedfloors().remove(this);
    }

    public boolean isActivated() {
        return activated;
    }

    public int getSize() {
        return size;
    }

    public String getId() {
        return id;
    }

    public List<Integer> getRooms() {
        return rooms;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getItemSkin() {
        return itemSkin;
    }

    public List<String> getItemLore() {
        return itemLore;
    }

    public String getItemTitle() {
        return itemTitle;
    }
}
