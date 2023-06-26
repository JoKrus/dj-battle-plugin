package net.jcom.minecraft.battleplugin.handler;

import net.jcom.minecraft.battleplugin.BattlePlugin;
import net.jcom.minecraft.battleplugin.data.IsBattleGoingOn;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

public class PlayerCommandSendHandler implements Listener {
    public PlayerCommandSendHandler(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCommandSend(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        if (IsBattleGoingOn.loadData()) {
            Bukkit.getLogger().info("Battle is going on");
            String cmd = playerCommandPreprocessEvent.getMessage().substring(1);
            if (!cmd.startsWith("battle stop")) {
                playerCommandPreprocessEvent.setCancelled(true);
                Bukkit.getLogger().info(cmd + " was stopped because a battle is going on.");
            }
        }
    }
}
