package me.wyzebb.playerviewdistancecontroller;

import me.wyzebb.playerviewdistancecontroller.commands.CommandManager;
import me.wyzebb.playerviewdistancecontroller.events.JoinLeaveEvent;
import me.wyzebb.playerviewdistancecontroller.events.NotAfkEvents;
import me.wyzebb.playerviewdistancecontroller.utility.ProcessConfigMessagesUtility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PlayerViewDistanceController extends JavaPlugin {
    private FileConfiguration prefixesConfig;
    private Map<UUID, Integer> playerAfkMap = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("Plugin started!");

        // Config
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        createPrefixesConfig();

        // Register join and leave events
        getServer().getPluginManager().registerEvents(new JoinLeaveEvent(this), this);
        getServer().getPluginManager().registerEvents(new NotAfkEvents(this), this);

        // Register commands and tab completer
        Objects.requireNonNull(getCommand("viewdistance")).setExecutor(new CommandManager(this));
        Objects.requireNonNull(getCommand("viewdistance")).setTabCompleter(new CommandManager(this));

        // Start AFK checker if enabled in the config
        if (getConfig().getBoolean("afk-chunk-limiter")) {
            new CheckAfk().runTaskTimer(this, 0, 20L);
        }
    }

    public void updateLastMoved(Player player) {
        final UUID playerId = player.getUniqueId();
        if (!playerAfkMap.containsKey(playerId)) getLogger().warning("No longer AFK");
        playerAfkMap.put(playerId, (int) System.currentTimeMillis());
    }

    private class CheckAfk extends BukkitRunnable {
        @Override
        public void run() {
            int currentTime = (int) System.currentTimeMillis();
            for (Player player : Bukkit.getOnlinePlayers()) {
                final UUID playerId = player.getUniqueId();
                int lastMoved = playerAfkMap.getOrDefault(playerId, currentTime);

                if (currentTime - lastMoved > (getConfig().getInt("afkTime")) * 1000) {
                    // SET VIEW DISTANCE AND DO NOT SAVE THAT TO FILE: SAVE ORIGINAL TO FILE HERE FIRST

                    getLogger().warning("AFK");
                    ProcessConfigMessagesUtility.processMessage("afk-msg", player);
                    playerAfkMap.remove(playerId);
                }
            }
        }
    }

    public FileConfiguration getPrefixesConfig() {
        return this.prefixesConfig;
    }

    private void createPrefixesConfig() {
        File prefixesConfigFile = new File(getDataFolder(), "prefixes.yml");
        if (!prefixesConfigFile.exists()) {
            prefixesConfigFile.getParentFile().mkdirs();
            saveResource("prefixes.yml", false);
        }

        prefixesConfig = YamlConfiguration.loadConfiguration(prefixesConfigFile);
    }

    @Override
    public void onDisable() {
        playerAfkMap.clear();

        getLogger().info("Plugin shut down!");
    }
}
