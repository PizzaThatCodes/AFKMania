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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.*;

public class AFKBlockModules implements ModuleInterface {

    private boolean enabled = false;
    private Location blockLocation;
    private Hologram blockHologram;
    private Location hologramLocation;

    AFKBlockPlayerInteract afkBlockPlayerInteract;
    public ArrayList<AFKBlockPlayerData> player_data_afk_block;

    public MyScheduledTask afkMessageTask;
    public MyScheduledTask afkBlockTask;

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

        if (!Main.getAfkBlockConfig().getConfigFile().exists()) {
            Main.getInstance().saveResource("modules/afk_block.yml", false);
        }
        Main.getAfkBlockConfig().updateConfig(Arrays.asList("gui", "block_settings.hologram", "upgrades", "rewards", "blocks"));

        Main.getAfkBlockConfig().saveConfig();
        Main.getAfkBlockConfig().reloadConfig();

        if (Main.getInstance().getServer().getPluginManager().getPlugin("DecentHolograms") == null) {
            Main.getInstance().getLogger().severe("DecentHolograms is not installed! Disabling AFKBlock module...");
            Main.getModuleManager().disableModule(this);
            return;
        }

        afkBlockPlayerInteract = new AFKBlockPlayerInteract();
        Main.getInstance().getServer().getPluginManager().registerEvents(afkBlockPlayerInteract, Main.getInstance());
        player_data_afk_block = new ArrayList<>();

        blockLocation = new Location(
                Main.getInstance().getServer().getWorld(Main.getAfkBlockConfig().getConfig().getString("block_location.world")),
                Main.getAfkBlockConfig().getConfig().getDouble("block_location.x"),
                Main.getAfkBlockConfig().getConfig().getDouble("block_location.y"),
                Main.getAfkBlockConfig().getConfig().getDouble("block_location.z")
        ).getBlock().getLocation();

        hologramLocation = blockLocation.clone();
        hologramLocation.add(hologramLocation.getX() > 0 ? -0.5 : 0.5, 1, hologramLocation.getZ() > 0 ? -0.5 : 0.5);

        ArrayList<String> hologramLines = new ArrayList<>();
        for (String hologram_lines : Main.getAfkBlockConfig().getConfig().getStringList("block_settings.hologram")) {
            hologramLocation.add(0, 0.3, 0);
            hologramLines.add(utils.translate(hologram_lines));
        }

        blockHologram = DHAPI.createHologram("AFKBlock", getHologramLocation(), false, hologramLines);

        afkMessageTask = Main.getScheduler().runTaskTimerAsynchronously(() -> {
            for (AFKBlockPlayerData playerData : player_data_afk_block) {
                if (playerData.getPlayer().getInventory().firstEmpty() == -1) {
                    playerData.getPlayer().sendTitle(utils.translate(Main.getAfkBlockConfig().getConfig().getString("afk-message.inventory-full.title")),
                            utils.translate(Main.getAfkBlockConfig().getConfig().getString("afk-message.inventory-full.subtitle")),
                            0,
                            5,
                            5
                    );
                } else {
                    playerData.getPlayer().sendTitle(utils.translate(Main.getAfkBlockConfig().getConfig().getString("afk-message.inventory-collect.title")),
                            utils.translate(Main.getAfkBlockConfig().getConfig().getString("afk-message.inventory-collect.subtitle")),
                            0,
                            5,
                            5
                    );
                }
            }
        }, 0, 20L);

