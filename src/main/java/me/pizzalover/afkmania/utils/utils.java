package me.pizzalover.afkmania.utils;

import me.pizzalover.afkmania.Main;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class utils {

    /**
     * Translate a string with color codes to have colored text (hex supported)
     * @param message returns a colored string using & (hex supported)
     * @return
     */
    public static String translate(String message) {
        // TODO: check if server version is 1.16 or above
        try {
            Method method = Class.forName("net.md_5.bungee.api.ChatColor").getMethod("of", String.class);
            message = message.replaceAll("&#",  "#");
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            }
        } catch (Exception e) {
            // Server version is below 1.16
            return ChatColor.translateAlternateColorCodes('&', message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Really janky setup to run a console command without it being logged into console
     * @param command the command to run
     * @param world the world to run the command in
     */
    public static void runConsoleCommand(String command, World world) {
        boolean commandFeedback = world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK);
        boolean commandBlock = world.getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT);
        world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
        world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
        Entity entity = world.spawnEntity(new Location(world, 0, 0, 0), EntityType.MINECART_COMMAND);
        Main.getInstance().getServer().dispatchCommand(entity, command);
        world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, commandFeedback);
        world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, commandBlock);
        entity.remove();
    }

}
