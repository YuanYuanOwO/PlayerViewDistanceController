package me.wyzebb.playerviewdistancecontroller.utility.lang;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class LanguageManager {
    private final Map<String, FileConfiguration> languages = new HashMap<>();
    private String defaultLanguage;

    public LanguageManager() {
        loadConfig();
        copyDefaultLanguages();
        loadLanguages();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        defaultLanguage = config.getString("language", "en_US"); // Defaults to "en_US" if not set
    }

    private void copyDefaultLanguages() {
        String[] languages = {"en_US.yml"}; // List of all languages provided

        File languagesFolder = new File(plugin.getDataFolder(), "lang");
        if (!languagesFolder.exists()) {
            languagesFolder.mkdirs();
        }

        for (String langFileName : languages) {
            File langFile = new File(languagesFolder, langFileName);
            if (!langFile.exists()) {
                try (InputStream in = plugin.getResource("lang/" + langFileName)) {
                    if (in != null) {
                        Files.copy(in, langFile.toPath());
                        plugin.getLogger().info(langFileName + " successfully copied to lang folder.");
                    } else {
                        plugin.getLogger().warning("Resource file not found: " + langFileName);
                    }
                } catch (IOException e) {
                    plugin.getLogger().warning("Failed to copy language file: " + langFileName);
                    e.printStackTrace();
                }
            } else {
                plugin.getLogger().info(langFileName + " already exists, skipping copy.");
            }
        }
    }


    private void loadLanguages() {
        File languagesFolder = new File(plugin.getDataFolder(), "lang");
        File[] files = languagesFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".yml")) {
                    String lang = file.getName().replace(".yml", "");
                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                    languages.put(lang, config);
                }
            }
        }
    }

    public FileConfiguration getLanguageFile() {
        return languages.getOrDefault(defaultLanguage, languages.get("en_US")); // Default to English if language not found
    }
}