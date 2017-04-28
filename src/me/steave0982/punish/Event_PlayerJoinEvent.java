package me.steave0982.punish;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import me.steave0982.punish.files.PlayerData;
import me.steave0982.punish.files.PlayersDataCF;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Event_PlayerJoinEvent
        implements Listener
{
    @EventHandler(priority=EventPriority.LOWEST)
    public void createData(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        PlayerData.loadPlayer(p.getUniqueId(), p);
        ((PlayersDataCF)PlayerData.dataMap.get(p)).get().set("player.ip", p.getAddress().getAddress().getHostAddress());
        ((PlayersDataCF)PlayerData.dataMap.get(p)).save();
    }
}