package net.jcom.minecraft.battleplugin;

import com.google.gson.Gson;
import net.jcom.minecraft.battleplugin.commands.BattleCommand;
import net.jcom.minecraft.battleplugin.commands.BattleSpectateCommand;
import net.jcom.minecraft.battleplugin.commands.BattleTeamCommand;
import net.jcom.minecraft.battleplugin.commands.tab.BattleSpectateTabComplete;
import net.jcom.minecraft.battleplugin.commands.tab.BattleTabComplete;
import net.jcom.minecraft.battleplugin.commands.tab.BattleTeamTabComplete;
import net.jcom.minecraft.battleplugin.data.IsBattleGoingOn;
import net.jcom.minecraft.battleplugin.data.SpectateDataSerializer;
import net.jcom.minecraft.battleplugin.data.TeamConfigSerializer;
import net.jcom.minecraft.battleplugin.handler.BattleHandler;
import net.jcom.minecraft.battleplugin.handler.LobbyHandler;
import net.jcom.minecraft.battleplugin.handler.PlayerCommandSendHandler;
import net.jcom.minecraft.battleplugin.handler.PreventBedHandler;
import net.jcom.minecraft.battleplugin.manager.SpectatorManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BattlePlugin extends JavaPlugin {

    private static BattlePlugin plugin;
    public static final Gson GSON = new Gson();

    @Override
    public void onEnable() {
        plugin = this;

        getConfig().addDefault(Defaults.GRACE_PERIOD_KEY, Defaults.GRACE_PERIOD_VALUE);
        getConfig().addDefault(Defaults.BATTLE_START_KEY, Defaults.BATTLE_START_VALUE);
        getConfig().addDefault(Defaults.BATTLE_DURATION_KEY, Defaults.BATTLE_DURATION_VALUE);
        getConfig().addDefault(Defaults.BATTLE_LOCATION_KEY, Defaults.BATTLE_LOCATION_VALUE);
        getConfig().addDefault(Defaults.LOBBY_LOCATION_KEY, Defaults.LOBBY_LOCATION_VALUE);
        getConfig().addDefault(Defaults.WORLD_BORDER_INIT_WIDTH_KEY, Defaults.WORLD_BORDER_INIT_WIDTH_VALUE);
        getConfig().addDefault(Defaults.WORLD_BORDER_END_WIDTH_KEY, Defaults.WORLD_BORDER_END_WIDTH_VALUE);
        getConfig().addDefault(Defaults.WORLD_BORDER_LOBBY_WIDTH_KEY, Defaults.WORLD_BORDER_LOBBY_WIDTH_VALUE);
        getConfig().addDefault(Defaults.TEAM_SIZE_KEY, Defaults.TEAM_SIZE_VALUE);


        getConfig().options().copyDefaults(true);
        saveConfig();

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
