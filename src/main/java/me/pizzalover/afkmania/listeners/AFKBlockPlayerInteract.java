package me.pizzalover.afkmania.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.pizzalover.afkmania.Main;
import me.pizzalover.afkmania.modules.AFKBlockModules;
import me.pizzalover.afkmania.modules.AFKPoolModules;
import me.pizzalover.afkmania.player_info.afk_block.AFKBlockPlayerData;
import me.pizzalover.afkmania.player_info.afk_pools.AFKPoolPlayerData;
import me.pizzalover.afkmania.utils.config.messageConfig;
import me.pizzalover.afkmania.utils.config.modules.afkBlockConfig;
import me.pizzalover.afkmania.utils.config.modules.afkPoolsConfig;
import me.pizzalover.afkmania.utils.config.settingConfig;
import me.pizzalover.afkmania.utils.utils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AFKBlockPlayerInteract implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        AFKBlockModules afkBlockModules = (AFKBlockModules) Main.getModuleManager().getModule("AFKBlock");

        AFKBlockPlayerData playerData = null;

        for(AFKBlockPlayerData tempPlayerData : afkBlockModules.player_data_afk_block) {
            if(tempPlayerData.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                playerData = tempPlayerData;
                break;
            }
        }

        if(playerData == null) {
            return;
        }

        playerData.getPlayer().resetTitle();


        BlockState blockstate = afkBlockModules.getBlockLocation().getBlock().getState().copy();
        blockstate.setType(XMaterial.valueOf(afkBlockConfig.getConfig().getString("block_settings.original_block")).parseMaterial());

        playerData.getPlayer().sendBlockChange(afkBlockModules.getBlockLocation(), blockstate.getBlockData());

        afkBlockModules.player_data_afk_block.remove(playerData);
    }

    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        AFKBlockModules afkBlockModules = (AFKBlockModules) Main.getModuleManager().getModule("AFKBlock");
        if(block.getLocation().equals(afkBlockModules.getBlockLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {


        if(event.getClickedBlock() == null) {
            return;
        }

        Player player = event.getPlayer();
        AFKBlockModules afkBlockModules = (AFKBlockModules) Main.getModuleManager().getModule("AFKBlock");

        if(!event.getClickedBlock().getLocation().equals(afkBlockModules.getBlockLocation())) {
            return;
        }

        AFKBlockPlayerData playerData = null;

        for(AFKBlockPlayerData tempPlayerData : afkBlockModules.player_data_afk_block) {
            if(tempPlayerData.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                playerData = tempPlayerData;
                break;
            }
        }

        if(playerData != null) {
            return;
        }

        playerData = new AFKBlockPlayerData(player, 0);
        afkBlockModules.player_data_afk_block.add(playerData);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        AFKBlockModules afkBlockModules = (AFKBlockModules) Main.getModuleManager().getModule("AFKBlock");

        if(player.getLocation().distance(afkBlockModules.getBlockLocation()) > afkBlockConfig.getConfig().getInt("block_settings.block_distance")) {
            AFKBlockPlayerData playerData = null;

            for(AFKBlockPlayerData tempPlayerData : afkBlockModules.player_data_afk_block) {
                if(tempPlayerData.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                    playerData = tempPlayerData;
                    break;
                }
            }

            if(playerData == null) {
                return;
            }

            if (afkBlockConfig.getConfig().getBoolean("afk-message.leaving-afk.send-message.enabled"))
                playerData.getPlayer().sendMessage(utils.translate(afkBlockConfig.getConfig().getString("afk-message.leaving-afk.send-message.message"))
                        .replace("%prefix%", utils.translate(settingConfig.getConfig().getString("prefix")))
                        .replace("{seconds}", playerData.getAFKBlockTimeSeconds() + "")
                );

            playerData.getPlayer().resetTitle();
            playerData.getPlayer().sendTitle(utils.translate(afkBlockConfig.getConfig().getString("afk-message.leaving-afk.title")),
                    utils.translate(afkBlockConfig.getConfig().getString("afk-message.leaving-afk.subtitle")),
                    0,
                    10,
                    5);


            BlockState blockstate = afkBlockModules.getBlockLocation().getBlock().getState().copy();
            blockstate.setType(XMaterial.valueOf(afkBlockConfig.getConfig().getString("block_settings.original_block")).parseMaterial());

            playerData.getPlayer().sendBlockChange(afkBlockModules.getBlockLocation(), blockstate.getBlockData());
            afkBlockModules.player_data_afk_block.remove(playerData);
        }
    }

}
