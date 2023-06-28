package net.jcom.minecraft.battleplugin.handler;

import net.jcom.minecraft.battleplugineventapi.event.BattleStartedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class BattleHandler implements Listener {
    public BattleHandler(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBattleStart(BattleStartedEvent bse) {
        Bukkit.broadcastMessage("Received StartEvent");
    }
}
