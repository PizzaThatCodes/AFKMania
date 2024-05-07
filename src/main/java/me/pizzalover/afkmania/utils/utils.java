package me.pizzalover.afkmania.utils;

import org.bukkit.ChatColor;

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

}
