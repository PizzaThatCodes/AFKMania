package me.pizzalover.afkmania.gui.AFKBlock;

import me.pizzalover.afkmania.Main;
import me.pizzalover.afkmania.gui.guiUtils;
import me.pizzalover.afkmania.utils.utils;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

public class AFKBlockGUI {

    public static void openUpgradeGUI(Player player) {

        String configPath = "gui";
        int slotAmount = Main.getAfkBlockConfig().getConfig().getInt(configPath + ".slot_amount");

        if(slotAmount % 9 != 0) {
            player.sendMessage(utils.translate( utils.addPlaceholderToText(player, Main.getMessageConfig().getConfig().getString("afk_block.gui.error_messages.slot_amount_not_divisible_by_9")
                    .replace("%prefix%", Main.getSettingConfig().getConfig().getString("prefix"))
                    .replace("%gui%", Main.getAfkBlockConfig().getConfig().getString(configPath + ".gui_name"))
                    .replace("%slot_number%", String.valueOf(slotAmount))
            )));
            return;
        }

        if(slotAmount > 54) {
            player.sendMessage(utils.translate( utils.addPlaceholderToText(player, Main.getMessageConfig().getConfig().getString("afk_block.gui.error_messages.slot_amount_greater_than_54")
                    .replace("%prefix%", Main.getSettingConfig().getConfig().getString("prefix"))
                    .replace("%gui%", Main.getAfkBlockConfig().getConfig().getString(configPath + ".gui_name"))
                    .replace("%slot_number%", String.valueOf(slotAmount))
            )));
            return;
        }
        if(slotAmount < 9) {
            player.sendMessage(utils.translate( utils.addPlaceholderToText(player, Main.getMessageConfig().getConfig().getString("afk_block.gui.error_messages.slot_amount_less_than_9")
                    .replace("%prefix%", Main.getSettingConfig().getConfig().getString("prefix"))
                    .replace("%gui%", Main.getAfkBlockConfig().getConfig().getString(configPath + ".gui_name"))
                    .replace("%slot_number%", String.valueOf(slotAmount))
            )));
            return;
        }

        int height = slotAmount / 9;
        Gui gui = Gui.empty(9, height);



        for(String slot : Main.getAfkBlockConfig().getConfig().getConfigurationSection(configPath + ".slots").getKeys(false)) {
            String path = configPath + ".slots." + slot + ".";

            int slotNum;

            try {
                slotNum = Integer.parseInt(slot);
            } catch (NumberFormatException exception) {
                player.sendMessage(utils.translate( utils.addPlaceholderToText(player, Main.getMessageConfig().getConfig().getString("afk_block.gui.error_messages.invalid_slot_number")
                        .replace("%prefix%", Main.getSettingConfig().getConfig().getString("prefix"))
                        .replace("%gui%", Main.getAfkBlockConfig().getConfig().getString(configPath + ".gui_name"))
                        .replace("%slot_number%", slot)
                )));
                continue;
            }

            gui.setItem(slotNum, guiUtils.createInventoryItem(player, path));

        }

        Window window = Window.single()
                .setViewer(player)
                .setGui(gui)
                .setTitle(utils.translate( utils.addPlaceholderToText(player, Main.getAfkBlockConfig().getConfig().getString(configPath + ".gui_name"))))
                .build();
        window.open();


    }

}
