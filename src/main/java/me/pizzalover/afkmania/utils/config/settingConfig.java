package me.pizzalover.afkmania.utils.config;

import me.pizzalover.afkmania.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class settingConfig {

    static String fileName = "config.yml";
    
    public static File configFile = new File(Main.getInstance().getDataFolder(), fileName);
    public static FileConfiguration databaseConfig = YamlConfiguration.loadConfiguration(configFile);

    public static FileConfiguration getConfig() {
        return databaseConfig;
    }

    public static File getConfigFile() {
        return configFile;
    }

    public static void saveConfig() {
        configFile = new File(Main.getInstance().getDataFolder(), fileName);
        databaseConfig = YamlConfiguration.loadConfiguration(configFile);
        try {
            databaseConfig.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setConfig(String path, String value) {
        databaseConfig.set(path, value);
        try {
            getConfig().save(getConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();
    }
    public static void setConfig(String path, long value) {
        databaseConfig.set(path, value);
        try {
            getConfig().save(getConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();
    }

    public static void reloadConfig() {
            if (configFile == null) {
                configFile = new File(Main.getInstance().getDataFolder(), fileName);
            }
            databaseConfig = YamlConfiguration.loadConfiguration(configFile);

            // Look for defaults in the jar
            File defConfigStream = new File(Main.getInstance().getDataFolder(), fileName);
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                databaseConfig.setDefaults(defConfig);
            }
    }


    public static void updateConfig() {
        if (!new File(Main.getInstance().getDataFolder() + "/" + fileName).exists()) {
            Main.getInstance().saveResource(fileName, false);
        }

        File langFile = new File(Main.getInstance().getDataFolder() + "/" + fileName);
        YamlConfiguration externalYamlConfig = YamlConfiguration.loadConfiguration(langFile);

        InputStreamReader defConfigStream = new InputStreamReader(Main.getInstance().getResource(fileName), StandardCharsets.UTF_8);
        YamlConfiguration internalLangConfig = YamlConfiguration.loadConfiguration(defConfigStream);

        // Gets all the keys inside the internal file and iterates through all of it's key pairs
        for (String string : internalLangConfig.getKeys(true)) {
            // Checks if the external file contains the key already.
            if (!externalYamlConfig.contains(string)) {
                // If it doesn't contain the key, we set the key based off what was found inside the plugin jar
                externalYamlConfig.set(string, internalLangConfig.get(string));
            }
        }
        try {
            externalYamlConfig.save(langFile);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

}
