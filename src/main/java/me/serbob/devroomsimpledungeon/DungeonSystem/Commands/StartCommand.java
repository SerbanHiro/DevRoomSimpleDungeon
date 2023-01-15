package me.serbob.devroomsimpledungeon.DungeonSystem.Commands;

import me.serbob.devroomsimpledungeon.DungeonSystem.Cores.DungeonCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class StartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You are not a player!");
            return true;
        }
        Player player = (Player) sender;
        if (DungeonCore.isIngame(player)) {
            player.sendMessage(ChatColor.RED + "You are already in a dungeon.");
            return true;
        }
        DungeonCore session = new DungeonCore(player, player.getLocation());
        try {
            session.start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return true;
    }
}
