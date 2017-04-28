package me.steave0982.punish.files;

import java.io.File;
import java.io.PrintStream;
import java.util.UUID;
import me.steave0982.punish.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;

public class PlayersDataCF
{
    private YamlConfiguration pdata;
    private File pdataf;
    private UUID uuid;

    public PlayersDataCF(UUID uuid)
    {
        this.pdata = new YamlConfiguration();
        this.pdataf = new File(Main.getInstance().getDataFolder() + "/playerdata", uuid + ".yml");
        this.uuid = uuid;
    }

    public boolean load()
    {
        try
        {
            if (!exists())
            {
                this.pdataf.createNewFile();
                this.pdata.addDefault("player.uuid", this.uuid.toString());
                this.pdata.addDefault("chat1sev.pastOffences", Integer.valueOf(0));
                this.pdata.addDefault("chat2sev.pastOffences", Integer.valueOf(0));
                this.pdata.addDefault("chat3sev.pastOffences", Integer.valueOf(0));
                this.pdata.addDefault("gen1sev.pastOffences", Integer.valueOf(0));
                this.pdata.addDefault("clientMod1sev.pastOffences", Integer.valueOf(0));
                this.pdata.addDefault("clientMod2sev.pastOffences", Integer.valueOf(0));
                this.pdata.addDefault("clientMod3sev.pastOffences", Integer.valueOf(0));
                this.pdata.options().copyDefaults(true);
                save();
                System.out.println("[PunishGUI] Created player config with the uuid [" + this.uuid + "].");
            }
            this.pdata.load(this.pdataf);
            return true;
        }
        catch (Exception e)
        {
            System.out.println("[PunishGUI] Failed loading player config with the uuid [" + this.uuid + "].");
        }
        return false;
    }

    public boolean save()
    {
        try
        {
            this.pdata.save(this.pdataf);
            return true;
        }
        catch (Exception e)
        {
            System.out.println("[PunishGUI] Failed saving player config with the uuid [" + this.uuid + "].");
        }
        return false;
    }

    public boolean exists()
    {
        return this.pdataf.exists();
    }

    public FileConfiguration get()
    {
        return this.pdata;
    }
}