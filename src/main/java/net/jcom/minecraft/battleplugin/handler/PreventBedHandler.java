package net.jcom.minecraft.battleplugin.handler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.data.type.Bed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

public class PreventBedHandler implements Listener {

    public PreventBedHandler(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void preventBedPlaced(BlockPlaceEvent blockPlaceEvent) {
        if (blockPlaceEvent.getBlockPlaced().getBlockData() instanceof Bed) {
            blockPlaceEvent.setCancelled(true);
            blockPlaceEvent.getPlayer().sendMessage(ChatColor.RED + "Beds are not allowed to be placed.");
        }
    }
}
