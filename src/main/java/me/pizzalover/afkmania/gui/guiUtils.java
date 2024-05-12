package me.pizzalover.afkmania.gui;

import com.cryptomorin.xseries.XMaterial;
import me.pizzalover.afkmania.Main;
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

    /**
     * Create an inventory item
     * @param player The player
     * @param path The path to the item in the config
     * @return The item
     */
    public static AbstractItem createInventoryItem(Player player, String path) {
        return new AbstractItem() {
            @Override
            public ItemProvider getItemProvider() {
                if(Main.getAfkBlockConfig().getConfig().getString(path + "item").split("-")[0].equalsIgnoreCase("base64")) {
                    String base64String = Main.getAfkBlockConfig().getConfig().getString(path + "item").replace("base64-", "");
                    ItemStack base64Skull = SkullCreator.itemWithBase64(XMaterial.PLAYER_HEAD.parseItem(), base64String );
                    SkullMeta base64SkullMeta = (SkullMeta) base64Skull.getItemMeta();

                    base64SkullMeta.setDisplayName(utils.translate( utils.addPlaceholderToText(player, Main.getAfkBlockConfig().getConfig().getString(path + "name"))));

                    ArrayList<String> lore = new ArrayList<>();

                    for(String s : Main.getAfkBlockConfig().getConfig().getStringList(path + "lore")) {
                        for(String str : s.split("<br>"))
                            lore.add(utils.translate( utils.addPlaceholderToText(player, str)));
                    }
                    base64SkullMeta.setLore(lore);
                    base64Skull.setItemMeta(base64SkullMeta);

                    return new ItemBuilder(base64Skull);
                }
                else if(Main.getAfkBlockConfig().getConfig().getString(path + "item").split("-")[0].equalsIgnoreCase("head")) {
                    ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
                    SkullMeta pSkull = (SkullMeta) item.getItemMeta();
                    pSkull.setDisplayName(utils.translate( utils.addPlaceholderToText(player, Main.getAfkBlockConfig().getConfig().getString(path + "name"))));

                    ArrayList<String> lore = new ArrayList<>();

                    for(String s : Main.getAfkBlockConfig().getConfig().getStringList(path + "lore")) {
                        for(String str : s.split("<br>"))
                            lore.add(utils.translate( utils.addPlaceholderToText(player, str)));
                    }
                    pSkull.setLore(lore);
                    String playerName = Main.getAfkBlockConfig().getConfig().getString(path + "item").split("-")[1];
                    if(playerName.contains("{")) {playerName = player.getName();}
                    pSkull.setOwner(playerName);
                    item.setItemMeta(pSkull);
                    return new ItemBuilder(item);
                }
                ItemStack itemStack = new ItemStack(XMaterial.valueOf(Main.getAfkBlockConfig().getConfig().getString(path + "item")).parseMaterial(), 1);
                ItemMeta itemMeta = itemStack.getItemMeta();

                itemMeta.setDisplayName(utils.translate( utils.addPlaceholderToText(player, Main.getAfkBlockConfig().getConfig().getString(path + "name"))));

                ArrayList<String> lore = new ArrayList<>();

                for(String s : Main.getAfkBlockConfig().getConfig().getStringList(path + "lore")) {
                    for(String str : s.split("<br>"))
                        lore.add(utils.translate(utils.addPlaceholderToText(player, str)));
                }
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);

                return new ItemBuilder(itemStack);
            }

            @Override
            public void handleClick(ClickType clickType, Player player, InventoryClickEvent inventoryClickEvent) {

                if(clickType.isLeftClick()) {
                    for(String s : Main.getAfkBlockConfig().getConfig().getStringList(path + "left_click_commands")) {
                        if(s.startsWith("[player]")) {
                            player.performCommand(utils.addPlaceholderToText(player, s).replace("[player] ", ""));
                        } else if (s.startsWith("[console]")) {
                            utils.runConsoleCommand(utils.addPlaceholderToText(player, s).replace("[console] ", ""), player.getWorld());
                        } else if (s.startsWith("[sound]")) {
                            String[] soundVariables = s.replace("[sound] ", "").split(" ");
                            player.playSound(player, Sound.valueOf(utils.addPlaceholderToText(player, soundVariables[0]) ), Integer.parseInt(soundVariables[1]), Integer.parseInt(soundVariables[2]));
                        } else {
                            if (s.equalsIgnoreCase("exitInventory")) {
                                player.closeInventory();
                            }
                        }
                    }
                }
                if(clickType.isRightClick()) {
                    for(String s : Main.getAfkBlockConfig().getConfig().getStringList(path + "right_click_commands")) {
                        if(s.startsWith("[player]")) {
                            player.performCommand(utils.addPlaceholderToText(player, s).replace("[player] ", ""));
                        } else if (s.startsWith("[console]")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), utils.addPlaceholderToText(player, s).replace("[console] ", ""));
                        } else if (s.startsWith("[sound]")) {
                            String[] soundVariables = s.replace("[sound] ", "").split(" ");
                            player.playSound(player, Sound.valueOf(utils.addPlaceholderToText(player, soundVariables[0]) ), Integer.parseInt(soundVariables[1]), Integer.parseInt(soundVariables[2]));
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

}
