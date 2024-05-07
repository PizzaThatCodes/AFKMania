package me.pizzalover.afkmania.modules;

import me.pizzalover.afkmania.modules.manager.ModuleInterface;

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
}
