package me.serbob.devroomsimpledungeon.DungeonSystem.Listeners;


import me.serbob.devroomsimpledungeon.DungeonSystem.Cores.DungeonCore;
import me.serbob.devroomsimpledungeon.DungeonSystem.Mobs.CustomMobs;
import me.serbob.devroomsimpledungeon.DungeonSystem.Managers.DatabaseManager;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.sql.SQLException;

public class MobListener implements Listener {
    @EventHandler
    public void MobKilled(EntityDeathEvent event) throws SQLException {
        if (DungeonCore.getCurrentSession(event.getEntity().getKiller()) == null) return;
        for (CustomMobs mob : DungeonCore.getCurrentSession(event.getEntity().getKiller()).getSpawnedMobs().getMobs()) {
            if (mob.getSpawned() == event.getEntity()) {
                DungeonCore.getCurrentSession(event.getEntity().getKiller()).getSpawnedMobs().getMobs().remove(mob);
                DatabaseManager.instance.addKill(event.getEntity().getKiller());
                break;
            }
        }
    }
    @EventHandler
    public void PlayerDenyDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (DungeonCore.isIngame(player)) {
            LivingEntity entity = null;
            if (!(event.getDamager() instanceof LivingEntity)) {
                if (event.getDamager() instanceof Projectile) {
                    entity = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
                }
            } else {
                entity = (LivingEntity) event.getDamager();
            }
            DungeonCore session = DungeonCore.getCurrentSession(player);
            if (DungeonCore.getCurrentSession(entity) != session) {
                event.setCancelled(true);
                DungeonCore mobSession = DungeonCore.getCurrentSession(entity);
                if (mobSession == null) {
                    return;
                }
                ((Creature) entity).setTarget(mobSession.getPlayer());
            }
        }
    }
}
