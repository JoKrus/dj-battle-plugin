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
                String teamName = StringUtils.join(arrList, " ");

                var success = TeamConfigSerializer.addEntry(teamName, p);

                if (!success) {
                    p.sendMessage(ChatColor.RED + "Joining the team failed. You are either in a team already or the " +
                            "team you tried to join was full.");
                    return false;
                }

                p.sendMessage("You joined \"" + teamName + "\" successfully.");
                var data = TeamConfigSerializer.loadData();

                for (var playersInTeam : data.biTeamToPlayers.get(teamName)) {
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

                var successPair = TeamConfigSerializer.removeEntry(p);

                if (!successPair.getLeft()) {
                    p.sendMessage(ChatColor.RED + "Leaving failed since you was not a member of a team anyway.");
                    return false;
                }

                var teamName = successPair.getRight();

                p.sendMessage("You left \"" + teamName + "\" successfully");

                var data = TeamConfigSerializer.loadData();

                if (data.biTeamToPlayers.get(teamName) != null) {
                    for (var playersInTeam : data.biTeamToPlayers.get(teamName)) {
                        if (playersInTeam.equals(p)) continue;
                        p.sendMessage(p.getName() + " just left your team!");
                    }
                }
                return true;
            }
            case "list" -> {
                var obj = TeamConfigSerializer.loadData();
                if (obj.biTeamToPlayers.size() == 0) {
                    p.sendMessage(ChatColor.RED + "No teams are currently registered!");
                }
                StringBuilder sb = new StringBuilder();

                sb.append(ChatColor.UNDERLINE).append(ChatColor.BOLD).append("List of all teams")
                        .append(ChatColor.RESET).append(System.lineSeparator()).append(System.lineSeparator());

                for (var team : obj.biTeamToPlayers.entrySet()) {
                    sb.append(ChatColor.UNDERLINE).append(team.getKey()).append(ChatColor.RESET).append(":");
                    for (var player : team.getValue()) {
                        sb.append(" ").append(player.getName()).append(",");
                    }
                    sb.replace(sb.length() - 1, sb.length(), System.lineSeparator());
                }
                p.sendMessage(sb.toString());
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
