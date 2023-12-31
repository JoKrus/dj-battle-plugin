package net.jcom.minecraft.battleplugin.data;

import net.jcom.minecraft.battleplugin.BattlePlugin;
import net.jcom.minecraft.battleplugin.apidata.TeamConfigWrapper;
import net.jcom.minecraft.battleplugin.config.Defaults;
import net.jcom.minecraft.battleplugin.config.DefaultsManager;
import net.jcom.minecraft.battleplugineventapi.data.TeamConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static net.jcom.minecraft.battleplugin.BattlePlugin.GSON;

public class TeamConfigSerializer {

    public static final Object TEAM_FILE_LOCK = new Object();
    public static final String TEAM_PATH =
            BattlePlugin.getPlugin().getDataFolder().getAbsolutePath() + File.separator +
                    "teamConfig.json";

    private TeamConfigSerializer() {
    }

    public static void init() {
        var folder = BattlePlugin.getPlugin().getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!new File(TEAM_PATH).exists()) {
            TeamConfigSerializer.saveData(TeamConfig.EMPTY);
        }
    }

    public static boolean addEntry(String teamName, Player player) {
        synchronized (TEAM_FILE_LOCK) {
            var obj = loadData();

            //Check if player is not in another team
            var currTeam = TeamConfigWrapper.getPlayerToTeamMap(obj).get(player);

            if (currTeam != null) {
                return false;
            }

            var teamPlayerList = obj.biTeamToPlayers.get(teamName);
            if (teamPlayerList == null) {
                //Create Team
                obj.biTeamToPlayers.put(teamName, new ArrayList<>());
                teamPlayerList = obj.biTeamToPlayers.get(teamName);
            }

            if (teamPlayerList.size() >= DefaultsManager.<Integer>getValue(Defaults.TEAM_SIZE)) {
                return false;
            }

            teamPlayerList.add(player);
            obj.biTeamToPlayers.put(teamName, teamPlayerList);

            saveData(obj);
            return true;
        }
    }

    public static Pair<Boolean, String> removeEntry(OfflinePlayer player) {
        synchronized (TEAM_FILE_LOCK) {
            var obj = loadData();

            //Check if player is in a team
            var currTeam = TeamConfigWrapper.getPlayerToTeamMap(obj).get(player);

            if (currTeam == null) {
                return Pair.of(false, null);
            }

            var teamPlayerList = obj.biTeamToPlayers.get(currTeam);
            if (teamPlayerList.size() == 1) {
                obj.biTeamToPlayers.remove(currTeam);
            } else {
                teamPlayerList.remove(player);
                obj.biTeamToPlayers.put(currTeam, teamPlayerList);
            }

            saveData(obj);
            return Pair.of(true, currTeam);
        }
    }

    public static boolean saveData(TeamConfig teamConfig) {
        synchronized (TEAM_FILE_LOCK) {
            try {
                var serializable = TeamConfigSerializable.fromTeamConfig(teamConfig);
                var jsonString = GSON.toJson(serializable);
                FileUtils.writeStringToFile(new File(TEAM_PATH), jsonString, StandardCharsets.UTF_8);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }


    public static boolean saveData(TeamConfigWrapper teamConfig) {
        return saveData(TeamConfigWrapper.toTeamConfig(teamConfig));
    }

    public static TeamConfigWrapper loadData() {
        synchronized (TEAM_FILE_LOCK) {
            try {
                var strObj = FileUtils.readFileToString(new File(TEAM_PATH), StandardCharsets.UTF_8);

                var configSerializable = GSON.fromJson(strObj, TeamConfigSerializable.class);
                var config = TeamConfigSerializable.toTeamConfig(configSerializable);
                return TeamConfigWrapper.ofTeamConfig(config);
            } catch (IOException e) {
                e.printStackTrace();
                return TeamConfigWrapper.ofTeamConfig(TeamConfig.EMPTY);
            }
        }
    }
}
