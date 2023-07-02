package net.jcom.minecraft.battleplugin.handler;

import net.jcom.minecraft.battleplugin.data.IsBattleGoingOn;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class PlayerCommandSendHandler implements Listener {
    public PlayerCommandSendHandler(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCommandSend(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        String cmd = playerCommandPreprocessEvent.getMessage().substring(1);
        if (IsBattleGoingOn.loadData()) {
            if (!cmd.startsWith("djbattle stop")) {
                playerCommandPreprocessEvent.setCancelled(true);
                playerCommandPreprocessEvent.getPlayer().sendMessage(ChatColor.RED + playerCommandPreprocessEvent.getMessage()
                        + " was stopped because a battle is going on.");
                Bukkit.getLogger().info(cmd + " was stopped because a battle is going on.");
            }
        } else {
            var forbiddenStrings = List.of("warp");

            if (!playerCommandPreprocessEvent.getPlayer().isOp()) {
                if (stringInList(forbiddenStrings, cmd.split(" ")[0])) {
                    playerCommandPreprocessEvent.setCancelled(true);
                    playerCommandPreprocessEvent.getPlayer().sendMessage(ChatColor.RED + playerCommandPreprocessEvent.getMessage()
                            + " was stopped because it contains forbidden command words.");
                    Bukkit.getLogger().info(cmd + " was stopped because it contains forbidden command words.");
                }
            }
        }
    }

    private static boolean stringInList(List<String> stringList, String s) {
        for (var sFromList : stringList) {
            if (sFromList.contains(s)) return true;
        }
        return false;
    }

}
