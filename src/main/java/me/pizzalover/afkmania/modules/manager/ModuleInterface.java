package me.pizzalover.afkmania.modules.manager;

public interface ModuleInterface {

    boolean isEnabled();

    void setEnabled(boolean enabled);

    String getName();

    void enable();

    void disable();
}
