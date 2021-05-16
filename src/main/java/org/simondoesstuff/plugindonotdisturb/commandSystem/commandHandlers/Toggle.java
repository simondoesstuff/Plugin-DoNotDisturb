package org.simondoesstuff.plugindonotdisturb.commandSystem.commandHandlers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simondoesstuff.plugindonotdisturb.DNDUser;
import org.simondoesstuff.plugindonotdisturb.DNDUserFlag;
import org.simondoesstuff.plugindonotdisturb.DNDUtils;
import org.simondoesstuff.plugindonotdisturb.commandSystem.DNDCommand;
import org.simondoesstuff.plugindonotdisturb.messagingSystem.DNDMessenger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Toggle implements DNDCommand {
    @Override
    public String getHandle() {
        return "toggle";
    }

    @Override
    public String getHelpLine() {
        return getHandle() + " [Flag Name]\nEnable/disable a DND flag.";
    }

    @Override
    public void run(String label, List<String> args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            DNDMessenger.send(sender, "&4This command can only be used by players.");
            return;
        }

        DNDUser u = DNDUser.retrieveUser((Player) sender);

        if (args.size() == 0) {
            u.sendDNDMsg("&4This command requires one argument.");
            return;
        }

        if (args.size() > 1) {
            u.sendDNDMsg("&4This command only requires one argument.");
        }

        String flagName = args.get(0);

        if (flagName.equalsIgnoreCase("all")) {
            u.sendDNDMsg("&aToggled every flag.");

            for (DNDUserFlag flag : DNDUserFlag.values()) {
                u.setFlag(flag, !u.getFlag(flag));
            }

            return;
        }

        DNDUserFlag flag = DNDUtils.searchEnum(DNDUserFlag.class, flagName);

        if (flag == null) {
            u.sendDNDMsg("&4Flag not recognized.");
            return;
        }

        boolean flagValue = !u.getFlag(flag);
        u.setFlag(flag, flagValue);

        u.sendDNDMsg("&7[&f&n%s&7] is now %s&7.", flag.name(), flagValue ? "&a&lenabled" : "&c&ldisabled");
    }

    @Override
    public List<String> tabComplete(List<String> args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return null;
        }

        // we have no tab completions past the 1st arg
        if (args.size() > 1) return null;

        DNDUser user = DNDUser.retrieveUser((Player) sender);

        List<String> list = new ArrayList<>();

        // we only want to tab complete the alert flags that the user has access to

        for (DNDUserFlag accessibleFlag : user.getAccessibleFlags()) {
            list.add(accessibleFlag.name());
        }

        // we will add "all", an extra option handled in run(...)

        list.add("all");

        return list;
    }
}
