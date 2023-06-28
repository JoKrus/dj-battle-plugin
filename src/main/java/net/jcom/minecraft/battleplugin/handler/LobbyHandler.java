package net.jcom.minecraft.battleplugin.handler;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.spigotmc.event.entity.EntityMountEvent;

public class LobbyHandler implements Listener {

    public LobbyHandler(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
        playerDeathEvent.getEntity().setGameMode(GameMode.ADVENTURE);
    }

    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent entityDamageEvent) {
        if (!(entityDamageEvent.getEntity() instanceof Player p)) {
            return;
        }

        if (p.getGameMode() == GameMode.ADVENTURE) {
            entityDamageEvent.setDamage(0);
        }
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent foodLevelChangeEvent) {
        if (!(foodLevelChangeEvent.getEntity() instanceof Player p)) {
            return;
        }

        if (p.getGameMode() == GameMode.ADVENTURE) {
            foodLevelChangeEvent.setCancelled(true);
            p.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onHorseMount(EntityMountEvent entityMountEvent) {
        if (!(entityMountEvent.getEntity() instanceof Player p)) {
            return;
        }

        if (p.getGameMode() == GameMode.ADVENTURE) {
            entityMountEvent.setCancelled(true);
        }
    }
}
