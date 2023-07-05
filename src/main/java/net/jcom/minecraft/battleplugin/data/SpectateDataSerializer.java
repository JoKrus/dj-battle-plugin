package net.jcom.minecraft.battleplugin.data;

import net.jcom.minecraft.battleplugin.BattlePlugin;
import net.jcom.minecraft.battleplugin.apidata.SpectateData;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static net.jcom.minecraft.battleplugin.BattlePlugin.GSON;

public class SpectateDataSerializer {

    public static final Object SPECTATE_FILE_LOCK = new Object();
    public static final String SPECTATE_PATH =
            BattlePlugin.getPlugin().getDataFolder().getAbsolutePath() + File.separator +
                    "spectateData.json";

    private SpectateDataSerializer() {
    }

    public static void init() {
        var folder = BattlePlugin.getPlugin().getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!new File(SPECTATE_PATH).exists()) {
            SpectateDataSerializer.saveData(SpectateData.EMPTY);
        }
    }

    public static void clear() {
        var file = new File(SPECTATE_PATH);
        if (file.exists()) {
            synchronized (SPECTATE_FILE_LOCK) {
                file.delete();
            }
        }
    }

    public static boolean setTarget(Player spectator, Player target) {
        synchronized (SPECTATE_FILE_LOCK) {
            var obj = loadData();
            obj.spectatorToTarget.put(spectator, target);
            saveData(obj);
            return true;
        }
    }

    public static boolean removeTarget(Player spectator) {
        synchronized (SPECTATE_FILE_LOCK) {
            var obj = loadData();
            obj.spectatorToTarget.remove(spectator);
            saveData(obj);
            return true;
        }
    }

    public static boolean isSpectating(Player spectator) {
        SpectateData obj;
        synchronized (SPECTATE_FILE_LOCK) {
            obj = loadData();
        }

        return obj.spectatorToTarget.containsKey(spectator);
    }

    public static boolean isSpectating(Player spectator, Player target) {
        SpectateData obj;
        synchronized (SPECTATE_FILE_LOCK) {
            obj = loadData();
        }

        return target.equals(obj.spectatorToTarget.get(spectator));
    }


    private static boolean saveData(SpectateData spectateData) {
        synchronized (SPECTATE_FILE_LOCK) {
            try {
                var serializable = SpectateDataSerializable.fromSpectateData(spectateData);
                var jsonString = GSON.toJson(serializable);
                FileUtils.writeStringToFile(new File(SPECTATE_PATH), jsonString, StandardCharsets.UTF_8);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    public static SpectateData loadData() {
        synchronized (SPECTATE_FILE_LOCK) {
            try {
                var strObj = FileUtils.readFileToString(new File(SPECTATE_PATH), StandardCharsets.UTF_8);
                var configSerializable = GSON.fromJson(strObj, SpectateDataSerializable.class);
                return SpectateDataSerializable.toSpectateData(configSerializable);
            } catch (IOException e) {
                return SpectateData.EMPTY;
            }
        }
    }
}
