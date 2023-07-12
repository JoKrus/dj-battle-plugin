package net.jcom.minecraft.battleplugin.manager;

import net.jcom.minecraft.battleplugin.BattlePlugin;
import net.jcom.minecraft.battleplugin.data.IsBattleGoingOn;
import net.jcom.minecraft.battleplugin.data.SpectateDataSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SpectatorManager {
    // tp every spectate data player to target every x ticks

    public static BukkitTask task = null;

    public static void init() {
        if (IsBattleGoingOn.loadData()) {
            start();
        }
    }

    public static void start() {
        int[] counter = {0};
        task = new BukkitRunnable() {
            @Override
            public void run() {
                counter[0] = counter[0] + 1;
                counter[0] = counter[0] % 30;


                var obj = SpectateDataSerializer.loadData();

                Bukkit.getScheduler().runTask(BattlePlugin.getPlugin(), () -> {

                    if (counter[0] == 0) {
                        for (var pl : Bukkit.getOnlinePlayers().stream()
                                .filter(player -> player.getGameMode() == GameMode.SPECTATOR)
                                .toList()) {
                            pl.sendMessage("You can change your spectate target via /djspec <PlayerName>");
                        }
                    }

                    for (var entry : obj.spectatorToTarget.entrySet()) {
                        var specOff = entry.getKey();
                        var targOff = entry.getValue();
                        if (!specOff.isOnline() || !targOff.isOnline()) {
                            continue;
                        }

                        var spec = specOff.getPlayer();
                        var targ = targOff.getPlayer();

                        if (spec != null && spec.getGameMode() == GameMode.SPECTATOR && targ != null &&
                                targ.getGameMode() == GameMode.SURVIVAL) {
                            tpAndSpectate(spec, targ);
                        }
                    }
                });
            }
        }.runTaskTimerAsynchronously(BattlePlugin.getPlugin(), 0, 40);
    }

    public static void stop() {
        if (task != null) {
            task.cancel();
        }
    }

    public static void tpAndSpectate(Player spec, Player target) {
        spec.teleport(target);
        if (spec.getSpectatorTarget() == null || !spec.getSpectatorTarget().equals(target)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(BattlePlugin.getPlugin(), () -> {
                spec.setSpectatorTarget(target);
            }, 8);
        }
    }

    //battle stop clears file
}
