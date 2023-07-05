package net.jcom.minecraft.battleplugin.commands;

import net.jcom.minecraft.battleplugin.BattlePlugin;
import net.jcom.minecraft.battleplugin.Defaults;
import net.jcom.minecraft.battleplugin.apidata.TeamConfigWrapper;
import net.jcom.minecraft.battleplugin.data.IsBattleGoingOn;
import net.jcom.minecraft.battleplugin.data.SpectateDataSerializer;
import net.jcom.minecraft.battleplugin.data.TeamConfigSerializer;
import net.jcom.minecraft.battleplugin.handler.GracePeriodHandler;
import net.jcom.minecraft.battleplugineventapi.event.BattleStartedEvent;
import net.jcom.minecraft.battleplugineventapi.event.BattleStoppedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        countDownBattle(getConfig().getInt(Defaults.BATTLE_START_KEY));

                        List<String> cmds = List.of(
                                "time set 0",
                                "weather clear",
                                "effect clear @a",
                                "clear @a",
                                "difficulty normal",
                                "give @a minecraft:bread 10",
                                "experience set @a 0",
                                "worldborder center " + getXZLoc(getConfig().getString(Defaults.BATTLE_LOCATION_KEY)),
                                "worldborder set " + getConfig().getInt(Defaults.WORLD_BORDER_INIT_WIDTH_KEY) + " 0",
                                "worldborder set " + getConfig().getInt(Defaults.WORLD_BORDER_END_WIDTH_KEY) + " " +
                                        getConfig().getInt(Defaults.BATTLE_DURATION_KEY),
                                "tp @a " + getConfig().getString(Defaults.BATTLE_LOCATION_KEY),
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

                        var gracePeriod = new GracePeriodHandler(BattlePlugin.getPlugin());

                        Bukkit.getScheduler().runTask(BattlePlugin.getPlugin(), () -> {
                            var config = TeamConfigWrapper.toTeamConfig(TeamConfigSerializer.loadData());
                            Bukkit.getPluginManager().callEvent(new BattleStartedEvent(config));
                        });

                        countDownGrace(getConfig().getInt(Defaults.GRACE_PERIOD_KEY));

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

                List<String> cmds = List.of(
                        "time set 0",
                        "difficulty peaceful",
                        "gamemode adventure @a",
                        "effect clear @a",
                        "clear @a",
                        "worldborder center " + getXZLoc(getConfig().getString(Defaults.LOBBY_LOCATION_KEY)),
                        "worldborder set " + getConfig().getInt(Defaults.WORLD_BORDER_LOBBY_WIDTH_KEY) + " 0",
                        "tp @a " + getConfig().getString(Defaults.LOBBY_LOCATION_KEY)
                );

                if (!wasTimer) {
                    for (var cmd : cmds) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    }
                }

                Bukkit.broadcastMessage("Battle was stopped!");

                SpectateDataSerializer.clear();

                Bukkit.getPluginManager().callEvent(new BattleStoppedEvent());
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
                        "setworldspawn " + getConfig().getString(Defaults.LOBBY_LOCATION_KEY),
                        "worldborder center " + getXZLoc(getConfig().getString(Defaults.LOBBY_LOCATION_KEY)),
                        "worldborder set " + getConfig().getInt(Defaults.WORLD_BORDER_LOBBY_WIDTH_KEY) + " 0"
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
