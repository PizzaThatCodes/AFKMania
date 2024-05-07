package me.pizzalover.afkmania.modules.manager;

import java.util.ArrayList;

public class ModuleManager {

    private ArrayList<ModuleInterface> modules = new ArrayList<>();

    /**
     * Enable a module
     * @param module the module to enable
     */
    public void enableModule(ModuleInterface module){
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
}

