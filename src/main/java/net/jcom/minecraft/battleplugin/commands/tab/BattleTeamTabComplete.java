package net.jcom.minecraft.battleplugin.commands.tab;


import net.jcom.minecraft.battleplugin.data.TeamConfigSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BattleTeamTabComplete implements TabCompleter {


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> complete = new ArrayList<>(), completeStarted = new ArrayList<>();

        if (sender instanceof Player player) {

            if (args.length == 1) {
                if (player.hasPermission("battle-plugin.team.join")) {
                    complete.add("join");
                }

                if (player.hasPermission("battle-plugin.team.leave")) {
                    complete.add("leave");
                }

                if (player.hasPermission("battle-plugin.team.list")) {
                    complete.add("list");
                }

                if (player.hasPermission("battle-plugin.team.test")) {
                    complete.add("test");
                }

                if (player.hasPermission("battle-plugin.team.remove")) {
                    complete.add("remove");
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
                        teamMateAdding(args, complete, completeStarted);
                    }
                    case "remove": {
                        teamMateAdding(args, complete, completeStarted);
                    }
                    default: {
                    }
                }
            }
        }

        return complete.isEmpty() ? completeStarted : complete;
    }

    private static void teamMateAdding(String[] args, List<String> complete, List<String> completeStarted) {
        //Get all teamnames
        complete.addAll(TeamConfigSerializer.loadData().biTeamToPlayers.keySet());
        complete.sort(String::compareToIgnoreCase);

        var arrList = new ArrayList<>(Arrays.stream(args).toList());
        arrList.remove(0);
        String myArgTilNow = StringUtils.join(arrList, " ");

        //update since space is okay
        if (!myArgTilNow.isEmpty()) {
            for (String entry : complete) {
                if (entry.startsWith(myArgTilNow)) {
                    completeStarted.add(entry);
                }
            }
            complete.clear();
        }
    }
}