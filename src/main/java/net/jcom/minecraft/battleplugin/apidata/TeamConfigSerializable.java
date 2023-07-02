package net.jcom.minecraft.battleplugin.apidata;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeamConfigSerializable {
    private HashMap<String, List<UUID>> teamToPlayers = new HashMap<>();

    private TeamConfigSerializable() {
    }

    public static TeamConfig toTeamConfig(TeamConfigSerializable serializable) {
        TeamConfig teamConfig = new TeamConfig();
        for (var entry : serializable.teamToPlayers.entrySet()) {
            var list = new ArrayList<Player>();
            for (UUID playerId : entry.getValue()) {
                list.add(Bukkit.getPlayer(playerId));
            }
            teamConfig.teamToPlayers.put(entry.getKey(), list);
        }
        return teamConfig;
    }

    public static TeamConfigSerializable fromTeamConfig(TeamConfig teamConfig) {
        TeamConfigSerializable serializable = new TeamConfigSerializable();
        for (var entry : teamConfig.teamToPlayers.entrySet()) {
            var list = new ArrayList<UUID>();
            for (Player player : entry.getValue()) {
                list.add(player.getUniqueId());
            }
            serializable.teamToPlayers.put(entry.getKey(), list);
        }
        return serializable;
    }
}
