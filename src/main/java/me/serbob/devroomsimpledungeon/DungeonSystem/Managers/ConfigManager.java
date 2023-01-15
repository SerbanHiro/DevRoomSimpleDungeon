package me.serbob.devroomsimpledungeon.DungeonSystem.Managers;

import me.serbob.devroomsimpledungeon.DevRoomSimpleDungeon;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigManager {
    public static ConfigManager instance=null;
    private YamlConfiguration file;
    File userDataFolder;
    File userDataFile;
    public static ConfigManager getInstance() {
        if(instance==null)
            instance=new ConfigManager();
        return instance;
    }
    public void verifyAlreadyUserdata(Player player) {
        userDataFolder = new File(DevRoomSimpleDungeon.instance.getDataFolder(), "userdata");
        userDataFile = new File(userDataFolder, player.getUniqueId()+".yml");
        if(!userDataFile.exists()) {
            try {
                userDataFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void setInventory(Player player, ItemStack[] oldInventory) {
        userDataFolder = new File(DevRoomSimpleDungeon.instance.getDataFolder(), "userdata");
        userDataFile = new File(userDataFolder, player.getUniqueId()+".yml");
        file = YamlConfiguration.loadConfiguration(userDataFile);
        file.set("oldInventory",oldInventory);
        try {
            file.save(userDataFile);
            file.load(userDataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLocation(Player player, Location oldLocation) {
        userDataFolder = new File(DevRoomSimpleDungeon.instance.getDataFolder(), "userdata");
        userDataFile = new File(userDataFolder, player.getUniqueId()+".yml");
        file = YamlConfiguration.loadConfiguration(userDataFile);
        file.set("oldLocation",oldLocation);
        try {
            file.save(userDataFile);
            file.load(userDataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadInventory(Player player) {
        userDataFolder = new File(DevRoomSimpleDungeon.instance.getDataFolder(), "userdata");
        userDataFile = new File(userDataFolder, player.getUniqueId()+".yml");
        file = YamlConfiguration.loadConfiguration(userDataFile);
        List<ItemStack> itemList = (List<ItemStack>) file.getList("oldInventory");
        System.out.println(itemList);
        ItemStack[] itemArray = itemList.toArray(new ItemStack[itemList.size()]);
        System.out.println(itemArray);
        player.getInventory().setContents(itemArray);
    }
    public void loadLocation(Player player) {
        userDataFolder = new File(DevRoomSimpleDungeon.instance.getDataFolder(), "userdata");
        userDataFile = new File(userDataFolder, player.getUniqueId()+".yml");
        file = YamlConfiguration.loadConfiguration(userDataFile);
        Location location = file.getLocation("oldLocation");
        player.teleport(location);
    }
    public boolean hasInventory(Player player) {
        userDataFolder = new File(DevRoomSimpleDungeon.instance.getDataFolder(), "userdata");
        userDataFile = new File(userDataFolder, player.getUniqueId()+".yml");
        file = YamlConfiguration.loadConfiguration(userDataFile);
        List<ItemStack> itemList = (List<ItemStack>) file.getList("oldInventory");
        return (itemList==null);
    }
}
