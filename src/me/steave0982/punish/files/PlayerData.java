package me.steave0982.punish.files;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import me.steave0982.punish.Main;
import org.bukkit.entity.Player;

public class PlayerData
{
    public static HashMap<UUID, PlayersDataCF> dataMap = new HashMap();

    public static void loadPlayer(UUID id, Player p)
    {
        File dir = new File(Main.getInstance().getDataFolder() + "/playerdata");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        PlayersDataCF pd = new PlayersDataCF(id);
        if (!pd.load()) {
            throw new IllegalStateException("[PunishGUI] Failed to load player config with the UUID " + id + "].");
        }
        dataMap.put(id, pd);
    }
}
