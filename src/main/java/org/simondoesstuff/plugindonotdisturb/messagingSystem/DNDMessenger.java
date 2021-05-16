package org.simondoesstuff.plugindonotdisturb.messagingSystem;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class DNDMessenger {
    public static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&8[&c&lD&e&lN&b&lD&8]&a");

    /**
     * A util for sending DND messages that conform to similar structure.
     *
     * Automatically colorizes the message based on '&', optionally appends
     * the plugin prefix, performs string formatting, and sends the message.
     *
     *
     * @param args
     * optionally used in string formatting of the message
     */
    public static void send(CommandSender recipient, String message, Object... args) {
        sendNoPrefix(recipient, PREFIX + " " + message, args);
    }

    /**
     * Same as send(...) but excludes the prefix
     */
    public static void sendNoPrefix(CommandSender recipient, String message, Object... args) {
        recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(message, args)));
    }
}
