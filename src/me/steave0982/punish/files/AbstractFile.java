package me.steave0982.punish.files;

import java.io.File;
import java.io.IOException;
import me.steave0982.punish.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class AbstractFile
{
    protected Main main;
    private File file;
    protected FileConfiguration config;

    public AbstractFile(Main main, String fileName)
    {
        this.main = main;
        this.file = new File(main.getDataFolder(), fileName);
        if (!this.file.exists()) {
            try
            {
                this.file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public void save()
    {
        try
        {
            this.config.save(this.file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
