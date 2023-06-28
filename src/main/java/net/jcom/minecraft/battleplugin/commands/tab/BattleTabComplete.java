package net.jcom.minecraft.battleplugin.commands.tab;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class BattleTabComplete implements TabCompleter {


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> complete = new ArrayList<>(), completeStarted = new ArrayList<>();

        if (sender instanceof Player player) {

            if (args.length == 1) {

                if (player.hasPermission("battleplugin.battle.start")) {
                    complete.add("start");
                }

                if (player.hasPermission("battleplugin.battle.stop")) {
                    complete.add("stop");
                }

                if (player.hasPermission("battleplugin.battle.reload")) {
                    complete.add("reload");
                }

                if (player.hasPermission("battleplugin.battle.init")) {
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
            }
        }

        return complete.isEmpty() ? completeStarted : complete;
    }
}