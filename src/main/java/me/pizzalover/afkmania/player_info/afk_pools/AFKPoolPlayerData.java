package me.pizzalover.afkmania.player_info.afk_pools;

import org.bukkit.entity.Player;

public class AFKPoolPlayerData {

    Player player;
    int afk_pool_time;
    boolean is_afk;

    /**
     * Constructor for the player data afk pool
     * @param player the player
     * @param afk_pool_time the afk pool time of the player
     * @param is_afk the afk status of the player
     */
    public AFKPoolPlayerData(Player player, int afk_pool_time, boolean is_afk) {
        this.player = player;
        this.afk_pool_time = afk_pool_time;
        this.is_afk = is_afk;
    }

    /**
     * Get the afk pool time of the player
     * @return the afk pool time of the player
     */
    public int getAFKPoolTime() {
        return afk_pool_time;
    }

    /**
     * Set the afk pool time of the player
     * @param afk_pool_time the afk pool time of the player
     */
    public void setAFKPoolTime(int afk_pool_time) {
        this.afk_pool_time = afk_pool_time;
    }

    /**
     * Get the afk status of the player
     * @return the afk status of the player
     */
    public boolean IsAFK() {
        return is_afk;
    }

    /**
     * Set the afk status of the player
     * @param is_afk the afk status of the player
     */
    public void setAFK(boolean is_afk) {
        this.is_afk = is_afk;
    }

    /**
     * Get the player
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Set the player
     * @param player the player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

}
