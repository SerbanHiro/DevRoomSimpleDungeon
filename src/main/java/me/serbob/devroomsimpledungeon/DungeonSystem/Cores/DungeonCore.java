package me.serbob.devroomsimpledungeon.DungeonSystem.Cores;

import me.serbob.devroomsimpledungeon.DevRoomSimpleDungeon;
import me.serbob.devroomsimpledungeon.DungeonSystem.Managers.ConfigManager;
import me.serbob.devroomsimpledungeon.DungeonSystem.Managers.DatabaseManager;
import me.serbob.devroomsimpledungeon.DungeonSystem.Mobs.CustomMobs;
import me.serbob.devroomsimpledungeon.DungeonSystem.Mobs.MobsList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonCore {
    private Player player;
    private Location oldLocation;
    private MobsList spawnedMobs = new MobsList();
    private WavesCore difficulty;
    private int secondsUntilDifficultyIncrease = -1;
    private static Location spawnLocation;
    public static List<DungeonCore> sessions = new ArrayList<>();
    public DungeonCore(Player player, Location oldLocation) {
        this.player = player;
        this.oldLocation = oldLocation;
        difficulty = WavesCore.waves.get(0);
        ConfigManager.getInstance().verifyAlreadyUserdata(player);
    }
    public void start() throws SQLException {
        ConfigManager.getInstance().setInventory(player,player.getInventory().getContents());
        ConfigManager.getInstance().setLocation(player,oldLocation);
        sessions.add(this);
        secondsUntilDifficultyIncrease = DevRoomSimpleDungeon.config.getInt("WaveDifficultyIncreaseAfter");
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
        player.getInventory().addItem(new ItemStack(Material.DIAMOND_CHESTPLATE));
        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE,64));
        player.teleport(spawnLocation);
        DatabaseManager.instance.addSession(player);
        for (Player p : player.getWorld().getPlayers()) {
            if (p != player) {
                player.hidePlayer(DevRoomSimpleDungeon.instance, p);
            }
        }
        for (Entity e : player.getWorld().getEntities()) {
            if (e instanceof LivingEntity && e != player && DungeonCore.getCurrentSession((LivingEntity) e) == null) {
                player.hideEntity(DevRoomSimpleDungeon.instance, e);
            }
        }
        player.sendMessage(ChatColor.GREEN + "You have been teleported to a dungeon!");
    }
    public void end() throws SQLException {
        sessions.remove(this);
        if (player.isOnline() && player.getHealth() > 0) {
            player.teleport(oldLocation);
            player.getInventory().clear();
            ConfigManager.getInstance().loadInventory(player);
        }
        for (Player p : player.getWorld().getPlayers()) {
            if (p != player) {
                player.showPlayer(DevRoomSimpleDungeon.instance, p);
            }
        }
        for (CustomMobs mob : spawnedMobs.getMobs()) {
            mob.getSpawned().remove();
        }
    }
    public static void WhileInDungeon() {
        Random random = new Random();
        for (DungeonCore session : sessions) {
            List<WavesCore> waves = new ArrayList<>();
            WavesCore currentWave = session.difficulty;
            while(currentWave != null) {
                waves.add(currentWave);
                currentWave = currentWave.PreviousWave();
            }
            for (WavesCore wave : waves) {
                int mobCount = wave.getMobCount();
                MobsList mobs = wave.getMobs();
                for (int i = 0; i < mobCount; i++) {
                    CustomMobs mob = new CustomMobs(mobs.getMobs().get(random.nextInt(mobs.getMobs().size())));
                    LivingEntity livingEntity = (LivingEntity) mob.spawn(session.player.getWorld(), DungeonCore.getSpawnLocation());
                    livingEntity.setInvisible(true);
                    int radius = DevRoomSimpleDungeon.instance.getConfig().getInt("MobDistanceSpawning");
                    Location spawnLocation = new Location(livingEntity.getWorld(), livingEntity.getLocation().getX() + random.nextInt(radius), livingEntity.getLocation().getY(), livingEntity.getLocation().getZ() + random.nextInt(radius));
                    while (spawnLocation.getBlock().getRelative(0, -1, 0).getType().isSolid()) {
                        spawnLocation.setY(spawnLocation.getY() + 1);
                    }
                    livingEntity.teleport(spawnLocation);
                    livingEntity.setInvisible(false);
                    // Hide mobs from players
                    for (Player p : session.player.getWorld().getPlayers()) {
                        if (p != session.player) {
                            p.hideEntity(DevRoomSimpleDungeon.instance, livingEntity);
                        }
                    }
                    // Mob target
                    Creature creature = (Creature) livingEntity;
                    creature.setTarget(session.player);
                    session.spawnedMobs.addMob(mob);
                }
            }
        }
    }
    public static void difficultyLoop() {
        for (DungeonCore session : sessions) {
            session.secondsUntilDifficultyIncrease--;
            if (session.secondsUntilDifficultyIncrease < 0) continue;
            if (session.secondsUntilDifficultyIncrease == 0) {
                increaseDifficultyWave(session);
            }
        }
    }
    private static void increaseDifficultyWave(DungeonCore session) {
        session.secondsUntilDifficultyIncrease = DevRoomSimpleDungeon.config.getInt("WaveDifficultyIncreaseAfter");
        if (session.difficulty.PlayerHasReachedTheMaximumWaveDifficulty()) return;
        WavesCore nextDifficultyWave = session.difficulty.UpcomingWave();
        if (nextDifficultyWave != null) {
            session.difficulty = nextDifficultyWave;
            if (nextDifficultyWave.UpcomingWave() == null) {
                session.difficulty.PlayerIsAtMaximumWaveDifficulty(true);
                session.player.sendMessage(ChatColor.GREEN + "You have reached the " + ChatColor.RED + "maximum" + ChatColor.GREEN + " difficulty!");
            }
        }
    }
    public static DungeonCore getCurrentSession(Player player) {
        for (DungeonCore session : sessions) {
            if (session.getPlayer().equals(player)) {
                return session;
            }
        }
        return null;
    }
    public static DungeonCore getCurrentSession(LivingEntity entity) {
        for (DungeonCore session : sessions) {
            for (CustomMobs mob : session.spawnedMobs.getMobs()) {
                if (mob.getSpawned().equals(entity)) {
                    return session;
                }
            }
        }
        return null;
    }
    public static void setPlayerSpawnLocation(Location spawnLocation) {
        DungeonCore.spawnLocation = spawnLocation;
    }
    public static Location getSpawnLocation() {
        return spawnLocation;
    }
    public static boolean isIngame(Player player) {
        for (DungeonCore session : sessions) {
            if (session.getPlayer().equals(player)) {
                return true;
            }
        }
        return false;
    }
    public MobsList getSpawnedMobs() {
        return spawnedMobs;
    }
    public Player getPlayer() {
        return player;
    }
}
