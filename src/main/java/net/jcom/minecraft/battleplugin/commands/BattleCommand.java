package net.jcom.minecraft.battleplugin.commands;

import net.jcom.minecraft.battleplugin.data.IsBattleGoingOn;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BattleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Missing argument");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "start" -> {
                if (IsBattleGoingOn.loadData()) {
                    sender.sendMessage("Battle already going on.");
                    return false;
                }
                IsBattleGoingOn.saveData(true);
                Bukkit.broadcastMessage("Battle has started!");
            }
            case "stop" -> {
                if (!IsBattleGoingOn.loadData()) {
                    sender.sendMessage("No battle present right now.");
                    return false;
                }
                IsBattleGoingOn.saveData(false);
                Bukkit.broadcastMessage("Battle was stopped!");
            }
        }

        return true;
    }
}
