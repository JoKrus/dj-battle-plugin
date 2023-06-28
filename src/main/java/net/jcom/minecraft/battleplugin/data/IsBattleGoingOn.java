package net.jcom.minecraft.battleplugin.data;

import net.jcom.minecraft.battleplugin.BattlePlugin;
import org.bukkit.Bukkit;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class IsBattleGoingOn implements Serializable {
    @Serial
    private static final long serialVersionUID = -1681014706529286334L;

    public static final String BATTLE_PATH =
            BattlePlugin.getPlugin().getDataFolder().getAbsolutePath() + File.separator +
                    "IsBattleGoing.data";

    public final AtomicBoolean isGoing;
    public final AtomicBoolean isTimer;

    private IsBattleGoingOn(boolean isBattleGoingOn, boolean isTimer) {
        this.isGoing = new AtomicBoolean(isBattleGoingOn);
        this.isTimer = new AtomicBoolean(isTimer);
    }

    public static void init() {
        var folder = BattlePlugin.getPlugin().getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!new File(BATTLE_PATH).exists()) {
            IsBattleGoingOn.saveData(false, false);
        }
    }

    public static boolean saveData(boolean toSave, boolean isTimer) {
        try {
            IsBattleGoingOn isb = new IsBattleGoingOn(toSave, isTimer);

            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(BATTLE_PATH)));
            out.writeObject(isb);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static IsBattleGoingOn loadDataWithTimer() {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(BATTLE_PATH)));
            IsBattleGoingOn data = (IsBattleGoingOn) in.readObject();
            in.close();
            return data;
        } catch (ClassNotFoundException | IOException e) {
            Bukkit.getLogger().info("Reading went wrong, so returning false");
            return new IsBattleGoingOn(false, false);
        }
    }

    public static boolean loadData() {
        return loadDataWithTimer().isGoing.get();
    }
}
