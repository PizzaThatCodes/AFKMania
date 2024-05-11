package me.pizzalover.afkmania.modules;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import me.pizzalover.afkmania.Main;
import me.pizzalover.afkmania.listeners.AFKPoolPlayerMoveEvent;
import me.pizzalover.afkmania.modules.manager.ModuleInterface;
import me.pizzalover.afkmania.player_info.afk_pools.AFKPoolPlayerData;
import me.pizzalover.afkmania.utils.config.modules.afkPoolsConfig;
import me.pizzalover.afkmania.utils.utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AFKPoolModules implements ModuleInterface {

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
        return "AFKPool";
    }

    AFKPoolPlayerMoveEvent afkPlayerMoveEvent;


    public ArrayList<AFKPoolPlayerData> player_data_afk_pool;
    public MyScheduledTask afkTimerTask;
    public MyScheduledTask afkMessageTask;


    @Override
    public void enable() {

        if(!afkPoolsConfig.getConfigFile().exists()) {
            Main.getInstance().saveResource("modules/afk_pool.yml", false);
        }

        afkPoolsConfig.updateConfig();

        afkPoolsConfig.saveConfig();
        afkPoolsConfig.reloadConfig();

        afkPlayerMoveEvent = new AFKPoolPlayerMoveEvent();
        Main.getInstance().getServer().getPluginManager().registerEvents(afkPlayerMoveEvent, Main.getInstance());
        player_data_afk_pool = new ArrayList<>();


        AFKPoolModules afkPoolModules = (AFKPoolModules) Main.getModuleManager().getModule("AFKPool");
        afkTimerTask = Main.getInstance().getScheduler().runTaskTimer(() -> {
            for(AFKPoolPlayerData playerData : afkPoolModules.player_data_afk_pool) {
                playerData.setAFKPoolTime(playerData.getAFKPoolTime() + 1);

                if(playerData.getAFKPoolTime() % afkPoolsConfig.getConfig().getInt("reward_interval_seconds") == 0 && playerData.getPlayer().getInventory().firstEmpty() != -1) {
                    rewardPlayer(playerData.getPlayer(), playerData.getAFKPoolRegion());
                }

            }

        }, 0, 20L);

        afkMessageTask = Main.getInstance().getScheduler().runTaskTimerAsynchronously(() -> {
            for(AFKPoolPlayerData playerData : afkPoolModules.player_data_afk_pool) {
                if(playerData.getPlayer().getInventory().firstEmpty() == -1) {
                    playerData.getPlayer().sendTitle(utils.translate(afkPoolsConfig.getConfig().getString("afk-message.inventory-full.title")),
                            utils.translate(afkPoolsConfig.getConfig().getString("afk-message.inventory-full.subtitle")),
                            0,
                            5,
                            5
                    );
                    continue;
                } else {
                    playerData.getPlayer().sendTitle(utils.translate(afkPoolsConfig.getConfig().getString("afk-message.inventory-collect.title")),
                            utils.translate(afkPoolsConfig.getConfig().getString("afk-message.inventory-collect.subtitle")),
                            0,
                            5,
                            5
                    );
                }
            }
        }, 0, 1L);


    }

    @Override
    public void disable() {
        if(afkPlayerMoveEvent != null)
            HandlerList.unregisterAll(afkPlayerMoveEvent);
        if(player_data_afk_pool != null) {
            player_data_afk_pool.clear();
            player_data_afk_pool = null;
        }

        if(afkTimerTask != null)
            afkTimerTask.cancel();
        if(afkMessageTask != null)
            afkMessageTask.cancel();


    }

    @Override
    public void onEnable() {


    }

    @Override
    public void onDisable() {

    }


    /**
     * Reward the player with a random reward from the config file
     * @param player the player to reward
     * @param region the region of the afk pool
     */
    private void rewardPlayer(Player player, String region) {
        String region_name = "";
        for(String regions : afkPoolsConfig.getConfig().getConfigurationSection("afk_pools").getKeys(false)) {
            if(afkPoolsConfig.getConfig().getString("afk_pools." + regions + ".region_name").equalsIgnoreCase(region)) {
                region_name = regions;
                break;
            }
        }
        List<String> rewards_to_get = afkPoolsConfig.getConfig().getStringList("afk_pools." + region_name + ".rewards");
        ConfigurationSection rewardsSection = afkPoolsConfig.getConfig().getConfigurationSection("rewards");
        if (rewardsSection == null) {
            Main.getInstance().getLogger().severe("Rewards section is missing in the config file.");
            return;
        }

        List<Reward> rewards = new ArrayList<>();
        int totalWeight = 0;

        for (String key : rewardsSection.getKeys(false)) {
            if(!rewards_to_get.contains(key)) continue;
            String command = rewardsSection.getString(key + ".command");
            int chance = rewardsSection.getInt(key + ".chance");
            rewards.add(new Reward(command, chance));
            totalWeight += chance;
        }

        if (totalWeight == 0) {
            Main.getInstance().getLogger().severe("Total weight of rewards is 0 or no rewards defined.");
            return;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(totalWeight);
        int currentWeight = 0;

        for (Reward reward : rewards) {
            currentWeight += reward.chance;
            if (randomIndex < currentWeight) {
                String command = reward.command.replace("%player%", player.getName());
                utils.runConsoleCommand(command, player.getWorld());
                break;
            }
        }

    }



    static class Reward {
        String command;
        int chance;

        Reward(String command, int chance) {
            this.command = command;
            this.chance = chance;
        }
    }
}
