package me.pizzalover.afkmania.gui;

import com.cryptomorin.xseries.XMaterial;
import me.pizzalover.afkmania.Main;
import me.pizzalover.afkmania.modules.AFKBlockModules;
import me.pizzalover.afkmania.player_info.afk_block.AFKBlockPlayerData;
import me.pizzalover.afkmania.utils.SkullCreator;
import me.pizzalover.afkmania.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.ArrayList;

public class guiUtils {

    public static AbstractItem createInventoryItem(Player player, String path) {
        return new AbstractItem() {
            @Override
            public ItemProvider getItemProvider() {
                if (Main.getAfkBlockConfig().getConfig().getString(path + "item").split("-")[0].equalsIgnoreCase("base64")) {
                    String base64String = Main.getAfkBlockConfig().getConfig().getString(path + "item").replace("base64-", "");
                    ItemStack base64Skull = SkullCreator.itemWithBase64(XMaterial.PLAYER_HEAD.parseItem(), base64String);
                    SkullMeta base64SkullMeta = (SkullMeta) base64Skull.getItemMeta();

                    base64SkullMeta.setDisplayName(utils.translate(utils.addPlaceholderToText(player, Main.getAfkBlockConfig().getConfig().getString(path + "name"))));

                    ArrayList<String> lore = new ArrayList<String>();

                    for (String s : Main.getAfkBlockConfig().getConfig().getStringList(path + "lore")) {
                        for (String str : s.split("<br>"))
                            lore.add(utils.translate(utils.addPlaceholderToText(player, str)));
                    }
                    base64SkullMeta.setLore(lore);
                    base64Skull.setItemMeta(base64SkullMeta);

                    return new ItemBuilder(base64Skull);
                } else if (Main.getAfkBlockConfig().getConfig().getString(path + "item").split("-")[0].equalsIgnoreCase("head")) {
                    ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
                    SkullMeta pSkull = (SkullMeta) item.getItemMeta();
                    pSkull.setDisplayName(utils.translate(utils.addPlaceholderToText(player, Main.getAfkBlockConfig().getConfig().getString(path + "name"))));

                    ArrayList<String> lore = new ArrayList<String>();

                    for (String s : Main.getAfkBlockConfig().getConfig().getStringList(path + "lore")) {
                        for (String str : s.split("<br>"))
                            lore.add(utils.translate(utils.addPlaceholderToText(player, str)));
                    }
                    pSkull.setLore(lore);
                    String playerName = Main.getAfkBlockConfig().getConfig().getString(path + "item").split("-")[1];
                    if (playerName.contains("{")) {
                        playerName = player.getName();
                    }
                    pSkull.setOwner(playerName);
                    item.setItemMeta(pSkull);
                    return new ItemBuilder(item);
                }
                ItemStack itemStack = new ItemStack(XMaterial.valueOf(Main.getAfkBlockConfig().getConfig().getString(path + "item")).parseMaterial(), 1);
                ItemMeta itemMeta = itemStack.getItemMeta();

                itemMeta.setDisplayName(utils.translate(utils.addPlaceholderToText(player, Main.getAfkBlockConfig().getConfig().getString(path + "name"))));

                ArrayList<String> lore = new ArrayList<String>();

                for (String s : Main.getAfkBlockConfig().getConfig().getStringList(path + "lore")) {
                    for (String str : s.split("<br>"))
                        lore.add(utils.translate(utils.addPlaceholderToText(player, str)));
                }
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);

                return new ItemBuilder(itemStack);
            }

            @Override
            public void handleClick(ClickType clickType, Player player, InventoryClickEvent inventoryClickEvent) {

                if (clickType.isLeftClick()) {
                    for (String s : Main.getAfkBlockConfig().getConfig().getStringList(path + "left_click_commands")) {
                        if (s.startsWith("[player]")) {
                            player.performCommand(utils.addPlaceholderToText(player, s).replace("[player] ", ""));
                        } else if (s.startsWith("[console]")) {
                            utils.runConsoleCommand(utils.addPlaceholderToText(player, s).replace("[console] ", ""), player.getWorld());
                        } else if (s.startsWith("[sound]")) {
                            String[] soundVariables = s.replace("[sound] ", "").split(" ");
                            player.playSound(player, Sound.valueOf(utils.addPlaceholderToText(player, soundVariables[0])), Integer.parseInt(soundVariables[1]), Integer.parseInt(soundVariables[2]));
                        } else {
                            if (s.equalsIgnoreCase("exitInventory")) {
                                player.closeInventory();
                            }
                        }
                    }
                }
                if (clickType.isRightClick()) {
                    for (String s : Main.getAfkBlockConfig().getConfig().getStringList(path + "right_click_commands")) {
                        if (s.startsWith("[player]")) {
                            player.performCommand(utils.addPlaceholderToText(player, s).replace("[player] ", ""));
                        } else if (s.startsWith("[console]")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), utils.addPlaceholderToText(player, s).replace("[console] ", ""));
                        } else if (s.startsWith("[sound]")) {
                            String[] soundVariables = s.replace("[sound] ", "").split(" ");
                            player.playSound(player, Sound.valueOf(utils.addPlaceholderToText(player, soundVariables[0])), Integer.parseInt(soundVariables[1]), Integer.parseInt(soundVariables[2]));
                        } else {
                            if (s.equalsIgnoreCase("exitInventory")) {
                                player.closeInventory();
                            }
                        }
                    }
                }

            }
        };
    }

    public static AbstractItem createUpgradeItem(Player player, String upgradeKey) {
        return new AbstractItem() {
            @Override
            public ItemProvider getItemProvider() {
                AFKBlockModules module = (AFKBlockModules) Main.getModuleManager().getModule("AFKBlock");
                AFKBlockPlayerData data = module.getOrCreatePlayerData(player);

                String path = "upgrades." + upgradeKey + ".";
                Material material = XMaterial.valueOf(Main.getAfkBlockConfig().getConfig().getString(path + "item", "STONE")).parseMaterial();
                ItemStack item = new ItemStack(material == null ? Material.STONE : material, 1);
                ItemMeta meta = item.getItemMeta();

                int level = data.getUpgradeLevel(upgradeKey);
                int maxLevel = Main.getAfkBlockConfig().getConfig().getInt(path + "max_level", 10);
                long cost = getUpgradeCost(path, level);

                String name = Main.getAfkBlockConfig().getConfig().getString(path + "name", "&e" + upgradeKey);
                meta.setDisplayName(utils.translate(name
                        .replace("%upgrade_level%", String.valueOf(level))
                        .replace("%next_level%", String.valueOf(level + 1))
                ));

                ArrayList<String> lore = new ArrayList<String>();
                for (String line : Main.getAfkBlockConfig().getConfig().getStringList(path + "lore")) {
                    lore.add(utils.translate(utils.addPlaceholderToText(player, line)
                            .replace("%upgrade_level%", String.valueOf(level))
                            .replace("%next_level%", String.valueOf(level + 1))
                            .replace("%upgrade_cost%", String.valueOf(cost))
                            .replace("%upgrade_max_level%", String.valueOf(maxLevel))
                    ));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
                return new ItemBuilder(item);
            }

            @Override
            public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
                if (!clickType.isLeftClick()) return;

                AFKBlockModules module = (AFKBlockModules) Main.getModuleManager().getModule("AFKBlock");
                AFKBlockPlayerData data = module.getOrCreatePlayerData(player);
                String path = "upgrades." + upgradeKey + ".";
                int level = data.getUpgradeLevel(upgradeKey);
                int maxLevel = Main.getAfkBlockConfig().getConfig().getInt(path + "max_level", 10);

                if (level >= maxLevel) {
                    for (String command : Main.getAfkBlockConfig().getConfig().getStringList(path + "maxed_commands")) {
                        utils.runConsoleCommand(formatUpgradeCommand(command, player, upgradeKey, level, 0), player.getWorld());
                    }
                    return;
                }

                long cost = getUpgradeCost(path, level);
                for (String command : Main.getAfkBlockConfig().getConfig().getStringList(path + "purchase_commands")) {
                    utils.runConsoleCommand(formatUpgradeCommand(command, player, upgradeKey, level, cost), player.getWorld());
                }

                data.setUpgradeLevel(upgradeKey, level + 1);
                module.saveUpgrades(data);

                for (String command : Main.getAfkBlockConfig().getConfig().getStringList(path + "after_purchase_commands")) {
                    utils.runConsoleCommand(formatUpgradeCommand(command, player, upgradeKey, level + 1, cost), player.getWorld());
                }

                event.getWhoClicked().closeInventory();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        me.pizzalover.afkmania.gui.AFKBlock.AFKBlockGUI.openUpgradeGUI(player);
                    }
                }, 1L);
            }
        };
    }

    private static String formatUpgradeCommand(String command, Player player, String upgradeKey, int currentLevel, long cost) {
        return utils.addPlaceholderToText(player, command)
                .replace("%player%", player.getName())
                .replace("%upgrade%", upgradeKey)
                .replace("%upgrade_level%", String.valueOf(currentLevel))
                .replace("%next_upgrade_level%", String.valueOf(currentLevel + 1))
                .replace("%upgrade_cost%", String.valueOf(cost));
    }

    private static long getUpgradeCost(String path, int currentLevel) {
        long baseCost = Main.getAfkBlockConfig().getConfig().getLong(path + "base_cost", 0);
        long incrementalCost = Main.getAfkBlockConfig().getConfig().getLong(path + "incremental_cost", 0);
        return Math.max(0, baseCost + (incrementalCost * currentLevel));
    }

}
