package me.pizzalover.afkmania.modules;

import me.pizzalover.afkmania.Main;
import me.pizzalover.afkmania.modules.manager.ModuleInterface;
import me.pizzalover.afkmania.utils.config.modules.afkBlockConfig;
import me.pizzalover.afkmania.utils.config.modules.afkPoolsConfig;

public class AFKBlockModules implements ModuleInterface {

    private boolean enabled = false;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getName() {
        return "AFKBlock";
    }

    @Override
    public void enable() {
        if(!isEnabled()) {
            return;
        }


    }

    @Override
    public void disable() {

        if(!isEnabled()) {
            return;
        }

    }

    @Override
    public void onEnable() {
        if(!afkBlockConfig.getConfigFile().exists()) {
            Main.getInstance().saveResource("modules/afk_block.yml", false);
        }
        afkBlockConfig.updateConfig();

        afkBlockConfig.saveConfig();
        afkBlockConfig.reloadConfig();
    }

    @Override
    public void onDisable() {

    }
}
