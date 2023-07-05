package net.jcom.minecraft.battleplugin.manager;

import net.jcom.minecraft.battleplugin.BattlePlugin;
import net.jcom.minecraft.battleplugin.data.IsBattleGoingOn;
import net.jcom.minecraft.battleplugin.data.SpectateDataSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SpectatorManager {
    // tp every spectate data player to target every x ticks

    public static void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!IsBattleGoingOn.loadData()) {
                    return;
                }

                var obj = SpectateDataSerializer.loadData();

                List<Pair<Player, Player>> toSpec = new ArrayList<>();

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
                        toSpec.add(Pair.of(spec, targ));
                    }
                }

                Bukkit.getScheduler().runTask(BattlePlugin.getPlugin(), () ->
                        toSpec.forEach(pair -> tpAndSpectate(pair.getLeft(), pair.getRight())));
            }
        }.runTaskTimerAsynchronously(BattlePlugin.getPlugin(), 0, 20);
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
