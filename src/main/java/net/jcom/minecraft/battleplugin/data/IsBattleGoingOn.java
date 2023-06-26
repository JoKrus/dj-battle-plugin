package net.jcom.minecraft.battleplugin.data;

import net.jcom.minecraft.battleplugin.BattlePlugin;
import org.bukkit.Bukkit;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import javax.xml.crypto.Data;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class IsBattleGoingOn implements Serializable {
    @Serial
    private static final long serialVersionUID = -1681014706529286330L;

    public static final String BATTLE_PATH =
            BattlePlugin.getPlugin().getDataFolder().getAbsolutePath() + File.separator +
                    "IsBattleGoing.data";

    public final AtomicBoolean atomicBoolean;

    private IsBattleGoingOn(boolean isBattleGoingOn) {
        this.atomicBoolean = new AtomicBoolean(isBattleGoingOn);
    }

    public static void init() {
        var folder = BattlePlugin.getPlugin().getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!new File(BATTLE_PATH).exists()) {
            IsBattleGoingOn.saveData(false);
        }
    }

    public static boolean saveData(boolean toSave) {
        try {
            IsBattleGoingOn isb = new IsBattleGoingOn(toSave);

            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(BATTLE_PATH)));
            out.writeObject(isb);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean loadData() {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(BATTLE_PATH)));
            IsBattleGoingOn data = (IsBattleGoingOn) in.readObject();
            in.close();
            return data.atomicBoolean.get();
        } catch (ClassNotFoundException | IOException e) {
            Bukkit.getLogger().info("Reading went wrong, so returning false");
            return false;
        }
    }
}
