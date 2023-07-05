package net.jcom.minecraft.battleplugin.commands.tab;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BattleSpectateTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> complete = new ArrayList<>(), completeStarted = new ArrayList<>();

        if (sender instanceof Player player) {
            if (args.length == 1) {
                complete.addAll(Bukkit.getOnlinePlayers().stream().filter(player1 -> !player1.equals(player))
                        .filter(player1 -> player1.getGameMode() == GameMode.SURVIVAL).map(Player::getName).toList());

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
