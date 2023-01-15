package me.serbob.devroomsimpledungeon.DungeonSystem.Mobs;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class CustomMobs {
    private EntityType mobType;
    private Entity spawned;

    public CustomMobs(ConfigurationSection config) {
        mobType = EntityType.valueOf(config.getString("type"));
    }
    public CustomMobs(CustomMobs mob) {
        mobType = mob.getMobType();
    }
    public Entity spawn(World world, Location location) {
        LivingEntity e = (LivingEntity) world.spawnEntity(location, mobType);
        spawned=e;
        return e;
    }
    public Entity getSpawned() {
        return spawned;
    }
    public EntityType getMobType() {
        return mobType;
    }
}
