package me.serbob.devroomsimpledungeon.DungeonSystem.Mobs;

import java.util.ArrayList;
import java.util.List;

public class MobsList {
    private List<CustomMobs> mobs;
    public MobsList() {
        this.mobs = new ArrayList<>();
    }
    public void addMob(CustomMobs mob) {
        mobs.add(mob);
    }
    public List<CustomMobs> getMobs() {
        return mobs;
    }
}