        afkBlockTask = Main.getScheduler().runTaskTimer(() -> {
            for (AFKBlockPlayerData playerData : player_data_afk_block) {
                playerData.setAFKBlockTimeTicks(playerData.getAFKBlockTimeTicks() + 1);

                double effectiveBreakSpeed = getEffectiveBreakSpeed(playerData);
                if (effectiveBreakSpeed <= 0) effectiveBreakSpeed = 0.1;

                if (playerData.getAFKBlockTimeTicks() % Math.max(1, (int) (effectiveBreakSpeed * 20)) == 0) {
                    rewardPlayer(playerData.getPlayer(), playerData);
                    updateVisualBlock(playerData);
                }
            }
        }, 20L, 1L);

    }

    public AFKBlockPlayerData getPlayerData(Player player) {
        for (AFKBlockPlayerData playerData : player_data_afk_block) {
            if (playerData.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return playerData;
            }
        }
        return null;
    }

    public AFKBlockPlayerData getOrCreatePlayerData(Player player) {
        AFKBlockPlayerData data = getPlayerData(player);
        if (data != null) {
            return data;
        }

        data = new AFKBlockPlayerData(player, 0);
        loadUpgrades(player, data);
        player_data_afk_block.add(data);
        updateVisualBlock(data);
        return data;
    }

    private void loadUpgrades(Player player, AFKBlockPlayerData data) {
        ConfigurationSection section = Main.getAfkPoolsConfig().getConfig().getConfigurationSection("afk_block_upgrades." + player.getUniqueId() + ".upgrades");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            data.setUpgradeLevel(key, section.getInt(key, 0));
        }
    }

    public void saveUpgrades(AFKBlockPlayerData data) {
        String basePath = "afk_block_upgrades." + data.getPlayer().getUniqueId() + ".upgrades";
        for (Map.Entry<String, Integer> entry : data.getUpgradeLevels().entrySet()) {
            Main.getAfkPoolsConfig().getConfig().set(basePath + "." + entry.getKey(), entry.getValue());
        }
        Main.getAfkPoolsConfig().saveConfig();
        Main.getAfkPoolsConfig().reloadConfig();
    }

    private void updateVisualBlock(AFKBlockPlayerData data) {
        BlockState blockstate = getBlockLocation().getBlock().getState().copy();
        blockstate.setType(getRandomBlockMaterial());
        data.getPlayer().sendBlockChange(getBlockLocation(), blockstate.getBlockData());
        data.getPlayer().playSound(data.getPlayer().getLocation(), "block.stone.break", 1, 1);
    }

    private Material getRandomBlockMaterial() {
        ConfigurationSection blocksSection = Main.getAfkBlockConfig().getConfig().getConfigurationSection("blocks");

        if (blocksSection != null) {
            List<WeightedEntry<Material>> weightedMaterials = new ArrayList<>();
            for (String key : blocksSection.getKeys(false)) {
                String matName = blocksSection.getString(key + ".material", "STONE");
                Material material;
                try {
                    material = XMaterial.valueOf(matName.toUpperCase()).parseMaterial();
                } catch (Exception e) {
                    material = Material.STONE;
                }
                int weight = Math.max(1, blocksSection.getInt(key + ".weight", 1));
                weightedMaterials.add(new WeightedEntry<Material>(material, weight));
            }
            Material selected = selectWeighted(weightedMaterials);
            return selected == null ? Material.STONE : selected;
        }

        ArrayList<Material> materialList = new ArrayList<Material>();
        for (String blocks : Main.getAfkBlockConfig().getConfig().getStringList("blocks")) {
            Material material = XMaterial.valueOf(blocks).parseMaterial();
            if (material != null) materialList.add(material);
        }

        if (materialList.size() == 0) {
            return Material.STONE;
        }

        int randomIndex = new Random().nextInt(materialList.size());
        return materialList.get(randomIndex);
    }

    private void rewardPlayer(Player player, AFKBlockPlayerData playerData) {
        ConfigurationSection rewardsSection = Main.getAfkBlockConfig().getConfig().getConfigurationSection("rewards");
        if (rewardsSection == null) {
            return;
        }

        Material rolledBlock = getRandomBlockMaterial();
        List<String> rewardPool = getRewardPoolForBlock(rolledBlock);

        List<WeightedEntry<String>> rewards = new ArrayList<WeightedEntry<String>>();
        for (String key : rewardsSection.getKeys(false)) {
            if (!rewardPool.isEmpty() && !rewardPool.contains(key)) {
                continue;
            }
            int chance = rewardsSection.getInt(key + ".chance", 1);
            if (chance <= 0) continue;
            rewards.add(new WeightedEntry<String>(key, chance));
        }

        String selectedRewardKey = selectWeighted(rewards);
        if (selectedRewardKey == null) {
            return;
        }

        String rewardPath = "rewards." + selectedRewardKey + ".";
        List<String> commands = Main.getAfkBlockConfig().getConfig().getStringList(rewardPath + "commands");
        if (commands.isEmpty()) {
            String singleCommand = Main.getAfkBlockConfig().getConfig().getString(rewardPath + "command", "");
            if (!singleCommand.isEmpty()) commands = Collections.singletonList(singleCommand);
        }

        int amount = Main.getAfkBlockConfig().getConfig().getInt(rewardPath + "amount", 1);
        int fortuneLevel = playerData.getUpgradeLevel("fortune");
        int extraItems = (int) Math.floor(Math.random() * (fortuneLevel + 1));
        int finalAmount = Math.max(1, amount + extraItems);

        for (String command : commands) {
            String built = applyPlaceholders(command, player, selectedRewardKey, finalAmount, playerData);
            utils.runConsoleCommand(built, player.getWorld());
        }

        runUpgradeProcHooks(player, playerData, selectedRewardKey);
    }

    private List<String> getRewardPoolForBlock(Material material) {
        ConfigurationSection blocksSection = Main.getAfkBlockConfig().getConfig().getConfigurationSection("blocks");
        if (blocksSection == null) return Collections.emptyList();

        for (String key : blocksSection.getKeys(false)) {
            String matName = blocksSection.getString(key + ".material", "STONE");
            Material configured;
            try {
                configured = XMaterial.valueOf(matName.toUpperCase()).parseMaterial();
            } catch (Exception e) {
                configured = Material.STONE;
            }

            if (configured == material) {
                return Main.getAfkBlockConfig().getConfig().getStringList("blocks." + key + ".rewards");
            }
        }
        return Collections.emptyList();
    }

    private void runUpgradeProcHooks(Player player, AFKBlockPlayerData playerData, String rewardKey) {
        ConfigurationSection upgrades = Main.getAfkBlockConfig().getConfig().getConfigurationSection("upgrades");
        if (upgrades == null) return;

        for (String upgradeKey : upgrades.getKeys(false)) {
            String path = "upgrades." + upgradeKey + ".";
            if (!Main.getAfkBlockConfig().getConfig().getBoolean(path + "enabled", false)) continue;

            int level = playerData.getUpgradeLevel(upgradeKey);
            if (level <= 0) continue;

            double chancePerLevel = Main.getAfkBlockConfig().getConfig().getDouble(path + "chance_per_level", 0);
            double procChance = Math.min(100.0, chancePerLevel * level);
            if (new Random().nextDouble() * 100 > procChance) continue;

            for (String command : Main.getAfkBlockConfig().getConfig().getStringList(path + "proc_commands")) {
                String built = applyPlaceholders(command, player, rewardKey, 1, playerData)
                        .replace("%upgrade%", upgradeKey)
                        .replace("%upgrade_level%", String.valueOf(level));
                utils.runConsoleCommand(built, player.getWorld());
            }
        }
    }

    private String applyPlaceholders(String command, Player player, String rewardKey, int amount, AFKBlockPlayerData data) {
        return utils.addPlaceholderToText(player, command)
                .replace("%player%", player.getName())
                .replace("%reward%", rewardKey)
                .replace("%amount%", String.valueOf(amount))
                .replace("%afk_block_seconds%", String.valueOf((int) data.getAFKBlockTimeSeconds()));
    }

    private double getEffectiveBreakSpeed(AFKBlockPlayerData playerData) {
        double base = Main.getAfkBlockConfig().getConfig().getDouble("block_settings.break_speed", 1D);
        int hasteLevel = playerData.getUpgradeLevel("haste");
        double reductionPerLevel = Main.getAfkBlockConfig().getConfig().getDouble("upgrades.haste.break_speed_reduction_per_level", 0D);

        double computed = base - (hasteLevel * reductionPerLevel);
        double minValue = Main.getAfkBlockConfig().getConfig().getDouble("upgrades.haste.minimum_break_speed", 0.2D);

        return Math.max(minValue, computed);
    }

    private <T> T selectWeighted(List<WeightedEntry<T>> entries) {
        if (entries == null || entries.isEmpty()) return null;

        int total = 0;
        for (WeightedEntry<T> entry : entries) {
            total += Math.max(0, entry.weight);
        }

        if (total <= 0) return null;

        int random = new Random().nextInt(total);
        int index = 0;
        for (WeightedEntry<T> entry : entries) {
            index += Math.max(0, entry.weight);
            if (random < index) {
                return entry.value;
            }
        }
        return null;
    }

    @Override
    public void disable() {
        if (Main.getInstance().getServer().getPluginManager().getPlugin("DecentHolograms") != null) {
            if (blockHologram != null)
                blockHologram.delete();
        }
        if (afkBlockPlayerInteract != null)
            HandlerList.unregisterAll(afkBlockPlayerInteract);
        if (player_data_afk_block != null) {
            for (AFKBlockPlayerData data : player_data_afk_block) {
                saveUpgrades(data);
            }
            player_data_afk_block.clear();
            player_data_afk_block = null;
        }
        if (afkMessageTask != null)
            afkMessageTask.cancel();
        if (afkBlockTask != null)
            afkBlockTask.cancel();

    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {

    }

    public Location getBlockLocation() {
        return blockLocation;
    }

    public void setBlockLocation(Location blockLocation) {
        this.blockLocation = blockLocation;
    }

    public Hologram getBlockHologram() {
        return blockHologram;
    }

    public void setBlockHologram(Hologram blockHologram) {
        this.blockHologram = blockHologram;
    }

    public Location getHologramLocation() {
        return hologramLocation;
    }

    public void setHologramLocation(Location hologramLocation) {
        this.hologramLocation = hologramLocation;
    }

    static class WeightedEntry<T> {
        final T value;
        final int weight;

        WeightedEntry(T value, int weight) {
            this.value = value;
            this.weight = weight;
        }
    }
}
