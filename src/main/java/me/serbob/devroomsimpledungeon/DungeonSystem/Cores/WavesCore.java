package me.serbob.devroomsimpledungeon.DungeonSystem.Cores;

import me.serbob.devroomsimpledungeon.DungeonSystem.Mobs.CustomMobs;
import me.serbob.devroomsimpledungeon.DungeonSystem.Mobs.MobsList;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class WavesCore {
    private MobsList mobs;
    private boolean maxDiff = false;
    private int mobCount;
    public static List<WavesCore> waves = new ArrayList<>();
    public WavesCore(ConfigurationSection config) {
        mobs = new MobsList();

        for (int i = 1; i <= 10; i++) {
            ConfigurationSection mobConfig = config.getConfigurationSection("mobs." + i);
            if (mobConfig != null) {
                mobs.addMob(new CustomMobs(mobConfig));
            } else break;
        }
        mobCount = config.getInt("mobsSpawned");
    }
    public int getMobCount() {
        return mobCount;
    }
    public MobsList getMobs() {
        return mobs;
    }
    public WavesCore UpcomingWave() {
        int index = waves.indexOf(this);
        if (index == waves.size() - 1) {
            return null;
        }
        return waves.get(index + 1);
    }
    public WavesCore PreviousWave() {
        int index = waves.indexOf(this);
        if (index == 0) {
            return null;
        }
        return waves.get(index - 1);
    }
    public void PlayerIsAtMaximumWaveDifficulty(boolean max) {
        this.maxDiff = max;
    }
    public boolean PlayerHasReachedTheMaximumWaveDifficulty() {
        return maxDiff;
    }
}
