package me.pizzalover.afkmania.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.pizzalover.afkmania.Main;
import me.pizzalover.afkmania.modules.AFKPoolModules;
import me.pizzalover.afkmania.player_info.afk_pools.AFKPoolPlayerData;
import me.pizzalover.afkmania.utils.utils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class AFKPoolPlayerMoveEvent implements Listener {

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

        ArrayList<String> region_list = new ArrayList<>();
        for (String afk_pool_region_list : Main.getAfkPoolsConfig().getConfig().getConfigurationSection("afk_pools").getKeys(false)) {
            region_list.add(Main.getAfkPoolsConfig().getConfig().getString("afk_pools." + afk_pool_region_list + ".region_name"));
//            player.sendMessage(afk_pool_region_list);
        }


        AFKPoolModules afkPoolModules = (AFKPoolModules) Main.getModuleManager().getModule("AFKPool");

        AFKPoolPlayerData playerData = null;


        for (AFKPoolPlayerData tempPlayerData : afkPoolModules.player_data_afk_pool) {
            if (tempPlayerData.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                playerData = tempPlayerData;
                break;
            }
        }

        for (String region_name : region_list) {
            ProtectedRegion region = regions.getRegion(region_name);
            if (region != null && region.contains(BukkitAdapter.adapt(player.getLocation()).getBlockX(), BukkitAdapter.adapt(player.getLocation()).getBlockY(), BukkitAdapter.adapt(player.getLocation()).getBlockZ())) {
                // Player is in region

                if (playerData == null) {
                    playerData = new AFKPoolPlayerData(player, 0, region_name);

                    afkPoolModules.player_data_afk_pool.add(playerData);
                }

                return;
            }

        }

        if (playerData == null) {
            return;
        }

        if (Main.getAfkPoolsConfig().getConfig().getBoolean("afk-message.leaving-afk.send-message.enabled"))
            playerData.getPlayer().sendMessage(utils.translate(Main.getAfkPoolsConfig().getConfig().getString("afk-message.leaving-afk.send-message.message"))
                    .replace("%prefix%", utils.translate(Main.getSettingConfig().getConfig().getString("prefix")))
                    .replace("{seconds}", playerData.getAFKPoolTimeSeconds() + "")
            );

        playerData.getPlayer().resetTitle();
        playerData.getPlayer().sendTitle(utils.translate(Main.getAfkPoolsConfig().getConfig().getString("afk-message.leaving-afk.title")),
                utils.translate(Main.getAfkPoolsConfig().getConfig().getString("afk-message.leaving-afk.subtitle")),
                0,
                10,
                5);


        afkPoolModules.player_data_afk_pool.remove(playerData);


    }


}
