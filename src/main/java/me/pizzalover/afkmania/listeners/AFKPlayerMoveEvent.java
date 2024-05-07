package me.pizzalover.afkmania.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.pizzalover.afkmania.Main;
import me.pizzalover.afkmania.modules.AFKPoolModules;
import me.pizzalover.afkmania.modules.manager.ModuleManager;
import me.pizzalover.afkmania.player_info.afk_pools.AFKPoolPlayerData;
import me.pizzalover.afkmania.utils.config.modules.afkPoolsConfig;
import me.pizzalover.afkmania.utils.config.settingConfig;
import me.pizzalover.afkmania.utils.utils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AFKPlayerMoveEvent implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        AFKPoolModules afkPoolModules = (AFKPoolModules) Main.getModuleManager().getModule("AFKPool");

        AFKPoolPlayerData playerData = null;

        for(AFKPoolPlayerData tempPlayerData : afkPoolModules.player_data_afk_pool) {
            if(tempPlayerData.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                playerData = tempPlayerData;
                break;
            }
        }

        if(playerData == null) {
            return;
        }

        playerData.getPlayer().resetTitle();


        afkPoolModules.player_data_afk_pool.remove(playerData);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        checkPlayerRegion(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        checkPlayerRegion(player);
    }

    private void checkPlayerRegion(Player player) {
        World world = player.getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));

        if (regions == null) {
            Main.getInstance().getLogger().warning("Region manager is not available for this world.");
            return;
        }

        ProtectedRegion region = regions.getRegion(afkPoolsConfig.getConfig().getString("region_name"));
        if (region != null && region.contains(BukkitAdapter.adapt(player.getLocation()).getBlockX(), BukkitAdapter.adapt(player.getLocation()).getBlockY(), BukkitAdapter.adapt(player.getLocation()).getBlockZ())) {
            // Player is in region
            AFKPoolModules afkPoolModules = (AFKPoolModules) Main.getModuleManager().getModule("AFKPool");

            AFKPoolPlayerData playerData = null;

            for(AFKPoolPlayerData tempPlayerData : afkPoolModules.player_data_afk_pool) {
                if(tempPlayerData.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                    playerData = tempPlayerData;
                    break;
                }
            }

            if(playerData == null) {
                playerData = new AFKPoolPlayerData(player, 0);

                afkPoolModules.player_data_afk_pool.add(playerData);
            }



        } else {
            // Player isn't in region
            AFKPoolModules afkPoolModules = (AFKPoolModules) Main.getModuleManager().getModule("AFKPool");

            AFKPoolPlayerData playerData = null;

            for(AFKPoolPlayerData tempPlayerData : afkPoolModules.player_data_afk_pool) {
                if(tempPlayerData.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                    playerData = tempPlayerData;
                    break;
                }
            }

            if(playerData == null) {
                return;
            }

            if(afkPoolsConfig.getConfig().getBoolean("afk-message.leaving-afk.send-message.enabled"))
                playerData.getPlayer().sendMessage(utils.translate(afkPoolsConfig.getConfig().getString("afk-message.leaving-afk.send-message.message"))
                        .replace("{prefix}", utils.translate(settingConfig.getConfig().getString("prefix")))
                        .replace("{seconds}", playerData.getAFKPoolTime() + "")
                );

            playerData.getPlayer().resetTitle();
            playerData.getPlayer().sendTitle(utils.translate(afkPoolsConfig.getConfig().getString("afk-message.leaving-afk.title")),
                    utils.translate(afkPoolsConfig.getConfig().getString("afk-message.leaving-afk.subtitle")),
                    0,
                    10,
                    5);


            afkPoolModules.player_data_afk_pool.remove(playerData);

        }
    }

}
