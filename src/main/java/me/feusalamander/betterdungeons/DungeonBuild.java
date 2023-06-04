package me.feusalamander.betterdungeons;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.feusalamander.betterdungeons.Manageurs.ActiveDungeon;
import me.feusalamander.betterdungeons.Manageurs.ActiveRoom;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;

public class DungeonBuild {
    BetterDungeons main = BetterDungeons.main;
    public void useSchematic(ActiveDungeon dungeon, Location loc, String path, int rotation){
        File schematic = new File(main.getDataFolder(), path);
        if(!schematic.exists())schematic = new File(main.getDataFolder(), "rooms/null.schem");
        ClipboardFormat format = ClipboardFormats.findByFile(schematic);
        assert format != null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(schematic))) {
            Clipboard clipboard = reader.read();
            ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
            AffineTransform transform = new AffineTransform().rotateY(rotation);
            clipboardHolder.setTransform(transform);
            Operation operation = clipboardHolder.createPaste(dungeon.getEditSession())
                    .to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()))
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
            dungeon.addEditCount();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void build(ActiveDungeon dungeon){
        ActiveRoom[][] matrix = dungeon.getMatrix();
        Location lastLoc = dungeon.getLastLoc();
        for(ActiveRoom[] colon : matrix){
            for(ActiveRoom box : colon){
                if(box == null){
                    useSchematic(dungeon, lastLoc, "rooms/null.schem", 0);
                }else if (!box.getRoom().equals(main.getPlaceholderRoom())){
                    if(box.getRoom().getType().equalsIgnoreCase("start")){
                         dungeon.setPlayerSpawn(new Location(dungeon.getWorld(), lastLoc.getX(), 53, lastLoc.getZ(), 180-box.getRotation(), 0));
                    }
                    if(box.getRoom().getSizeX()>1||box.getRoom().getSizeY()>1){
                        Location newLoc = lastLoc.clone();
                        newLoc.add(box.getModifiedX(), 0, box.getModifiedY());
                        useSchematic(dungeon, newLoc, box.getRoom().getPath(), box.getRotation());
                    }else {
                        useSchematic(dungeon, lastLoc, box.getRoom().getPath(), box.getRotation());
                    }
                }
                if(lastLoc.getZ() < ((dungeon.getFloor().getSize()-1)*32)+ dungeon.getDungeonLoc()){
                    lastLoc.set(lastLoc.getX(), 50, lastLoc.getZ()+32);
                }else{
                    lastLoc.set(lastLoc.getX()+32, 50, dungeon.getDungeonLoc());
                }
            }
        }
    }
}
