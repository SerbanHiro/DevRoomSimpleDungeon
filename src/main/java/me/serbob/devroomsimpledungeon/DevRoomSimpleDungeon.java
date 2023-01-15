package me.serbob.devroomsimpledungeon;

import me.serbob.devroomsimpledungeon.DungeonSystem.Managers.DatabaseManager;
import me.serbob.devroomsimpledungeon.DungeonSystem.Cores.WavesCore;
import me.serbob.devroomsimpledungeon.DungeonSystem.Cores.DungeonCore;
import me.serbob.devroomsimpledungeon.DungeonSystem.Listeners.*;
import me.serbob.devroomsimpledungeon.DungeonSystem.Commands.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.SQLException;

public final class DevRoomSimpleDungeon extends JavaPlugin {
    public static DevRoomSimpleDungeon instance;
    public static FileConfiguration config;
    public static YamlConfiguration file;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        config = getConfig();
        File userDataFolder = new File(getDataFolder(), "userdata");
        if (!userDataFolder.exists()) {
            userDataFolder.mkdirs();
        }

        init();
    }
    @Override
    public void onDisable() {
        DungeonCore.sessions.stream().forEach(session -> {
            try {
                session.end();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            DatabaseManager.instance.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void init() {
        CommandsAndListeners();
        waveDifficulties();
        connectDatabase();
        BukkitRunnables();
    }

    public void CommandsAndListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new MobListener(), this);
        getCommand("start").setExecutor(new StartCommand());
        getCommand("leave").setExecutor(new LeaveCommand());
        getCommand("stats").setExecutor(new StatsCommand());
    }

    public void connectDatabase() {
        String dbUrl = "jdbc:mysql://" + config.getString("databaseHost") + ":" + config.getString("databasePort");
        String dbUser = config.getString("databaseUsername");
        String dbPassword = config.getString("databasePassword");
        System.out.println("Database CONNECTED!");
        try {
            DatabaseManager.instance = new DatabaseManager(dbUrl, dbUser, dbPassword, config.getString("databaseName"), "DevRoomSimpleDungeon");
        } catch (RuntimeException e) {
            getServer().getPluginManager().disablePlugin(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void BukkitRunnables() {
        new BukkitRunnable() {
            @Override
            public void run() {
                DungeonCore.WhileInDungeon();
            }
        }.runTaskTimer(this, 0L, 20L * config.getInt("MobSpawnPauseSeconds"));

        new BukkitRunnable() {
            @Override
            public void run() {
                DungeonCore.difficultyLoop();
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    public void waveDifficulties() {
        for (int i = 1; i <= 10; ++i) {
            ConfigurationSection config = getConfig().getConfigurationSection("difficulties." + i);
            if(config == null) {
                break;
            }
            WavesCore.waves.add(new WavesCore(config));
        }
        DungeonCore.setPlayerSpawnLocation(config.getLocation("start-location"));
    }
}
