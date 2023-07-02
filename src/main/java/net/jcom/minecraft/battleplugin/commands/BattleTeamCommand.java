package net.jcom.minecraft.battleplugin.commands;

import net.jcom.minecraft.battleplugin.data.IsBattleGoingOn;
import net.jcom.minecraft.battleplugin.data.TeamConfigSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class BattleTeamCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return false;
        }


        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Missing argument.");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "join" -> {
                if (IsBattleGoingOn.loadData()) {
                    sender.sendMessage(ChatColor.RED + "You can't join a team during a battle.");
                    return false;
                }
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.RED + "Missing argument.");
                    return false;
                }
                var arrList = new ArrayList<>(Arrays.stream(args).toList());
                arrList.remove(0);
                String teamname = StringUtils.join(arrList, " ");
                var success = TeamConfigSerializer.addEntry(teamname, p);

                if (!success) {
                    p.sendMessage(ChatColor.RED + "Joining the team failed. You are either in a team already or the " +
                            "team you tried to join was full.");
                    return false;
                }

                p.sendMessage("You joined \"" + teamname + "\" successfully.");
                var data = TeamConfigSerializer.loadData();

                for (var playersInTeam : data.biTeamToPlayers.get(teamname)) {
                    if (playersInTeam.equals(p)) continue;
                    p.sendMessage(p.getName() + " just joined your team!");
                }
                return true;
            }
            case "leave" -> {
                if (IsBattleGoingOn.loadData()) {
                    sender.sendMessage(ChatColor.RED + "You can't leave a team during a battle.");
                    return false;
                }
                return true;
            }
            case "list" -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
