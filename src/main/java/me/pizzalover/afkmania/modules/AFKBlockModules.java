package me.pizzalover.afkmania.modules;

import com.cryptomorin.xseries.XMaterial;
import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.pizzalover.afkmania.Main;
import me.pizzalover.afkmania.listeners.AFKBlockPlayerInteract;
import me.pizzalover.afkmania.modules.manager.ModuleInterface;
import me.pizzalover.afkmania.player_info.afk_block.AFKBlockPlayerData;
import me.pizzalover.afkmania.utils.utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class AFKBlockModules implements ModuleInterface {

    private boolean enabled = false;
    private Location blockLocation;
    private Hologram blockHologram;
    private Location hologramLocation;

    AFKBlockPlayerInteract afkBlockPlayerInteract;
    public ArrayList<AFKBlockPlayerData> player_data_afk_block;

    public MyScheduledTask afkMessageTask;
    public MyScheduledTask afkBlockTask;
    public MyScheduledTask afkBlockMining;

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
        return "AFKBlock";
    }

    @Override
    public void enable() {

        if(!Main.getAfkBlockConfig().getConfigFile().exists()) {
            Main.getInstance().saveResource("modules/afk_block.yml", false);
        }
        Main.getAfkBlockConfig().updateConfig(Arrays.asList("gui", "block_settings.hologram", "upgrades"));

        Main.getAfkBlockConfig().saveConfig();
        Main.getAfkBlockConfig().reloadConfig();


        // Check if DecentHolograms is installed
        if(Main.getInstance().getServer().getPluginManager().getPlugin("DecentHolograms") == null) {
            Main.getInstance().getLogger().severe("DecentHolograms is not installed! Disabling AFKBlock module...");
            Main.getModuleManager().disableModule(this);
            return;
        }

        afkBlockPlayerInteract = new AFKBlockPlayerInteract();
        Main.getInstance().getServer().getPluginManager().registerEvents(afkBlockPlayerInteract, Main.getInstance());
        player_data_afk_block = new ArrayList<>();

        // Load the block location for holograms and that...
        blockLocation = new Location(
                Main.getInstance().getServer().getWorld(Main.getAfkBlockConfig().getConfig().getString("block_location.world")),
                Main.getAfkBlockConfig().getConfig().getDouble("block_location.x"),
                Main.getAfkBlockConfig().getConfig().getDouble("block_location.y"),
                Main.getAfkBlockConfig().getConfig().getDouble("block_location.z")
        );
        blockLocation = blockLocation.getBlock().getLocation();


        hologramLocation = blockLocation.clone();
        hologramLocation.add(hologramLocation.getX() > 0 ? -0.5 : 0.5, 1, hologramLocation.getZ() > 0 ? -0.5 : 0.5);

        // Load the hologram lines
        ArrayList<String> hologramLines = new ArrayList<>();
        for(String hologram_lines : Main.getAfkBlockConfig().getConfig().getStringList("block_settings.hologram")) {
            hologramLocation.add(0, 0.3, 0);
            hologramLines.add(utils.translate(hologram_lines));
        }

        // Create the hologram
        blockHologram = DHAPI.createHologram("AFKBlock", getHologramLocation(), false, hologramLines);



        // Handles the AFK title messages
        afkMessageTask = Main.getScheduler().runTaskTimerAsynchronously(() -> {
            for(AFKBlockPlayerData playerData : player_data_afk_block) {
                if(playerData.getPlayer().getInventory().firstEmpty() == -1) {
                    playerData.getPlayer().sendTitle(utils.translate(Main.getAfkBlockConfig().getConfig().getString("afk-message.inventory-full.title")),
                            utils.translate(Main.getAfkBlockConfig().getConfig().getString("afk-message.inventory-full.subtitle")),
                            0,
                            5,
                            5
                    );
                    continue;
                } else {
                    playerData.getPlayer().sendTitle(utils.translate(Main.getAfkBlockConfig().getConfig().getString("afk-message.inventory-collect.title")),
                            utils.translate(Main.getAfkBlockConfig().getConfig().getString("afk-message.inventory-collect.subtitle")),
                            0,
                            5,
                            5
                    );
                }
            }
        }, 0, 1L);

        // Adds 1 to the AFKBlockTime every second
        afkBlockTask = Main.getScheduler().runTaskTimerAsynchronously(() -> {
            for(AFKBlockPlayerData playerData : player_data_afk_block) {
                playerData.setAFKBlockTimeTicks(playerData.getAFKBlockTimeTicks() + 1);

                if(playerData.getAFKBlockTimeTicks() % (Main.getAfkBlockConfig().getConfig().getDouble("block_settings.break_speed")*20) == 0) {
                    playerData.getPlayer().playSound(playerData.getPlayer().getLocation(), "block.break", 1, 1);

                    BlockState blockstate = getBlockLocation().getBlock().getState().copy();

                    ArrayList<Material> materialList = new ArrayList<>();
                    for(String blocks : Main.getAfkBlockConfig().getConfig().getStringList("blocks")) {
                        Material material = XMaterial.valueOf(blocks).parseMaterial();
                        materialList.add(material);
                    }

                    if(materialList.size() == 0) {
                        Main.getInstance().getLogger().severe("No blocks found in the config! Disabling AFKBlock module...");
                        Main.getModuleManager().disableModule(this);
                        return;
                    }

                    int randomIndex = new Random().nextInt(materialList.size());
                    blockstate.setType(materialList.get(randomIndex));


                    playerData.getPlayer().sendBlockChange(getBlockLocation(), blockstate.getBlockData());

                }

            }
        }, 20L, 1L);

        // Mining task, switch the block every time it changes and reward the player...
        afkBlockMining = Main.getScheduler().runTaskTimerAsynchronously(() -> {
            for(AFKBlockPlayerData playerData : player_data_afk_block) {

//                block_settings:
//                break_speed: 1


            }
        }, 0, 1L);

    }

    @Override
    public void disable() {
        if(Main.getInstance().getServer().getPluginManager().getPlugin("DecentHolograms") != null) {
            if (blockHologram != null)
                blockHologram.delete();
        }
        if(afkBlockPlayerInteract != null)
            HandlerList.unregisterAll(afkBlockPlayerInteract);
        if(player_data_afk_block != null) {
            player_data_afk_block.clear();
            player_data_afk_block = null;
        }
        if(afkMessageTask != null)
            afkMessageTask.cancel();
        if(afkBlockTask != null)
            afkBlockTask.cancel();
        if(afkBlockMining != null)
            afkBlockMining.cancel();

    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {

    }

    /**
     * Get the block location
     * @return the block location
     */
    public Location getBlockLocation() {
        return blockLocation;
    }

    /**
     * Set the block location
     * @param blockLocation the block location
     */
    public void setBlockLocation(Location blockLocation) {
        this.blockLocation = blockLocation;
    }

    /**
     * Get the block hologram
     * @return the block hologram
     */
    public Hologram getBlockHologram() {
        return blockHologram;
    }

    /**
     * Set the block hologram
     * @param blockHologram the block hologram
     */
    public void setBlockHologram(Hologram blockHologram) {
        this.blockHologram = blockHologram;
    }

    /**
     * Get the hologram location
     * @return the hologram location
     */
    public Location getHologramLocation() {
        return hologramLocation;
    }

    /**
     * Set the hologram location
     * @param hologramLocation the hologram location
     */
    public void setHologramLocation(Location hologramLocation) {
        this.hologramLocation = hologramLocation;
    }
}
