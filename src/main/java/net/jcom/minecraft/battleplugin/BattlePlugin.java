package net.jcom.minecraft.battleplugin;

import net.jcom.minecraft.battleplugin.commands.BattleCommand;
import net.jcom.minecraft.battleplugin.commands.tab.BattleTabComplete;
import net.jcom.minecraft.battleplugin.data.IsBattleGoingOn;
import net.jcom.minecraft.battleplugin.handler.LobbyHandler;
import net.jcom.minecraft.battleplugin.handler.PlayerCommandSendHandler;
import net.jcom.minecraft.battleplugin.handler.PreventBedHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BattlePlugin extends JavaPlugin {

    private static BattlePlugin plugin;


    @Override
    public void onEnable() {
        plugin = this;

        // Plugin startup logic
        Bukkit.getLogger().info("BattlePlugin - started!");

        IsBattleGoingOn.init();

        getCommand("battle").setExecutor(new BattleCommand());
        getCommand("battle").setTabCompleter(new BattleTabComplete());

        new LobbyHandler(this);
        new PlayerCommandSendHandler(this);
        new PreventBedHandler(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("BattlePlugin - shutdown!");
    }

    public static BattlePlugin getPlugin() {
        return plugin;
    }
}
