package net.jcom.minecraft.battleplugin.apidata;

import com.google.common.collect.HashBiMap;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class TeamConfigWrapper extends TeamConfig {
    public final HashBiMap<String, List<Player>> biTeamToPlayers = HashBiMap.create();

    public static TeamConfigWrapper ofTeamConfig(TeamConfig teamConfig) {
        var wrapper = new TeamConfigWrapper();
        wrapper.biTeamToPlayers.putAll(teamConfig.teamToPlayers);
        return wrapper;
    }

    public static TeamConfig toTeamConfig(TeamConfigWrapper teamConfigWrapper) {
        TeamConfig teamConfig = new TeamConfig();
        teamConfig.teamToPlayers.putAll(teamConfigWrapper.biTeamToPlayers);
        return teamConfig;
    }

    public static HashMap<Player, String> getPlayerToTeamMap(TeamConfigWrapper teamConfig) {
        HashMap<Player, String> ret = new HashMap<>();

        for (var entry : teamConfig.biTeamToPlayers.inverse().entrySet()) {
            for (var ply : entry.getKey()) {
                ret.put(ply, entry.getValue());
            }
        }

        return ret;
    }
}
