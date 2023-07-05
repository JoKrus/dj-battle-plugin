package net.jcom.minecraft.battleplugin.commands;

import net.jcom.minecraft.battleplugin.apidata.TeamConfigWrapper;
import net.jcom.minecraft.battleplugin.data.SpectateDataSerializer;
import net.jcom.minecraft.battleplugin.data.TeamConfigSerializer;
import net.jcom.minecraft.battleplugin.handler.BattleHandler;
import net.jcom.minecraft.battleplugin.manager.SpectatorManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class BattleSpectateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        //djspec Playername
        if (!(sender instanceof Player p)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return false;
        }
        if (p.getGameMode() != GameMode.SPECTATOR) {
            sender.sendMessage(ChatColor.RED + "Only usable in SpectatorMode.");
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "You need to provide a player to spectate.");
            return false;
        }

        var arrList = new ArrayList<>(Arrays.stream(args).toList());
        arrList.remove(0);
        String playerName = StringUtils.join(arrList, " ");

        var target = Bukkit.getPlayer(playerName);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Target could not be found.");
            return false;
        }
        if (target.getGameMode() != GameMode.SURVIVAL) {
            sender.sendMessage(ChatColor.RED + "Your target has to be in survival mode.");
            return false;
        }
        var teamData = TeamConfigSerializer.loadData();
        var pTTM = TeamConfigWrapper.getPlayerToTeamMap(teamData);
        var team = pTTM.get(p);
        var targetTeam = pTTM.get(target);
        if (!targetTeam.equals(team)) {
            var playersOfTeamAlive = BattleHandler.getPlayersOfTeamAlive(teamData, team);
            if (playersOfTeamAlive.size() > 0) {
                sender.sendMessage(ChatColor.RED + "You can only spectate your team until your team is out of the " +
                        "battle.");
                return false;
            }
        }

        SpectateDataSerializer.setTarget(p, target);
        SpectatorManager.tpAndSpectate(p, target);

        sender.sendMessage("You are now spectating " + target.getName());

        return true;
    }
}
