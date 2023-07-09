package net.jcom.minecraft.battleplugin.handler;

import net.jcom.minecraft.battleplugin.BattlePlugin;
import net.jcom.minecraft.battleplugin.apidata.TeamConfigWrapper;
import net.jcom.minecraft.battleplugin.data.DataUtils;
import net.jcom.minecraft.battleplugin.data.IsBattleGoingOn;
import net.jcom.minecraft.battleplugin.data.SpectateDataSerializer;
import net.jcom.minecraft.battleplugin.data.TeamConfigSerializer;
import net.jcom.minecraft.battleplugin.manager.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DataFlowIssue")
public class BattleHandler implements Listener {

    private final File battleData;

    public BattleHandler(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.battleData = Paths.get(plugin.getDataFolder().getAbsolutePath(), "respawnData.yml").toFile();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
        if (!IsBattleGoingOn.loadData()) {
            return;
        }
        var deathLocation = playerDeathEvent.getEntity().getLocation();
        DataUtils.setAndSave(battleData, playerDeathEvent.getEntity().getUniqueId() + ".location", deathLocation);


        //Update spectators to another player alive
        var teamData = TeamConfigSerializer.loadData();
        var team = TeamConfigWrapper.getPlayerToTeamMap(teamData).get(playerDeathEvent.getEntity());

        if (team == null) {
            //Not in a team, which is a faulty state but just continue then
            return;
        }

        var playersOfTeamAlive = getPlayersOfTeamAlive(teamData, team);
        var teamMatesOfDyingMember = getPlayersOfTeamSpec(teamData, team);

        if (playersOfTeamAlive.size() > 0) {
            var target = playersOfTeamAlive.get(0).getPlayer();
            for (var spectator : teamMatesOfDyingMember) {
                if (!SpectateDataSerializer.isSpectating(spectator.getPlayer(), playerDeathEvent.getEntity())) {
                    continue;
                }
                //when spectated the dead player, go to new player
                SpectateDataSerializer.setTarget(spectator.getPlayer(), target);
                SpectatorManager.tpAndSpectate(spectator.getPlayer(), target);
            }
        } else {
            for (var spectator : teamMatesOfDyingMember) {
                SpectateDataSerializer.removeTarget(spectator.getPlayer());
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent) {
        if (IsBattleGoingOn.loadData()) {
            playerRespawnEvent.getPlayer().setGameMode(GameMode.SPECTATOR);

            //spectate teammate or respawn at death location

            var teamData = TeamConfigSerializer.loadData();
            var team = TeamConfigWrapper.getPlayerToTeamMap(teamData).get(playerRespawnEvent.getPlayer());
            var playersOfTeamAlive = getPlayersOfTeamAlive(teamData, team);
            if (playersOfTeamAlive.size() > 0) {
                //https://github.com/CuzIm1Tigaaa/Spectator/blob/master/src/main/java/de/cuzim1tigaaa/spectator/player/SpectateManager.java
                var target = playersOfTeamAlive.get(0).getPlayer();
                playerRespawnEvent.setRespawnLocation(target.getLocation());

                //spectate after a brief delay
                Bukkit.getScheduler().scheduleSyncDelayedTask(BattlePlugin.getPlugin(), () -> {
                    SpectateDataSerializer.setTarget(playerRespawnEvent.getPlayer(), target);
                    SpectatorManager.tpAndSpectate(playerRespawnEvent.getPlayer(), target);
                }, 10);
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
        if (toggleSneakEvent.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            if (SpectateDataSerializer.isSpectating(toggleSneakEvent.getPlayer())) {
                var teamData = TeamConfigSerializer.loadData();
                var team = TeamConfigWrapper.getPlayerToTeamMap(teamData).get(toggleSneakEvent.getPlayer());
                var playersOfTeamAlive = getPlayersOfTeamAlive(teamData, team);
                if (playersOfTeamAlive.size() > 0) {
                    toggleSneakEvent.getPlayer().sendMessage(ChatColor.RED + "You can not leave your target when your" +
                            " team mates are still alive!");
                    toggleSneakEvent.setCancelled(true);
                } else {
                    SpectateDataSerializer.removeTarget(toggleSneakEvent.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onAnimation(PlayerAnimationEvent playerAnimationEvent) {
        if (playerAnimationEvent.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            if (playerAnimationEvent.getPlayer().getSpectatorTarget() != null) {
                playerAnimationEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        if (playerQuitEvent.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            playerQuitEvent.getPlayer().setHealth(0);
        }
    }

    @NotNull
    public static List<OfflinePlayer> getPlayersOfTeamAlive(TeamConfigWrapper teamData, String team) {
        var list = teamData.biTeamToPlayers.get(team);
        if (list == null) {
            return new ArrayList<>();
        } else {
            return list.stream().filter(offlinePlayer -> {
                if (offlinePlayer.isOnline()) {
                    var player = offlinePlayer.getPlayer();
                    return !player.isDead() && player.getGameMode() == GameMode.SURVIVAL;
                }
                return false;
            }).toList();
        }
    }

    @NotNull
    public static List<OfflinePlayer> getPlayersOfTeamSpec(TeamConfigWrapper teamData, String team) {
        var list = teamData.biTeamToPlayers.get(team);
        if (list == null) {
            return new ArrayList<>();
        } else {
            return list.stream().filter(offlinePlayer -> {
                if (offlinePlayer.isOnline()) {
                    var player = offlinePlayer.getPlayer();
                    return !player.isDead() && player.getGameMode() == GameMode.SPECTATOR;
                }
                return false;
            }).toList();
        }
    }
}
