package net.jcom.minecraft.battleplugin.handler;

import net.jcom.minecraft.battleplugineventapi.event.BattleStartedEvent;
import net.jcom.minecraft.battleplugineventapi.event.BattleStoppedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class BattleApiHandler implements Listener {

    public BattleApiHandler(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBattleStart(BattleStartedEvent event) {
        Bukkit.getLogger().info("BattleStartedEvent received");
    }

    @EventHandler
    public void onBattleStop(BattleStoppedEvent event) {
        Bukkit.getLogger().info("BattleStoppedEvent received");
        if (event.hasWinner()) {
            Bukkit.getLogger().info(event.getWinnerTeam() + " won!");
        }
    }
}


