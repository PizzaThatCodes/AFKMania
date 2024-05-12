package me.pizzalover.afkmania;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import me.pizzalover.afkmania.commands.afkManiaReload;
import me.pizzalover.afkmania.modules.manager.ModuleInterface;
import me.pizzalover.afkmania.modules.manager.ModuleManager;
import me.pizzalover.afkmania.utils.config.configManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class Main extends JavaPlugin {

    private static Main instance;
    private static TaskScheduler scheduler;
    private static ModuleManager moduleManager;

    private static configManager settingConfig;
    private static configManager messageConfig;
    private static configManager afkBlockConfig;
    private static configManager afkPoolsConfig;

    /**
     * Get the instance of the plugin
     * @return the instance of the plugin
     */
    public static Main getInstance() {
        return instance;
    }

    /**
     * Get the scheduler of the plugin
     * @return the scheduler of the plugin
     */
    public static TaskScheduler getScheduler() {
        return scheduler;
    }

    /**
     * Get the module manager of the plugin
     * @return the module manager of the plugin
     */
    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

    /**
     * Get the setting config of the plugin
     * @return the setting config of the plugin
     */
    public static configManager getSettingConfig() {
        return settingConfig;
    }

    /**
     * Get the message config of the plugin
     * @return the message config of the plugin
     */
    public static configManager getMessageConfig() {
        return messageConfig;
    }

    /**
     * Get the afk block config of the plugin
     * @return the afk block config of the plugin
     */
    public static configManager getAfkBlockConfig() {
        return afkBlockConfig;
    }

    /**
     * Get the afk pools config of the plugin
     * @return the afk pools config of the plugin
     */
    public static configManager getAfkPoolsConfig() {
        return afkPoolsConfig;
    }

    @Override
    public void onEnable() {
        this.instance = this;
        this.moduleManager = new ModuleManager();
        scheduler = UniversalScheduler.getScheduler(this);

        settingConfig = new configManager("config.yml");
        messageConfig = new configManager("messages.yml");
        afkBlockConfig = new configManager("modules/afk_block.yml");
        afkPoolsConfig = new configManager("modules/afk_pool.yml");

        // Registering the modules
        getModuleManager().checkConfigModules();

        for(ModuleInterface module : getModuleManager().getAllModules()) {
            module.onEnable();
        }

        if(!settingConfig.getConfigFile().exists()) {
            saveResource("config.yml", false);
        }

        if(!messageConfig.getConfigFile().exists()) {
            saveResource("messages.yml", false);
        }

        // Updating the config files
        settingConfig.updateConfig(null);
        messageConfig.updateConfig(null);

        settingConfig.saveConfig();
        settingConfig.reloadConfig();

        messageConfig.saveConfig();
        messageConfig.reloadConfig();




        getCommand("afkmania").setExecutor(new afkManiaReload());



        getLogger().info("AFKMania has been enabled!");


    }

    @Override
    public void onDisable() {

        getScheduler().cancelTasks();

        for(ModuleInterface module : getModuleManager().getAllModules()) {
            if(module.isEnabled()) {
                module.disable();
                getLogger().warning("Disabling module: " + module.getName());
            }
        }
        getModuleManager().getAllModules().clear();

        getLogger().info("AFKMania has been disabled!");
    }



}
