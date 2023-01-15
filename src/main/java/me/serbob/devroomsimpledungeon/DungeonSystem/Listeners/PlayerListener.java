package me.serbob.devroomsimpledungeon.DungeonSystem.Listeners;

import me.serbob.devroomsimpledungeon.DungeonSystem.Cores.DungeonCore;
import me.serbob.devroomsimpledungeon.DungeonSystem.Managers.ConfigManager;
import me.serbob.devroomsimpledungeon.DungeonSystem.Managers.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.sql.SQLException;

public class PlayerListener implements Listener {
    @EventHandler
    public void PlayerDied(PlayerDeathEvent event) throws SQLException {
        Player player = event.getEntity();
        if (DungeonCore.isIngame(player)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            DungeonCore session = DungeonCore.getCurrentSession(player);
            DatabaseManager.instance.addDeath(session.getPlayer());
            session.end();
        }
    }
    @EventHandler
    public void InventoryClickDeny(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase("Stats")) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void PlayerJoined(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();
        DatabaseManager.instance.addPlayer(player);
        if (ConfigManager.getInstance().hasInventory(player)) {
            ConfigManager.getInstance().loadInventory(player);
            ConfigManager.getInstance().loadLocation(player);
        }
    }
    @EventHandler
    public void PlayerRespawned(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if(!ConfigManager.getInstance().hasInventory(player)) {
            player.getInventory().clear();
            ConfigManager.getInstance().loadInventory(player);
            ConfigManager.getInstance().loadLocation(player);
        }
    }
    @EventHandler
    public void PlayerDisconnected(PlayerQuitEvent event) throws SQLException {
        Player player = event.getPlayer();
        if (DungeonCore.isIngame(player)) {
            DungeonCore session = DungeonCore.getCurrentSession(player);
            if (session == null) {
                return;
            }
            session.end();
        }
    }
}
