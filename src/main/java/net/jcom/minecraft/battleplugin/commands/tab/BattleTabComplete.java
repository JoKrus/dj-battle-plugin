package net.jcom.minecraft.battleplugin.commands.tab;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class BattleTabComplete implements TabCompleter {


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> complete = new ArrayList<>(), completeStarted = new ArrayList<>();

        if (!(sender instanceof Player player)) {
            return completeStarted;
        }

        if (args.length == 1) {

            if (player.hasPermission("battle-plugin.battle.start")) {
                complete.add("start");
            }

            if (player.hasPermission("battle-plugin.battle.stop")) {
                complete.add("stop");
            }

            if (player.hasPermission("battle-plugin.battle.reload")) {
                complete.add("reload");
            }

            if (player.hasPermission("battle-plugin.battle.init")) {
                complete.add("init");
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
                case "stop": {
                    //Get all teamnames
                    complete.addAll(List.of("true", "false"));

                    if (!args[args.length - 1].isEmpty()) {
                        for (String entry : complete) {
                            if (entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                                completeStarted.add(entry);
                            }
                        }
                        complete.clear();
                    }
                }
                case "start": {
                    complete.add("name");
                }
                default: {
                }
            }
        } else if (args.length == 3) {
            switch (args[0]) {
                case "start": {
                    complete.add("category");
                }
                case "stop": {
                }
                default: {
                }
            }
        }

        return complete.isEmpty() ? completeStarted : complete;
    }
}