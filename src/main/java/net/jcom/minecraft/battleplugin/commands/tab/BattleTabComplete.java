package net.jcom.minecraft.battleplugin.commands.tab;


import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;

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

    public List<String> onTabComplete2(CommandSender Sender, Command Command, String Label, String[] Args) {

        List<String> complete = new ArrayList<>(), completeStarted = new ArrayList<>();

        if (Sender instanceof Player) {

            if (Args.length == 1) {

                //if(GPM.getPManager().hasPermission(Sender, "SitToggle") && !GPM.getCManager().S_SITMATERIALS
                // .isEmpty()) complete.add("toggle");

                // if(GPM.getPManager().hasPermission(Sender, "PlayerSitToggle") && GPM.getCManager().PS_ALLOW_SIT)
                // complete.add("playertoggle");

                if (!Args[Args.length - 1].isEmpty()) {

                    for (String entry : complete)
                        if (entry.toLowerCase().startsWith(Args[Args.length - 1].toLowerCase()))
                            completeStarted.add(entry);

                    complete.clear();
                }
            }
        }

        return complete.isEmpty() ? completeStarted : complete;
    }

}