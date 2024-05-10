package me.pizzalover.afkmania.gui.AFKBlock;

import me.pizzalover.afkmania.gui.guiUtils;
import me.pizzalover.afkmania.utils.config.messageConfig;
import me.pizzalover.afkmania.utils.config.modules.afkBlockConfig;
import me.pizzalover.afkmania.utils.config.settingConfig;
import me.pizzalover.afkmania.utils.utils;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

public class AFKBlockGUI {

    public static void openJobGUI(Player player) {

        String configPath = "gui";
        int slotAmount = afkBlockConfig.getConfig().getInt(configPath + ".slotsAmount");

        if(slotAmount % 9 != 0) {
            player.sendMessage(utils.translate( utils.addPlaceholderToText(player, messageConfig.getConfig().getString("afk_block.gui.error_messages.slot_amount_not_divisible_by_9")
                    .replace("{prefix}", settingConfig.getConfig().getString("prefix"))
                    .replace("{gui}", "afk block gui")
                    .replace("{slotAmount}", String.valueOf(slotAmount))
            )));
            return;
        }

        if(slotAmount > 54) {
            player.sendMessage(utils.translate( utils.addPlaceholderToText(player, messageConfig.getConfig().getString("afk_block.gui.error_messages.slot_amount_greater_than_54")
                    .replace("{prefix}", settingConfig.getConfig().getString("prefix"))
                    .replace("{gui}", "afk block gui")
                    .replace("{slotAmount}", String.valueOf(slotAmount))
            )));
            return;
        }
        if(slotAmount < 9) {
            player.sendMessage(utils.translate( utils.addPlaceholderToText(player, messageConfig.getConfig().getString("afk_block.gui.error_messages.slot_amount_less_than_9")
                    .replace("{prefix}", settingConfig.getConfig().getString("prefix"))
                    .replace("{gui}", "afk block gui")
                    .replace("{slotAmount}", String.valueOf(slotAmount))
            )));
            return;
        }

        int height = slotAmount / 9;
        Gui gui = Gui.empty(9, height);



        for(String slot : afkBlockConfig.getConfig().getConfigurationSection(configPath + ".slots").getKeys(false)) {
            String path = configPath + ".slots." + slot + ".";

            int slotNum;

            try {
                slotNum = Integer.parseInt(slot);
            } catch (NumberFormatException exception) {
                player.sendMessage(utils.translate( utils.addPlaceholderToText(player, messageConfig.getConfig().getString("afk_block.gui.error_messages.invalid_slot_number")
                        .replace("{prefix}", settingConfig.getConfig().getString("prefix"))
                        .replace("{gui}", "afk block gui")
                        .replace("{slot}", slot)
                )));
                continue;
            }

            gui.setItem(slotNum, guiUtils.createInventoryItem(player, path));

        }

        Window window = Window.single()
                .setViewer(player)
                .setGui(gui)
                .setTitle(utils.translate( utils.addPlaceholderToText(player, afkBlockConfig.getConfig().getString(configPath + ".name")
                        .replace("{prefix}", settingConfig.getConfig().getString("prefix")))))
                .build();
        window.open();

    }

}
