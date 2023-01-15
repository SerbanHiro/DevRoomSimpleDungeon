package me.serbob.devroomsimpledungeon.DungeonSystem.Commands;

import me.serbob.devroomsimpledungeon.DungeonSystem.Managers.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.List;

public class StatsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You are not a player!");
            return true;
        }
        sender.sendMessage(ChatColor.GREEN + "Opening Stats");
        Inventory inventory = Bukkit.createInventory(null, 9, "Stats");
        try {
            int kills = DatabaseManager.instance.getKills((Player) sender);
            int deaths = DatabaseManager.instance.getDeaths((Player) sender);
            int sessions = DatabaseManager.instance.getSessions((Player) sender);
            float averageKillsSession = kills/(float)sessions;
            if (String.valueOf(averageKillsSession) == "NaN") averageKillsSession = 0;
            inventory.setItem(1, createItemWithNameAndLore(Material.DIAMOND_SWORD, "Kills", String.valueOf(kills)));
            inventory.setItem(3, createItemWithNameAndLore(Material.BONE, "Deaths", String.valueOf(deaths)));
            inventory.setItem(5, createItemWithNameAndLore(Material.CHEST, "Sessions", String.valueOf(sessions)));
            inventory.setItem(7, createItemWithNameAndLore(Material.GOLDEN_APPLE, "Average Kills Per Session", String.valueOf(averageKillsSession)));
            ((Player) sender).openInventory(inventory);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    private ItemStack createItemWithNameAndLore(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of(lore));
        item.setItemMeta(meta);
        return item;
    }
}
