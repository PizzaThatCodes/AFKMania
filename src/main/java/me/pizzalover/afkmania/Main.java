package me.pizzalover.afkmania;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import me.pizzalover.afkmania.commands.afkManiaReload;
import me.pizzalover.afkmania.modules.AFKBlockModules;
import me.pizzalover.afkmania.modules.AFKPoolModules;
import me.pizzalover.afkmania.modules.manager.ModuleInterface;
import me.pizzalover.afkmania.modules.manager.ModuleManager;
import me.pizzalover.afkmania.utils.config.messageConfig;
import me.pizzalover.afkmania.utils.config.modules.afkPoolsConfig;
import me.pizzalover.afkmania.utils.config.settingConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private static TaskScheduler scheduler;
    private static ModuleManager moduleManager;

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

    @Override
    public void onEnable() {
        this.instance = this;
        this.moduleManager = new ModuleManager();
        scheduler = UniversalScheduler.getScheduler(this);

        if(!settingConfig.getConfigFile().exists()) {
            saveResource("config.yml", false);
        }

        if(!afkPoolsConfig.getConfigFile().exists()) {
            saveResource("modules/afk_pool.yml", false);
        }

        if(!messageConfig.getConfigFile().exists()) {
            saveResource("messages.yml", false);
        }

        // Updating the config files
        settingConfig.updateConfig();
        afkPoolsConfig.updateConfig();
        messageConfig.updateConfig();

        settingConfig.saveConfig();
        settingConfig.reloadConfig();

        afkPoolsConfig.saveConfig();
        afkPoolsConfig.reloadConfig();

        messageConfig.saveConfig();
        messageConfig.reloadConfig();






        // Registering the modules
        getModuleManager().checkConfigModules();



        getCommand("afkmania").setExecutor(new afkManiaReload());


        getScheduler().runTaskTimerAsynchronously(() -> {
            if(!getModuleManager().isModuleEnabled("AFKPool")) {
                AFKPoolModules afkPoolModules = (AFKPoolModules) getModuleManager().getModule("AFKPool");
                if(afkPoolModules.afkTimerTask != null && afkPoolModules.afkMessageTask != null) {
                    afkPoolModules.afkTimerTask.cancel();
                    afkPoolModules.afkMessageTask.cancel();
                }
            }
        }, 0, 5L);



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