package net.jcom.minecraft.battleplugin.apidata;

import org.bukkit.OfflinePlayer;

import java.util.HashMap;

public class SpectateData {
    public final HashMap<OfflinePlayer, OfflinePlayer> spectatorToTarget = new HashMap<>();

    public static final SpectateData EMPTY = new SpectateData();

    private SpectateData() {
    }
}
