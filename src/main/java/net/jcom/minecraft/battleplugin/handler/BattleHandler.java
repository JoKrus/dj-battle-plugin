package net.jcom.minecraft.battleplugin.handler;

import net.jcom.minecraft.battleplugin.apidata.TeamConfigWrapper;
import net.jcom.minecraft.battleplugin.data.DataUtils;
import net.jcom.minecraft.battleplugin.data.IsBattleGoingOn;
import net.jcom.minecraft.battleplugin.data.TeamConfigSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
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

            var teamData = TeamConfigSerializer.loadData();
            var team = TeamConfigWrapper.getPlayerToTeamMap(teamData).get(playerRespawnEvent.getPlayer());
            var playersOfTeamAlive =
                    teamData.biTeamToPlayers.get(team).stream().filter(offlinePlayer -> {
                        if (offlinePlayer.isOnline()) {
                            var player = offlinePlayer.getPlayer();
                            return !player.isDead() && player.getGameMode() == GameMode.SURVIVAL;
                        }
                        return false;
                    }).toList();
            if (playersOfTeamAlive.size() > 0) {
                //https://github.com/CuzIm1Tigaaa/Spectator/blob/master/src/main/java/de/cuzim1tigaaa/spectator/player/SpectateManager.java
                //TODO make permanent (reapply every x secs) and update on each death etc, also make changeable.
                playerRespawnEvent.getPlayer().setSpectatorTarget(playersOfTeamAlive.get(0).getPlayer());
            } else {
                //No team member left
                FileConfiguration config = YamlConfiguration.loadConfiguration(battleData);
                var loc = config.getLocation(playerRespawnEvent.getPlayer().getUniqueId() + ".location");
                if (loc != null) {
                    playerRespawnEvent.setRespawnLocation(loc);
                }
            }
        }

        DataUtils.setAndSave(battleData, playerRespawnEvent.getPlayer().getUniqueId() + ".location", null);

    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent toggleSneakEvent) {

    }
}
