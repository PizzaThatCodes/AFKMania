package me.pizzalover.afkmania.player_info.afk_block;

import org.bukkit.entity.Player;

public class AFKBlockPlayerData {

    Player player;
    int afk_block_time;

    /**
     * Constructor for the player data afk pool
     * @param player the player
     * @param afk_block_time the afk pool time of the player
     */
    public AFKBlockPlayerData(Player player, int afk_block_time) {
        this.player = player;
        this.afk_block_time = afk_block_time;
    }

    /**
     * Get the afk pool time of the player
     * @return the afk pool time of the player
     */
    public int getAFKBlockTime() {
        return afk_block_time;
    }

    /**
     * Set the afk pool time of the player
     * @param afk_block_time the afk pool time of the player
     */
    public void setAFKBlockTime(int afk_block_time) {
        this.afk_block_time = afk_block_time;
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
