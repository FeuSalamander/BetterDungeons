package me.feusalamander.betterdungeons.Commands;

import me.feusalamander.betterdungeons.BetterDungeons;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CmdExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player p))return false;
        if(args.length == 0){p.sendMessage("This command doesn't exist");return false;}
        if(args[0].equalsIgnoreCase("gui")){
            p.openInventory(BetterDungeons.main.gui.getGui());
            return true;
        }
        p.sendMessage("This command doesn't exist");
        return false;
    }
}
