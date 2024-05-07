package me.pizzalover.afkmania;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import me.pizzalover.afkmania.commands.afkManiaReload;
import me.pizzalover.afkmania.modules.AFKBlockModules;
import me.pizzalover.afkmania.modules.AFKPoolModules;
import me.pizzalover.afkmania.modules.manager.ModuleInterface;
import me.pizzalover.afkmania.modules.manager.ModuleManager;
import me.pizzalover.afkmania.utils.config.modules.afkPoolsConfig;
import me.pizzalover.afkmania.utils.config.settingConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static AFKPoolModules afkPoolModule;

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

        // Updating the config files
        settingConfig.updateConfig();
        afkPoolsConfig.updateConfig();

        settingConfig.saveConfig();
        settingConfig.reloadConfig();

        afkPoolsConfig.saveConfig();
        afkPoolsConfig.reloadConfig();






        AFKPoolModules afkPoolModule = new AFKPoolModules();
        if(settingConfig.getConfig().getBoolean("modules.afk_pool.enabled")) {
            getModuleManager().enableModule(afkPoolModule);
        } else {
            getModuleManager().disableModule(afkPoolModule);
        }

        AFKBlockModules afkBlockModule = new AFKBlockModules();
        if(settingConfig.getConfig().getBoolean("modules.afk_block.enabled")) {
            getModuleManager().enableModule(afkBlockModule);
        } else {
            getModuleManager().disableModule(afkBlockModule);
        }



        getLogger().info("AFKMania has been enabled!");

        getLogger().info("---------------------------------");
        for(ModuleInterface module : getModuleManager().getAllModules()) {
            if(module.isEnabled()) {
                getLogger().info("| [+] " + module.getName() + " |");
            } else {
                getLogger().info("| [-] " + module.getName() + " |");
            }
        }
        getLogger().info("---------------------------------");


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
