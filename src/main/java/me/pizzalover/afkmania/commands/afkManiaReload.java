package me.pizzalover.afkmania.commands;

import me.pizzalover.afkmania.Main;
import me.pizzalover.afkmania.utils.utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class afkManiaReload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {


        if(args.length == 0) {
            sender.sendMessage(utils.translate(Main.getSettingConfig().getConfig().getString("prefix") + "Unknown command. check &e/afkmania help &rfor more information."));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" : {
                if(sender.hasPermission("afkmania.reload")) {
                    // Reload the plugin
                    sender.sendMessage(utils.translate(
                            Objects.requireNonNull(Main.getMessageConfig().getConfig().getString("commands.reload"))
                                    .replace("%prefix%", Objects.requireNonNull(Main.getSettingConfig().getConfig().getString("prefix"))))
                    );
                    Main.getMessageConfig().reloadConfig();
                    Main.getSettingConfig().reloadConfig();

                    Main.getModuleManager().checkConfigModules();

                } else {
                    sender.sendMessage(utils.translate(
                            Objects.requireNonNull(Main.getMessageConfig().getConfig().getString("commands.no_permission"))
                                    .replace("%prefix%", Objects.requireNonNull(Main.getSettingConfig().getConfig().getString("prefix"))))
                    );
                }
                return true;
            }
            case "help" : {
                sender.sendMessage(utils.translate("&eAFKMania Commands:"));
                sender.sendMessage(utils.translate("&e/afkmania reload &7- Reload the plugin"));
                return true;
            }
            default: {
                sender.sendMessage(utils.translate(
                        Objects.requireNonNull(Main.getMessageConfig().getConfig().getString("commands.unknown_command"))
                                .replace("%prefix%", Objects.requireNonNull(Main.getSettingConfig().getConfig().getString("prefix"))))
                );
                return true;
            }
        }


    }
}
