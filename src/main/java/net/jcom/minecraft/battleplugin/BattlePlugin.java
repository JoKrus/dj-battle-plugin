package net.jcom.minecraft.battleplugin;

import com.google.gson.Gson;
import net.jcom.minecraft.battleplugin.commands.BattleCommand;
import net.jcom.minecraft.battleplugin.commands.BattleSpectateCommand;
import net.jcom.minecraft.battleplugin.commands.BattleTeamCommand;
import net.jcom.minecraft.battleplugin.commands.tab.BattleSpectateTabComplete;
import net.jcom.minecraft.battleplugin.commands.tab.BattleTabComplete;
import net.jcom.minecraft.battleplugin.commands.tab.BattleTeamTabComplete;
import net.jcom.minecraft.battleplugin.config.Defaults;
import net.jcom.minecraft.battleplugin.config.DefaultsManager;
import net.jcom.minecraft.battleplugin.data.IsBattleGoingOn;
import net.jcom.minecraft.battleplugin.data.SpectateDataSerializer;
import net.jcom.minecraft.battleplugin.data.TeamConfigSerializer;
import net.jcom.minecraft.battleplugin.handler.*;
import net.jcom.minecraft.battleplugin.manager.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BattlePlugin extends JavaPlugin {

    private static BattlePlugin plugin;
    public static final Gson GSON = new Gson();

    @Override
    public void onEnable() {
        plugin = this;

        DefaultsManager.init(this, Defaults.class);

        // Plugin startup logic
        Bukkit.getLogger().info("BattlePlugin - started!");

        IsBattleGoingOn.init();
        TeamConfigSerializer.init();
        SpectateDataSerializer.init();

        SpectatorManager.init();

        getCommand("djbattle").setExecutor(new BattleCommand());
        getCommand("djbattle").setTabCompleter(new BattleTabComplete());

        getCommand("djteam").setExecutor(new BattleTeamCommand());
        getCommand("djteam").setTabCompleter(new BattleTeamTabComplete());

        getCommand("djspec").setExecutor(new BattleSpectateCommand());
        getCommand("djspec").setTabCompleter(new BattleSpectateTabComplete());

        new LobbyHandler(this);
        new PlayerCommandSendHandler(this);
        new PreventBedHandler(this);
        new BattleHandler(this);
        new ChatHandler(this);
        new BattleApiHandler(this);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            var console = Bukkit.getServer().getConsoleSender();
            String command = "djbattle init";
            Bukkit.dispatchCommand(console, command);
        }, 1);
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
