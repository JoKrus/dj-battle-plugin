package net.jcom.minecraft.battleplugin.data;

import net.jcom.minecraft.battleplugin.apidata.SpectateData;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class SpectateDataSerializable {

    private HashMap<UUID, UUID> spectatorToTarget = new HashMap<>();

    private SpectateDataSerializable() {
    }

    public static SpectateData toSpectateData(SpectateDataSerializable serializable) {
        SpectateData spectateData = SpectateData.EMPTY;
        for (var entry : serializable.spectatorToTarget.entrySet()) {
            var spec = Bukkit.getOfflinePlayer(entry.getKey());
            var target = Bukkit.getOfflinePlayer(entry.getValue());
            spectateData.spectatorToTarget.put(spec, target);
        }
        return spectateData;
    }

    public static SpectateDataSerializable fromSpectateData(SpectateData spectateData) {
        SpectateDataSerializable serializable = new SpectateDataSerializable();
        for (var entry : spectateData.spectatorToTarget.entrySet()) {
            serializable.spectatorToTarget.put(entry.getKey().getUniqueId(), entry.getValue().getUniqueId());
        }
        return serializable;
    }
}
