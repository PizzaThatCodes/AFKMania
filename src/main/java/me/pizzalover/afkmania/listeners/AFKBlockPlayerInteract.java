package me.pizzalover.afkmania.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.pizzalover.afkmania.Main;
import me.pizzalover.afkmania.gui.AFKBlock.AFKBlockGUI;
import me.pizzalover.afkmania.modules.AFKBlockModules;
import me.pizzalover.afkmania.player_info.afk_block.AFKBlockPlayerData;
import me.pizzalover.afkmania.utils.utils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AFKBlockPlayerInteract implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        AFKBlockModules afkBlockModules = (AFKBlockModules) Main.getModuleManager().getModule("AFKBlock");

        AFKBlockPlayerData playerData = afkBlockModules.getPlayerData(player);

        if (playerData == null) {
            return;
        }

        playerData.getPlayer().resetTitle();
        restoreDefaultBlock(playerData, afkBlockModules);
        afkBlockModules.saveUpgrades(playerData);
        afkBlockModules.player_data_afk_block.remove(playerData);
    }

    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        AFKBlockModules afkBlockModules = (AFKBlockModules) Main.getModuleManager().getModule("AFKBlock");
        if (block.getLocation().equals(afkBlockModules.getBlockLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {


        if (event.getClickedBlock() == null) {
            return;
        }

        Player player = event.getPlayer();
        AFKBlockModules afkBlockModules = (AFKBlockModules) Main.getModuleManager().getModule("AFKBlock");

        if (!event.getClickedBlock().getLocation().equals(afkBlockModules.getBlockLocation())) {
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            AFKBlockPlayerData playerData = afkBlockModules.getPlayerData(player);
            if (playerData != null) {
                return;
            }

            afkBlockModules.getOrCreatePlayerData(player);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            player.sendMessage(utils.translate(utils.addPlaceholderToText(player, Main.getInstance().getMessageConfig().getConfig().getString("afk_block.gui.opening_gui")
                    .replace("%prefix%", Main.getInstance().getSettingConfig().getConfig().getString("prefix"))
            )));
            AFKBlockGUI.openUpgradeGUI(player);

        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        AFKBlockModules afkBlockModules = (AFKBlockModules) Main.getModuleManager().getModule("AFKBlock");

        if (player.getLocation().distance(afkBlockModules.getBlockLocation()) > Main.getAfkBlockConfig().getConfig().getInt("block_settings.block_distance")) {
            AFKBlockPlayerData playerData = afkBlockModules.getPlayerData(player);

            if (playerData == null) {
                return;
            }

            if (Main.getAfkBlockConfig().getConfig().getBoolean("afk-message.leaving-afk.send-message.enabled"))
                playerData.getPlayer().sendMessage(utils.translate(Main.getAfkBlockConfig().getConfig().getString("afk-message.leaving-afk.send-message.message"))
                        .replace("%prefix%", utils.translate(Main.getSettingConfig().getConfig().getString("prefix")))
                        .replace("{seconds}", playerData.getAFKBlockTimeSeconds() + "")
                );

            playerData.getPlayer().resetTitle();
            playerData.getPlayer().sendTitle(utils.translate(Main.getAfkBlockConfig().getConfig().getString("afk-message.leaving-afk.title")),
                    utils.translate(Main.getAfkBlockConfig().getConfig().getString("afk-message.leaving-afk.subtitle")),
                    0,
                    10,
                    5);

            restoreDefaultBlock(playerData, afkBlockModules);
            afkBlockModules.saveUpgrades(playerData);
            afkBlockModules.player_data_afk_block.remove(playerData);
        }
    }

    private void restoreDefaultBlock(AFKBlockPlayerData playerData, AFKBlockModules afkBlockModules) {
        BlockState blockstate = afkBlockModules.getBlockLocation().getBlock().getState().copy();
        blockstate.setType(XMaterial.valueOf(Main.getAfkBlockConfig().getConfig().getString("block_settings.original_block")).parseMaterial());

        playerData.getPlayer().sendBlockChange(afkBlockModules.getBlockLocation(), blockstate.getBlockData());
    }

}
