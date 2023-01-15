package me.serbob.devroomsimpledungeon.DungeonSystem.Commands;

import me.serbob.devroomsimpledungeon.DungeonSystem.Cores.DungeonCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class LeaveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You are not a player!");
            return true;
        }

        Player player = (Player) sender;

        if (DungeonCore.isIngame(player)) {
            DungeonCore session = DungeonCore.getCurrentSession(player);
            try {
                session.end();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            player.sendMessage(ChatColor.RED + "You have left the dungeon and have been teleported to your original place.");
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "You are not in a dungeon.");
            return true;
        }
    }
}
