package net.jcom.minecraft.battleplugin.handler;

import net.jcom.minecraft.battleplugin.data.DataUtils;
import net.jcom.minecraft.battleplugin.data.IsBattleGoingOn;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Paths;

@SuppressWarnings("DataFlowIssue")
public class BattleHandler implements Listener {

    private final File battleData;

    public BattleHandler(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.battleData = Paths.get(plugin.getDataFolder().getAbsolutePath(), "respawnData.yml").toFile();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
        if (IsBattleGoingOn.loadData()) {
            var deathLocation = playerDeathEvent.getEntity().getLocation();
            DataUtils.setAndSave(battleData, playerDeathEvent.getEntity().getUniqueId() + ".location", deathLocation);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent) {
        if (IsBattleGoingOn.loadData()) {
            playerRespawnEvent.getPlayer().setGameMode(GameMode.SPECTATOR);


            //TODO only allow spectate of alive teammates in survivor mode
            // if all of team is dead, else into a player


            FileConfiguration config = YamlConfiguration.loadConfiguration(battleData);
            var loc = config.getLocation(playerRespawnEvent.getPlayer().getUniqueId() + ".location");
            if (loc != null) {
                playerRespawnEvent.setRespawnLocation(loc);
            }
        }

        DataUtils.setAndSave(battleData, playerRespawnEvent.getPlayer().getUniqueId() + ".location", null);

    }
}
