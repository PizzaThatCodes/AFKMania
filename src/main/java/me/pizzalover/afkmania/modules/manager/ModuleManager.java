package me.pizzalover.afkmania.modules.manager;

import me.pizzalover.afkmania.Main;
import me.pizzalover.afkmania.modules.AFKBlockModules;
import me.pizzalover.afkmania.modules.AFKPoolModules;
import me.pizzalover.afkmania.utils.config.settingConfig;

import java.util.ArrayList;

public class ModuleManager {

    private ArrayList<ModuleInterface> modules = new ArrayList<>();

    /**
     * Enable a module
     * @param module the module to enable
     */
    public void enableModule(ModuleInterface module){
        if(modules.contains(module)){
            module.enable();
            module.setEnabled(true);
            return;
        }
        modules.add(module);
        module.setEnabled(true);
        module.enable();
    }

    /**
     * Disable a module
     * @param module the module to disable
     */
    public void disableModule(ModuleInterface module){
        if(modules.contains(module)){
            module.disable();
            module.setEnabled(false);
            return;
        }
        modules.add(module);
        module.setEnabled(false);
        module.disable();
    }

    /**
     * Get all the modules
     * @return all the modules
     */
    public ArrayList<ModuleInterface> getAllModules() {
        return modules;
    }

    /**
     * Check if a module is enabled
     * @param moduleName the name of the module
     * @return if the module is enabled
     */
    public boolean isModuleEnabled(String moduleName) {
        for(ModuleInterface module : getAllModules()) {
            if(module.getName().equalsIgnoreCase(moduleName)) {
                return module.isEnabled();
            }
        }
        return false;
    }

    /**
     * Get a module by name
     * @param moduleName the name of the module
     * @return the module
     */
    public ModuleInterface getModule(String moduleName) {
        for (ModuleInterface module : getAllModules()) {
            if (module.getName().equalsIgnoreCase(moduleName)) {
                return module;
            }
        }
        return null;
    }


    /**
     * Check the modules in the config to see if they are enabled or disabled, and to enable or disable them.
     */
    public void checkConfigModules() {

        for(ModuleInterface module : getAllModules()) {
            if(module.isEnabled()) {
                module.disable();
            }
        }
        if(getAllModules().size() > 0)
            getAllModules().clear();

        AFKPoolModules afkPoolModule = new AFKPoolModules();
        if(settingConfig.getConfig().getBoolean("modules.afk_pool.enabled")) {
            enableModule(afkPoolModule);
        } else {
            disableModule(afkPoolModule);
        }

        AFKBlockModules afkBlockModule = new AFKBlockModules();
        if(settingConfig.getConfig().getBoolean("modules.afk_block.enabled")) {
            enableModule(afkBlockModule);
        } else {
            disableModule(afkBlockModule);
        }


        Main.getInstance().getLogger().info("---------------------------------");
        for(ModuleInterface module : getAllModules()) {
            if(module.isEnabled()) {
                Main.getInstance().getLogger().info("| [+] " + module.getName() + " |");
            } else {
                Main.getInstance().getLogger().info("| [-] " + module.getName() + " |");
            }
        }
        Main.getInstance().getLogger().info("---------------------------------");

    }

}

