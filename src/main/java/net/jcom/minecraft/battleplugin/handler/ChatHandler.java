package net.jcom.minecraft.battleplugin.handler;

import net.jcom.minecraft.battleplugin.BattlePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

public class ChatHandler implements Listener {

    public ChatHandler(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SPECTATOR) {
            return;
        }
        event.setCancelled(true);
        //If spectator, only sent to spectators
        String message =
                ChatColor.RED + "[DEAD]" + ChatColor.RESET + " <" + event.getPlayer().getName() + "> " + event.getMessage();

        Bukkit.getScheduler().runTask(BattlePlugin.getPlugin(), () -> {
            for (var pl : Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getGameMode() == GameMode.SPECTATOR)
                    .toList()) {
                pl.sendMessage(message);
            }
        });
    }
}
