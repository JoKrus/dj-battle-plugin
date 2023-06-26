package net.jcom.minecraft.battleplugin.commands;

import net.jcom.minecraft.battleplugin.BattlePlugin;
import net.jcom.minecraft.battleplugin.data.IsBattleGoingOn;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class BattleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Missing argument");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "start" -> {
                if (IsBattleGoingOn.loadData()) {
                    sender.sendMessage("Battle already going on.");
                    return false;
                }
                IsBattleGoingOn.saveData(true);

                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "effect give @a minecraft:saturation infinite 100 true");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        countDown();

                        List<String> cmds = List.of(
                                "time set 0",
                                "difficulty normal",
                                "gamemode survival @a",
                                "weather clear",
                                "effect clear @a"
                        );

                        Bukkit.getScheduler().runTask(BattlePlugin.getPlugin(), () -> {
                            for (var cmd : cmds) {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
                            }
                        });
                        Bukkit.broadcastMessage("Battle has started!");
                    }
                }.runTaskAsynchronously(BattlePlugin.getPlugin());

            }
            case "stop" -> {
                if (!IsBattleGoingOn.loadData()) {
                    sender.sendMessage("No battle present right now.");
                    return false;
                }
                IsBattleGoingOn.saveData(false);
                Bukkit.broadcastMessage("Battle was stopped!");
            }
        }

        return true;
    }

    private static void countDown() {
        Bukkit.broadcastMessage("Battle will start in 5...");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Bukkit.broadcastMessage("4...");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Bukkit.broadcastMessage("3...");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Bukkit.broadcastMessage("2...");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Bukkit.broadcastMessage("1...");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
