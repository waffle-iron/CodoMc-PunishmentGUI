package me.steave0982.punish;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import me.steave0982.punish.files.PlayerData;
import me.steave0982.punish.files.PlayersDataCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main
        extends JavaPlugin
        implements Listener
{
    String reasonPunish;
    String reportBanReason;
    public HashMap<UUID, String> punishArgs = new HashMap();
    public HashMap<UUID, String> punishPlayer = new HashMap();
    private static Main instance;

    public void onEnable()
    {
        instance = this;
        this.punishPlayer.clear();
        this.punishArgs.clear();
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        for (Player po : Bukkit.getOnlinePlayers()) {
            PlayerData.loadPlayer(po.getUniqueId(), po);
        }
        PluginDescriptionFile pdfFile = getDescription();
        Logger logger = getLogger();
        logger.info("[" + pdfFile.getName() + " v" + pdfFile.getVersion() + "]" + " Punish by Steave0982: Enabled");
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new Event_PlayerJoinEvent(), this);
    }

    public void onDisable()
    {
        PluginDescriptionFile pdfFile = getDescription();
        Logger logger = getLogger();
        logger.info("[" + pdfFile.getName() + " v" + pdfFile.getVersion() + "]" + " Punish by Steave0982: Disabled");
    }

    public static Main getInstance()
    {
        return instance;
    }

    @EventHandler
    public void onJoin(PlayerLoginEvent event)
    {
        Player player = event.getPlayer();
        if (this.punishPlayer.containsKey(player.getUniqueId())) {
            this.punishPlayer.remove(player.getUniqueId());
        }
        if (this.punishArgs.containsKey(player.getUniqueId())) {
            this.punishPlayer.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void punishmentGui(InventoryClickEvent event)
    {
        if ((event.getClickedInventory() == null) || (event.getInventory() == null) || (event.getClickedInventory().getName() == null) || (event.getInventory().getName() == null)) {
            return;
        }
        if (event.getCurrentItem() == null) {
            return;
        }
        if (event.getCurrentItem().getItemMeta() == null) {
            return;
        }
        if (event.getCurrentItem().getItemMeta().getDisplayName() == null) {
            return;
        }
        if (!event.getInventory().getName().contains("Punish")) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player)event.getWhoClicked();
        if ((event.getCurrentItem().getType() == Material.PAPER) &&
                (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.GRAY + "Example Warning Input;")))
        {
            Bukkit.dispatchCommand(player, "warn " + (String)this.punishPlayer.get(player.getUniqueId()) + " " + (String)this.punishArgs.get(player.getUniqueId()));
            player.closeInventory();

            this.punishPlayer.remove(player.getUniqueId());
            this.punishArgs.remove(player.getUniqueId());
        }
        if ((event.getCurrentItem().getType() == Material.ENCHANTED_BOOK) &&
                (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.RESET + "Report Ban Duration: " + ChatColor.YELLOW + "Permanent")))
        {
            for (Player po : Bukkit.getOnlinePlayers()) {
                if ((po.hasPermission("Rank.Helper")) || (po.getName().equals(this.punishPlayer.get(player.getUniqueId()))))
                {
                    po.sendMessage(ChatColor.BLUE + "Punish> " + ChatColor.GRAY + player.getName() + " report banned " + (String)this.punishPlayer.get(player.getUniqueId()) + " for Permanent.");
                    po.sendMessage(ChatColor.BLUE + "Punish> " + ChatColor.GRAY + ChatColor.BOLD + "Reason: " + ChatColor.GRAY + (String)this.punishArgs.get(player.getUniqueId()));
                }
            }
            player.closeInventory();
            this.punishPlayer.remove(player.getUniqueId());
            this.punishArgs.remove(player.getUniqueId());
        }
        if ((event.getCurrentItem().getType() == Material.REDSTONE_BLOCK) &&
                (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "Permanent")))
        {
            Bukkit.dispatchCommand(player, "ban " + (String)this.punishPlayer.get(player.getUniqueId()) + " " + (String)this.punishArgs.get(player.getUniqueId()));
            player.closeInventory();
            this.punishPlayer.remove(player.getUniqueId());
            this.punishArgs.remove(player.getUniqueId());
        }
        if ((event.getCurrentItem().getType() == Material.BOOK_AND_QUILL) &&
                (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.RESET + "Mute Duration: " + ChatColor.YELLOW + "Permanent")))
        {
            Bukkit.dispatchCommand(player, "mute " + (String)this.punishPlayer.get(player.getUniqueId()) + " " + (String)this.punishArgs.get(player.getUniqueId()));
            player.closeInventory();
            this.punishPlayer.remove(player.getUniqueId());
            this.punishArgs.remove(player.getUniqueId());
        }
        Player target = Bukkit.getPlayer((String)this.punishPlayer.get(player.getUniqueId()));
        if (event.getCurrentItem().getType() == Material.INK_SACK)
        {
            if (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.DARK_GREEN + "Give Warning if 0 Past Offences and 0 Warnings."))
            {
                Bukkit.dispatchCommand(player, "tempmute " + (String)this.punishPlayer.get(player.getUniqueId()) + " 2h " + (String)this.punishArgs.get(player.getUniqueId()));
                ((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).get().set("chat1sev.pastOffences", Integer.valueOf(((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).get().getInt("chat1sev.pastOffences") + 1));
                ((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).save();
                player.closeInventory();
                this.punishPlayer.remove(player.getUniqueId());
                this.punishArgs.remove(player.getUniqueId());
            }
            if (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.GRAY + "Medium Abuse/Harassment"))
            {
                Bukkit.dispatchCommand(player, "tempmute " + (String)this.punishPlayer.get(player.getUniqueId()) + " 1d " + (String)this.punishArgs.get(player.getUniqueId()));
                ((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).get().set("chat2sev.pastOffences", Integer.valueOf(((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).get().getInt("chat2sev.pastOffences") + 1));
                ((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).save();
                player.closeInventory();
                this.punishPlayer.remove(player.getUniqueId());
                this.punishArgs.remove(player.getUniqueId());
            }
            if (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.GRAY + "Severe Abuse/Harassment"))
            {
                Bukkit.dispatchCommand(player, "tempmute " + (String)this.punishPlayer.get(player.getUniqueId()) + " 30d " + (String)this.punishArgs.get(player.getUniqueId()));
                ((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).get().set("chat3sev.pastOffences", Integer.valueOf(((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).get().getInt("chat3sev.pastOffences") + 1));
                ((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).save();
                player.closeInventory();
                this.punishPlayer.remove(player.getUniqueId());
                this.punishArgs.remove(player.getUniqueId());
            }
            if (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.GRAY + "Map/Bug Exploiting"))
            {
                Bukkit.dispatchCommand(player, "tempban " + (String)this.punishPlayer.get(player.getUniqueId()) + " 4h " + (String)this.punishArgs.get(player.getUniqueId()));
                ((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).get().set("gen1sev.pastOffences", Integer.valueOf(((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).get().getInt("gen1sev.pastOffences") + 1));
                ((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).save();
                player.closeInventory();
                this.punishPlayer.remove(player.getUniqueId());
                this.punishArgs.remove(player.getUniqueId());
            }
            if (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.GRAY + "Examples;"))
            {
                Bukkit.dispatchCommand(player, "tempban " + (String)this.punishPlayer.get(player.getUniqueId()) + " 1d " + (String)this.punishArgs.get(player.getUniqueId()));
                ((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).get().set("clientMod1sev.pastOffences", Integer.valueOf(((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).get().getInt("clientMod1sev.pastOffences") + 1));
                ((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).save();
                player.closeInventory();
                this.punishPlayer.remove(player.getUniqueId());
                this.punishArgs.remove(player.getUniqueId());
            }
            if (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.GRAY + "Hacks;"))
            {
                Bukkit.dispatchCommand(player, "tempban " + (String)this.punishPlayer.get(player.getUniqueId()) + " 30d " + (String)this.punishArgs.get(player.getUniqueId()));
                ((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).get().set("clientMod2sev.pastOffences", Integer.valueOf(((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).get().getInt("clientMod2sev.pastOffences") + 1));
                ((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).save();
                player.closeInventory();
                this.punishPlayer.remove(player.getUniqueId());
                this.punishArgs.remove(player.getUniqueId());
            }
            if (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.GRAY + "Hacks:"))
            {
                Bukkit.dispatchCommand(player, "tempban " + (String)this.punishPlayer.get(player.getUniqueId()) + " 30d " + (String)this.punishArgs.get(player.getUniqueId()));
                ((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).get().set("clientMod3sev.pastOffences", Integer.valueOf(((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).get().getInt("clientMod3sev.pastOffences") + 1));
                ((PlayersDataCF)PlayerData.dataMap.get(target.getUniqueId())).save();
                player.closeInventory();
                this.punishPlayer.remove(player.getUniqueId());
                this.punishArgs.remove(player.getUniqueId());
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("player only command.");
            return false;
        }
        if ((label.equalsIgnoreCase("punish")) || (label.equalsIgnoreCase("p")))
        {
            Player player = (Player)sender;
            if (!player.hasPermission("Rank.TRAINEE"))
            {
                player.sendMessage(ChatColor.BLUE + "Permissions> " + ChatColor.GRAY + "This requires Permission Rank [" + ChatColor.BLUE + "TRAINEE" + ChatColor.GRAY + "].");
                return false;
            }
            if (args.length > 1)
            {
                String Steave0982 = args[0];
                if (Steave0982.length() > 16)
                {
                    player.sendMessage(Steave0982.length() + " length.");
                    player.sendMessage(ChatColor.BLUE + "Offline Player Search> " + ChatColor.YELLOW + "0 " + ChatColor.GRAY + "matches for [" + ChatColor.YELLOW + Steave0982 + ChatColor.GRAY + "].");
                    return false;
                }
                OfflinePlayer op = Bukkit.getPlayer(Steave0982);
                this.reasonPunish = "";
                for (int i = 1; i < args.length; i++) {
                    this.reasonPunish = (this.reasonPunish + args[i] + " ");
                }
                this.punishPlayer.put(player.getUniqueId(), args[0]);
                this.punishArgs.put(player.getUniqueId(), this.reasonPunish);
                Inventory inv = Bukkit.createInventory(null, 54, "            Punish");

                ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
                SkullMeta meta = (SkullMeta)item.getItemMeta();
                meta.setOwner(Steave0982);
                meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + Steave0982);
                ArrayList<String> headLore = new ArrayList();
                headLore.add(ChatColor.RESET + this.reasonPunish);
                meta.setLore(headLore);
                item.setItemMeta(meta);

                ItemStack chatOffense = new ItemStack(Material.BOOK_AND_QUILL);
                ItemMeta chatOffenseim = chatOffense.getItemMeta();
                chatOffenseim.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Chat Offense");
                ArrayList<String> chatOffensear = new ArrayList();
                chatOffensear.add(ChatColor.GRAY + "Verbal Abuse, Spam, Harassment, Trolling, etc");
                chatOffenseim.setLore(chatOffensear);
                chatOffense.setItemMeta(chatOffenseim);

                ItemStack generalOffense = new ItemStack(Material.HOPPER);
                ItemMeta generalOffenseim = generalOffense.getItemMeta();
                generalOffenseim.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "General Offense");
                ArrayList<String> generalOffensear = new ArrayList();
                generalOffensear.add(ChatColor.GRAY + "Command/Map/Class/Skill exploits, etc");
                generalOffenseim.setLore(generalOffensear);
                generalOffense.setItemMeta(generalOffenseim);

                ItemStack clientMod = new ItemStack(Material.IRON_SWORD);
                ItemMeta clientModim = clientMod.getItemMeta();
                clientModim.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Client Mod");
                ArrayList<String> clientModar = new ArrayList();
                clientModar.add(ChatColor.GRAY + "X-ray, Forcefield, Speed, Fly, Inventory Hacks, etc");
                clientModim.setLore(clientModar);
                clientMod.setItemMeta(clientModim);

                ItemStack warning = new ItemStack(Material.PAPER);
                ItemMeta warningim = warning.getItemMeta();
                warningim.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Warning");
                ArrayList<String> warningar = new ArrayList();
                warningar.add("");
                warningar.add(ChatColor.GRAY + "Example Warning Input;");
                warningar.add(ChatColor.RESET + "   Spam - Repeatedly writing MEOW");
                warningar.add(ChatColor.RESET + "   Swearing - Saying 'fuck' and 'shit'");
                warningar.add(ChatColor.RESET + "   Hack Accusation - Accused Tomp13 of hacking");
                warningar.add(ChatColor.RESET + "   Trolling - was trying to make bob angry in chat");
                warningim.setLore(warningar);
                warning.setItemMeta(warningim);

                ItemStack permban = new ItemStack(Material.REDSTONE_BLOCK);
                ItemMeta permbanim = permban.getItemMeta();
                permbanim.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Permanent Ban");
                ArrayList<String> permbanar = new ArrayList();
                permbanar.add(ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "Permanent");
                permbanar.add("");
                permbanar.add(ChatColor.DARK_GREEN + "Must supply a detailed reason for Ban.");
                permbanim.setLore(permbanar);
                permban.setItemMeta(permbanim);

                ItemStack permmute = new ItemStack(Material.BOOK_AND_QUILL);
                ItemMeta permmuteim = permmute.getItemMeta();
                permmuteim.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Permanent Mute");
                ArrayList<String> permmutear = new ArrayList();
                permmutear.add(ChatColor.RESET + "Mute Duration: " + ChatColor.YELLOW + "Permanent");
                permmutear.add("");
                permmutear.add(ChatColor.GRAY + "Severe Advertising;");
                permmutear.add(ChatColor.RESET + "   'JOIN MINECADE! MINEPLEX SUCKS!");
                permmutear.add(ChatColor.RESET + "   'join crap.server.net! FREE ADMIN!");
                permmutear.add("");
                permmutear.add(ChatColor.DARK_GREEN + "Must supply a detailed reason for Mute.");
                permmuteim.setLore(permmutear);
                permmute.setItemMeta(permmuteim);

                ItemStack reportBan = new ItemStack(Material.ENCHANTED_BOOK);
                ItemMeta reportBanim = reportBan.getItemMeta();
                reportBanim.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Permanent Report Ban");
                ArrayList<String> reportBanar = new ArrayList();
                reportBanar.add(ChatColor.RESET + "Report Ban Duration: " + ChatColor.YELLOW + "Permanent");
                reportBanar.add("");
                reportBanar.add(ChatColor.GRAY + "Abusing Report Feature");
                reportBanar.add(ChatColor.RESET + "   /report SomeUser THE STAFF HERE SUCK");
                reportBanar.add(ChatColor.RESET + "   /report SomeUser MINEPLEX IS A F****** PIECE OF S***");
                reportBanim.setLore(reportBanar);
                reportBan.setItemMeta(reportBanim);

                ItemStack chatSev1 = new ItemStack(Material.INK_SACK, 1, (short)2);
                ItemMeta chatSev1im = chatSev1.getItemMeta();
                chatSev1im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Severity 1");
                ArrayList<String> chatSev1ar = new ArrayList();
                chatSev1ar.add(ChatColor.RESET + "Past Offences: " + ChatColor.YELLOW + ((PlayersDataCF)PlayerData.dataMap.get(op.getUniqueId())).get().getInt("chat1sev.pastOffences"));
                chatSev1ar.add(ChatColor.RESET + "Mute Duration: " + ChatColor.YELLOW + "2.0 Hours");
                chatSev1ar.add("");
                chatSev1ar.add(ChatColor.GRAY + "Light Spam");
                chatSev1ar.add(ChatColor.RESET + "   Sending the same message 2 - 5 times");
                chatSev1ar.add("");
                chatSev1ar.add(ChatColor.GRAY + "Light Advertising");
                chatSev1ar.add(ChatColor.RESET + "   'anyone want to play on minecade?'");
                chatSev1ar.add("");
                chatSev1ar.add(ChatColor.GRAY + "Light Abuse/Harassment");
                chatSev1ar.add(ChatColor.RESET + "   'you suck at this game'");
                chatSev1ar.add("");
                chatSev1ar.add(ChatColor.GRAY + "Hackusations");
                chatSev1ar.add(ChatColor.RESET + "   'you're such a hacker!'");
                chatSev1ar.add("");
                chatSev1ar.add(ChatColor.GRAY + "Trolling");
                chatSev1ar.add("");
                chatSev1ar.add(ChatColor.DARK_GREEN + "Give Warning if 0 Past Offences and 0 Warnings.");
                chatSev1im.setLore(chatSev1ar);
                chatSev1.setItemMeta(chatSev1im);

                ItemStack chatSev2 = new ItemStack(Material.INK_SACK, 1, (short)11);
                ItemMeta chatSev2im = chatSev2.getItemMeta();
                chatSev2im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Severity 2");
                ArrayList<String> chatSev2ar = new ArrayList();
                chatSev2ar.add(ChatColor.RESET + "Past Offences: " + ChatColor.YELLOW + ((PlayersDataCF)PlayerData.dataMap.get(op.getUniqueId())).get().getInt("chat2sev.pastOffences"));
                chatSev2ar.add(ChatColor.RESET + "Mute Duration: " + ChatColor.YELLOW + "1.0 Days");
                chatSev2ar.add("");
                chatSev2ar.add(ChatColor.GRAY + "Medium Spam");
                chatSev2ar.add(ChatColor.RESET + "   Sending the same message 6 - 20 times");
                chatSev2ar.add("");
                chatSev2ar.add(ChatColor.GRAY + "Medium Advertising;");
                chatSev2ar.add(ChatColor.RESET + "   'join crap.server.net' - posted once");
                chatSev2ar.add("");
                chatSev2ar.add(ChatColor.GRAY + "Medium Abuse/Harassment");
                chatSev2ar.add(ChatColor.RESET + "   'piss off you stupid newb'");
                chatSev2ar.add(ChatColor.RESET + "   'SHIT ADMINS ARE SHIT!!!'");
                chatSev2ar.add(ChatColor.RESET + "   'you're terrible, learn to play'");
                chatSev2ar.add("");
                chatSev2ar.add(ChatColor.GRAY + "Avoiding Chat Filter");
                chatSev2ar.add(ChatColor.RESET + "   'F|_|<K YOU'");
                chatSev2im.setLore(chatSev2ar);
                chatSev2.setItemMeta(chatSev2im);

                ItemStack chatSev3 = new ItemStack(Material.INK_SACK, 1, (short)1);
                ItemMeta chatSev3im = chatSev2.getItemMeta();
                chatSev3im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Severity 3");
                ArrayList<String> chatSev3ar = new ArrayList();
                chatSev3ar.add(ChatColor.RESET + "Past Offences: " + ChatColor.YELLOW + ((PlayersDataCF)PlayerData.dataMap.get(op.getUniqueId())).get().getInt("chat3sev.pastOffences"));
                chatSev3ar.add(ChatColor.RESET + "Mute Duration: " + ChatColor.YELLOW + "30.0 Days");
                chatSev3ar.add("");
                chatSev3ar.add(ChatColor.GRAY + "Severe Spam");
                chatSev3ar.add(ChatColor.RESET + "   Sending the same message 20+ times");
                chatSev3ar.add(ChatColor.RESET + "   Only really used for a spam bot");
                chatSev3ar.add("");
                chatSev3ar.add(ChatColor.GRAY + "Severe Abuse/Harassment");
                chatSev3ar.add(ChatColor.RESET + "   'go fucking die in a fire you fucking sack of shit'");
                chatSev3im.setLore(chatSev3ar);
                chatSev3.setItemMeta(chatSev3im);

                ItemStack generalSev1 = new ItemStack(Material.INK_SACK, 1, (short)2);
                ItemMeta generalSev1im = generalSev1.getItemMeta();
                generalSev1im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Severity 1");
                ArrayList<String> generalSev1ar = new ArrayList();
                generalSev1ar.add(ChatColor.RESET + "Past Offences: " + ChatColor.YELLOW + ((PlayersDataCF)PlayerData.dataMap.get(op.getUniqueId())).get().getInt("gen1sev.pastOffences"));
                generalSev1ar.add(ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "4.0 Hours");
                generalSev1ar.add("");
                generalSev1ar.add(ChatColor.GRAY + "Team Killing");
                generalSev1ar.add(ChatColor.RESET + "   Intentionally killing your team mates");
                generalSev1ar.add("");
                generalSev1ar.add(ChatColor.GRAY + "Trolling (Gameplay)");
                generalSev1ar.add(ChatColor.RESET + "   Using abilities to trap players in spawn");
                generalSev1ar.add("");
                generalSev1ar.add(ChatColor.GRAY + "Map/Bug Exploiting");
                generalSev1ar.add(ChatColor.RESET + "   Abusing an exploit to gain an advantage");
                generalSev1im.setLore(generalSev1ar);
                generalSev1.setItemMeta(generalSev1im);

                ItemStack clientModSev1 = new ItemStack(Material.INK_SACK, 1, (short)2);
                ItemMeta clientModSev1im = clientModSev1.getItemMeta();
                clientModSev1im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Severity 1");
                ArrayList<String> clientModSev1ar = new ArrayList();
                clientModSev1ar.add(ChatColor.RESET + "Past Offences: " + ChatColor.YELLOW + ((PlayersDataCF)PlayerData.dataMap.get(op.getUniqueId())).get().getInt("clientMod1sev.pastOffences"));
                clientModSev1ar.add(ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "2.0 Days");
                clientModSev1ar.add("");
                clientModSev1ar.add(ChatColor.GRAY + "Examples;");
                clientModSev1ar.add(ChatColor.RESET + "   Damage Indicators");
                clientModSev1ar.add(ChatColor.RESET + "   Player Radar");
                clientModSev1im.setLore(clientModSev1ar);
                clientModSev1.setItemMeta(clientModSev1im);

                ItemStack clientModSev2 = new ItemStack(Material.INK_SACK, 1, (short)11);
                ItemMeta clientModSev2im = clientModSev2.getItemMeta();
                clientModSev2im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Severity 2");
                ArrayList<String> clientModSev2ar = new ArrayList();
                clientModSev2ar.add(ChatColor.RESET + "Past Offences: " + ChatColor.YELLOW + ((PlayersDataCF)PlayerData.dataMap.get(op.getUniqueId())).get().getInt("clientMod2sev.pastOffences"));
                clientModSev2ar.add(ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "30.0 Days");
                clientModSev2ar.add("");
                clientModSev2ar.add(ChatColor.GRAY + "Hacks;");
                clientModSev2ar.add(ChatColor.RESET + "   Forcefield");
                clientModSev2ar.add(ChatColor.RESET + "   Speed Hack");
                clientModSev2ar.add(ChatColor.RESET + "   Reach Hack");
                clientModSev2ar.add(ChatColor.RESET + "   Other Hack");
                clientModSev2ar.add("");
                clientModSev2ar.add(ChatColor.GRAY + "Hacks Report (SR & FR);");
                clientModSev2ar.add(ChatColor.RESET + "   Forcefield");
                clientModSev2ar.add(ChatColor.RESET + "   Speed Hack");
                clientModSev2ar.add(ChatColor.RESET + "   Reach Hack");
                clientModSev2ar.add(ChatColor.RESET + "   Other Hack");
                clientModSev2ar.add(ChatColor.RESET + "   Fly Hack");
                clientModSev2im.setLore(clientModSev2ar);
                clientModSev2.setItemMeta(clientModSev2im);

                ItemStack clientModSev3 = new ItemStack(Material.INK_SACK, 1, (short)1);
                ItemMeta clientModSev3im = clientModSev3.getItemMeta();
                clientModSev3im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Severity 3");
                ArrayList<String> clientModSev3ar = new ArrayList();
                clientModSev3ar.add(ChatColor.RESET + "Past Offences: " + ChatColor.YELLOW + ((PlayersDataCF)PlayerData.dataMap.get(op.getUniqueId())).get().getInt("clientMod3sev.pastOffences"));
                clientModSev3ar.add(ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "30.0 Days");
                clientModSev3ar.add("");
                clientModSev3ar.add(ChatColor.GRAY + "Hacks:");
                clientModSev3ar.add(ChatColor.RESET + "   Fly Hack");
                clientModSev3ar.add("");
                clientModSev3ar.add(ChatColor.RED + "" + ChatColor.BOLD + "WARNING; ");
                clientModSev3ar.add(ChatColor.RED + "Use Severity 2 for Forum/Staff Reports");
                clientModSev3im.setLore(clientModSev3ar);
                clientModSev3.setItemMeta(clientModSev3im);

                ItemStack devWarning = new ItemStack(Material.YELLOW_FLOWER);
                ItemMeta devWarningim = devWarning.getItemMeta();
                devWarningim.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "DEV WARNING");
                ArrayList<String> devWarningar = new ArrayList();
                devWarningar.add(ChatColor.RESET + "Developers are advised against using the punish system");
                devWarningar.add(ChatColor.RESET + "unless permitted by Staff");
                devWarningim.setLore(devWarningar);
                devWarning.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                devWarning.setItemMeta(devWarningim);
                if (player.hasPermission("Rank.DEVELOPER")) {
                    inv.setItem(0, devWarning);
                }
                inv.setItem(10, chatOffense);
                inv.setItem(19, chatSev1);
                inv.setItem(12, generalOffense);
                inv.setItem(21, generalSev1);
                inv.setItem(14, clientMod);
                inv.setItem(23, clientModSev1);
                if (player.hasPermission("Rank.MODERATOR"))
                {
                    inv.setItem(28, chatSev2);
                    inv.setItem(37, chatSev3);
                    inv.setItem(32, clientModSev2);
                    inv.setItem(41, clientModSev3);
                    inv.setItem(34, permban);
                    inv.setItem(43, permmute);
                }
                inv.setItem(25, warning);
                inv.setItem(26, reportBan);
                inv.setItem(4, item);
                player.openInventory(inv);
            }
            else
            {
                player.sendMessage(ChatColor.BLUE + "Punish> " + ChatColor.GRAY + "Commands List:");
                player.sendMessage(ChatColor.GOLD + "/punish " + ChatColor.GRAY + "<player> <reason>" + ChatColor.GOLD + " Mod");
                return false;
            }
        }
        return false;
    }
}
