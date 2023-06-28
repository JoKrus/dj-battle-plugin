package net.jcom.minecraft.battleplugin.handler;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.plugin.Plugin;
import org.spigotmc.event.entity.EntityMountEvent;

public class LobbyHandler implements Listener {

    public LobbyHandler(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
        if (!(entityMountEvent.getMount() instanceof Player p)) {
            return;
        }

        if (p.getGameMode() == GameMode.ADVENTURE) {
            entityMountEvent.setCancelled(true);
        }
    }


    @EventHandler
    public void frameDrop(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) {
            return;
        }

        if (p.getGameMode() == GameMode.ADVENTURE) {
            if (e.getEntity() instanceof ItemFrame) {
                if (e.getDamager() instanceof Player) {
                    e.setCancelled(true);

                }
                if (e.getDamager() instanceof Projectile) {
                    if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
                        e.getDamager().remove();
                        e.setCancelled(true);
                    }
                }
            }
        }
    }


    @EventHandler
    public void frameRotate(PlayerInteractEntityEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            if (e.getRightClicked().getType().equals(EntityType.ITEM_FRAME)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onUnleash(PlayerUnleashEntityEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            e.setCancelled(true);
        }
    }
}
