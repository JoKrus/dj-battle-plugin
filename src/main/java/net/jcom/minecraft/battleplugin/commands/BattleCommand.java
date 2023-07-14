package net.jcom.minecraft.battleplugin.commands;

import net.jcom.minecraft.battleplugin.BattlePlugin;
import net.jcom.minecraft.battleplugin.apidata.TeamConfigWrapper;
import net.jcom.minecraft.battleplugin.config.Defaults;
import net.jcom.minecraft.battleplugin.config.DefaultsManager;
import net.jcom.minecraft.battleplugin.data.IsBattleGoingOn;
import net.jcom.minecraft.battleplugin.data.SpectateDataSerializer;
import net.jcom.minecraft.battleplugin.data.TeamConfigSerializer;
import net.jcom.minecraft.battleplugin.handler.GracePeriodHandler;
import net.jcom.minecraft.battleplugin.manager.SpectatorManager;
import net.jcom.minecraft.battleplugineventapi.event.BattleStartedEvent;
import net.jcom.minecraft.battleplugineventapi.event.BattleStoppedEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BattleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Missing argument");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "start" -> {
                if (IsBattleGoingOn.loadData()) {
                    sender.sendMessage(ChatColor.RED + "Battle already going on.");
                    return false;
                }
                IsBattleGoingOn.saveData(true, true);

                //Remove offline players, create teams for solo players
                correctTeamData();

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        countDownBattle(DefaultsManager.getValue(Defaults.BATTLE_START));
                        List<String> cmds = List.of(
                                "time set 0",
                                "weather clear",
                                "effect clear @a",
                                "clear @a",
                                "difficulty normal",
                                "give @a minecraft:bread 10",
                                "experience set @a 0",
                                "worldborder center " + getXZLoc(DefaultsManager.getValue(Defaults.BATTLE_LOCATION)),
                                "worldborder set " + DefaultsManager.getValue(Defaults.WORLD_BORDER_INIT_WIDTH) + " 0",
                                "worldborder set " + DefaultsManager.getValue(Defaults.WORLD_BORDER_END_WIDTH) + " " +
                                        DefaultsManager.getValue(Defaults.BATTLE_DURATION),
                                "tp @a " + DefaultsManager.getValue(Defaults.BATTLE_LOCATION),
                                "gamemode survival @a"
                        );

                        Bukkit.getScheduler().runTask(BattlePlugin.getPlugin(), () -> {
                            if (!IsBattleGoingOn.loadData()) {
                                return;
                            }
                            for (var cmd : cmds) {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
                            }

                            for (var p : Bukkit.getOnlinePlayers().toArray(new Player[0])) {
                                p.setSaturation(5);
                                p.setFoodLevel(20);
                                p.setHealth(20);
                            }
                        });

                        if (!IsBattleGoingOn.loadData()) {
                            return;
                        }
                        Bukkit.broadcastMessage("Battle has started!");

                        IsBattleGoingOn.saveData(true, false);
                        SpectatorManager.start();

                        var gracePeriod = new GracePeriodHandler(BattlePlugin.getPlugin());

                        Bukkit.getScheduler().runTask(BattlePlugin.getPlugin(), () -> {
                            var config = TeamConfigWrapper.toTeamConfig(TeamConfigSerializer.loadData());
                            Bukkit.getPluginManager().callEvent(new BattleStartedEvent(config));
                        });

                        countDownGrace(DefaultsManager.getValue(Defaults.GRACE_PERIOD));

                        HandlerList.unregisterAll(gracePeriod);
                    }
                }.runTaskAsynchronously(BattlePlugin.getPlugin());
            }
            case "stop" -> {
                if (!IsBattleGoingOn.loadData()) {
                    sender.sendMessage(ChatColor.RED + "No battle present right now.");
                    return false;
                }

                boolean wasTimer = IsBattleGoingOn.loadDataWithTimer().isTimer.get();
                IsBattleGoingOn.saveData(false, false);

                //check if only one teams players are alive

                var teamData = TeamConfigSerializer.loadData();

                var pToTeam = TeamConfigWrapper.getPlayerToTeamMap(teamData);

                Set<String> teamNames = new HashSet<>();

                for (var pl : Bukkit.getOnlinePlayers().stream()
                        .filter(player -> player.getGameMode() == GameMode.SURVIVAL)
                        .toList()) {
                    var team = pToTeam.get(pl);
                    if (team == null) {
                        team = pl.getName();
                    }
                    teamNames.add(team);
                }

                var battleStoppedEvent = new BattleStoppedEvent();

                var winner = "";
                if (teamNames.size() == 1) {
                    winner = teamNames.stream().findFirst().orElse(null);
                    battleStoppedEvent = new BattleStoppedEvent(winner);
                }

                List<String> cmds = List.of(
                        "time set 0",
                        "difficulty peaceful",
                        "gamemode adventure @a",
                        "effect clear @a",
                        "clear @a",
                        "worldborder center " + getXZLoc(DefaultsManager.getValue(Defaults.LOBBY_LOCATION)),
                        "worldborder set " + DefaultsManager.getValue(Defaults.WORLD_BORDER_LOBBY_WIDTH) + " 0",
                        "tp @a " + DefaultsManager.getValue(Defaults.LOBBY_LOCATION)
                );

                if (!wasTimer) {
                    for (var cmd : cmds) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    }
                }

                Bukkit.broadcastMessage("Battle was stopped!");
                if (teamNames.size() == 1) {
                    Bukkit.broadcastMessage(ChatColor.AQUA + winner + " has won the battle! Congratulations!");
                }
                SpectatorManager.stop();
                SpectateDataSerializer.clear();

                Bukkit.getPluginManager().callEvent(battleStoppedEvent);
            }
            case "init" -> {
                List<String> cmds = List.of(
                        "gamerule sendCommandFeedback true",
                        "defaultgamemode adventure",
                        "difficulty peaceful",
                        "gamerule doInsomnia false",
                        "gamerule doTraderSpawning false",
                        "gamerule logAdminCommands false",
                        "gamerule commandBlockOutput false",
                        "gamerule doWeatherCycle false",
                        "gamerule doPatrolSpawning false",
                        "gamerule disableRaids true",
                        "setworldspawn " + DefaultsManager.getValue(Defaults.LOBBY_LOCATION),
                        "worldborder center " + getXZLoc(DefaultsManager.getValue(Defaults.LOBBY_LOCATION)),
                        "worldborder set " + DefaultsManager.getValue(Defaults.WORLD_BORDER_LOBBY_WIDTH) + " 0"
                );

                for (var cmd : cmds) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }

                sender.sendMessage("Battle settings initialized");
            }
            case "reload" -> {
                BattlePlugin.getPlugin().reloadConfig();
                sender.sendMessage("Config reloaded!");
            }
        }

        return true;
    }

    private static void correctTeamData() {
        var configWrapped = TeamConfigSerializer.loadData();
        var playerMap = TeamConfigWrapper.getPlayerToTeamMap(configWrapped);

        var offlineTeamPlayers = playerMap.keySet().stream()
                .filter(offlinePlayer -> !offlinePlayer.isOnline())
                .toList();

        for (var pl : offlineTeamPlayers) {
            TeamConfigSerializer.removeEntry(pl);
        }

        var toAddPlayers = Bukkit.getOnlinePlayers().stream()
                .filter(player -> !playerMap.containsKey(player))
                .map(player -> Pair.of(player.getName(), player))
                .toList();

        for (var pair : toAddPlayers) {
            TeamConfigSerializer.addEntry(pair.getLeft(), pair.getRight());
        }
    }

    private String getXZLoc(String loc) {
        if (loc == null)
            return "0 0";

        var arr = loc.split("\\s+");
        return arr[0] + " " + arr[2];
    }

    private static FileConfiguration getConfig() {
        return BattlePlugin.getPlugin().getConfig();
    }

    private static void countDownGrace(int sec) {
        if (sec < 1) return;

        Bukkit.broadcastMessage(sec + " second grace period started!");

        if (!IsBattleGoingOn.loadData()) {
            return;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (int i = sec - 1; i >= 1; --i) {
            if (!IsBattleGoingOn.loadData()) {
                return;
            }
            if (i > 20) {
                if (i % 10 == 0) {
                    Bukkit.broadcastMessage(i + " seconds until grace period ends!");
                }
            } else if (i > 5) {
                if (i % 5 == 0) {
                    Bukkit.broadcastMessage(i + " seconds until grace period ends!");
                }
            } else {
                Bukkit.broadcastMessage(i + "...");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (!IsBattleGoingOn.loadData()) {
            return;
        }
        Bukkit.broadcastMessage("Fighting begins!");
    }

    private static void countDownBattle(int secs) {
        Bukkit.broadcastMessage("Battle will start in");

        for (int i = secs; i >= 1; --i) {
            if (!IsBattleGoingOn.loadData()) {
                return;
            }

            Bukkit.broadcastMessage(i + "...");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
