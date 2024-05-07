package me.pizzalover.afkmania.modules;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import me.pizzalover.afkmania.Main;
import me.pizzalover.afkmania.listeners.AFKPlayerMoveEvent;
import me.pizzalover.afkmania.modules.manager.ModuleInterface;
import me.pizzalover.afkmania.player_info.afk_pools.AFKPoolPlayerData;
import me.pizzalover.afkmania.utils.config.modules.afkPoolsConfig;
import me.pizzalover.afkmania.utils.utils;
import org.bukkit.Bukkit;
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

    AFKPlayerMoveEvent afkPlayerMoveEvent;


    public ArrayList<AFKPoolPlayerData> player_data_afk_pool;
    public MyScheduledTask afkTimerTask;
    public MyScheduledTask afkMessageTask;

    @Override
    public void enable() {
        if(!isEnabled()) {
            return;
        }
        afkPlayerMoveEvent = new AFKPlayerMoveEvent();
        // Module is enabled, do stuff
        Main.getInstance().getServer().getPluginManager().registerEvents(afkPlayerMoveEvent, Main.getInstance());
        player_data_afk_pool = new ArrayList<>();


        AFKPoolModules afkPoolModules = (AFKPoolModules) Main.getModuleManager().getModule("AFKPool");
        afkTimerTask = Main.getInstance().getScheduler().runTaskTimer(() -> {
            for(AFKPoolPlayerData playerData : afkPoolModules.player_data_afk_pool) {
                playerData.setAFKPoolTime(playerData.getAFKPoolTime() + 1);

                if(playerData.getAFKPoolTime() % afkPoolsConfig.getConfig().getInt("reward_interval_seconds") == 0 && playerData.getPlayer().getInventory().firstEmpty() != -1) {
                    rewardPlayer(playerData.getPlayer());
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
        if(isEnabled()) {
            return;
        }
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



    private void rewardPlayer(Player player) {
        ConfigurationSection rewardsSection = afkPoolsConfig.getConfig().getConfigurationSection("rewards");
        if (rewardsSection == null) {
            Main.getInstance().getLogger().severe("Rewards section is missing in the config file.");
            return;
        }

        List<Reward> rewards = new ArrayList<>();
        int totalWeight = 0;

        for (String key : rewardsSection.getKeys(false)) {
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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
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
