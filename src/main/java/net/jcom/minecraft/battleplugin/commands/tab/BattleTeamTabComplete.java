package net.jcom.minecraft.battleplugin.commands.tab;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class BattleTeamTabComplete implements TabCompleter {


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> complete = new ArrayList<>(), completeStarted = new ArrayList<>();

        if (sender instanceof Player player) {

            if (args.length == 1) {
                if (player.hasPermission("battle-plugin.team.join")) {
                    complete.add("join");
                }

                if (player.hasPermission("battle-plugin.battle.leave")) {
                    complete.add("leave");
                }

                if (player.hasPermission("battle-plugin.battle.list")) {
                    complete.add("list");
                }

                if (!args[args.length - 1].isEmpty()) {
                    for (String entry : complete) {
                        if (entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                            completeStarted.add(entry);
                        }
                    }
                    complete.clear();
                }
            } else if (args.length == 2) {
                switch (args[0]) {
                    case "join": {
                        //Get all teamnames
                    }
                    case "leave": {
                    }
                    default: {
                    }
                }
            }
        }

        return complete.isEmpty() ? completeStarted : complete;
    }
}