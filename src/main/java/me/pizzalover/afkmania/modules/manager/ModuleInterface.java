package me.pizzalover.afkmania.modules.manager;

public interface ModuleInterface {

    /**
     * Check if the module is enabled
     * @return if the module is enabled
     */
    boolean isEnabled();

    /**
     * Set the module to enabled or disabled
     * @param enabled if the module is enabled
     */
    void setEnabled(boolean enabled);

    /**
     * Get the name of the module
     * @return the name of the module
     */
    String getName();

    /**
     * Function when the module is enabled
     */
    void enable();

    /**
     * Function when the module is disabled
     */
    void disable();

    /**
     * Function to call on the onEnable event, doesn't matter if it's enabled or not
     */
    void onEnable();

    /**
     * Function to call on the onDisable event, doesn't matter if it's enabled or not
     */
    void onDisable();
}
