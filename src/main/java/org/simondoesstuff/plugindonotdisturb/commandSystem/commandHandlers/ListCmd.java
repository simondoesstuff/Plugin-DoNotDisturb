package org.simondoesstuff.plugindonotdisturb.commandSystem.commandHandlers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simondoesstuff.plugindonotdisturb.DNDUser;
import org.simondoesstuff.plugindonotdisturb.DNDUserAlertFlag;
import org.simondoesstuff.plugindonotdisturb.DNDUserFlag;
import org.simondoesstuff.plugindonotdisturb.commandSystem.DNDCommand;
import org.simondoesstuff.plugindonotdisturb.messagingSystem.DNDMessenger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Named ListCmd as opposed to List to avoid covering
 *  Java's commonly used List class.
 */
public class ListCmd implements DNDCommand {
    @Override
    public String getHandle() {
        return "list";
    }

    @Override
    public String getHelpLine() {
        return getHandle() + "\nDisplay a list of all current DND flags.";
    }

    @Override
    public void run(String label, List<String> args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            DNDMessenger.send(sender, "&4This command can only be used by players.");
            return;
        }

        DNDUser u = DNDUser.retrieveUser((Player) sender);

        if (args.size() != 0) {
            u.sendDNDMsg("&4This command requires no arguments.");
        }

        // here we will divide all flags into an enabled and disabled list.
        // we will then sort each list independently of one another.
        // then we will iterate through both lists and convert each flag
        // into a formatted string, append the alert flag value onto the
        // string and return the feedback. As the process goes along, we will
        // total the amount of flags that the user did not have permission
        // to access and the number will be displayed subtly at the end of the
        // command feedback.

        ArrayList<DNDUserFlag> enabledList = new ArrayList<>();
        ArrayList<DNDUserFlag> disabledList = new ArrayList<>();
        int inaccessibleFlagsAmount = 0;

        for (DNDUserFlag flag : DNDUserFlag.values()) {
            // first we determine if the user has permission for this flag.
            // if they don't -> don't add the flag to the feedback, instead
            // add it to the inaccessible flags tally.
            if (!u.getBase().hasPermission("dnd.flag." + flag.name())) {
                inaccessibleFlagsAmount++;
                continue;
            }

            boolean state = u.getFlag(flag);

            if (state) enabledList.add(flag);
            else disabledList.add(flag);
        }

        Collections.sort(enabledList);
        Collections.sort(disabledList);

        StringBuilder feedback = new StringBuilder("&f&nYour flags:");

        for (DNDUserFlag flag : enabledList) {
            feedback.append('\n');
            appendFormattedListEntry(feedback, u, flag, true);
        }

        for (DNDUserFlag flag : disabledList) {
            feedback.append('\n');
            appendFormattedListEntry(feedback, u, flag, false);
        }

        if (inaccessibleFlagsAmount > 0) {
            feedback
                    .append('\n')
                    .append("&0 +")
                    .append(inaccessibleFlagsAmount)
                    .append(" inaccessible flags");
        }

        u.sendDNDMsg(feedback.toString());
    }

    @Override
    public List<String> tabComplete(List<String> args, CommandSender sender) {
        return null;
    }

    private void appendFormattedListEntry(StringBuilder feedback, DNDUser u, DNDUserFlag flag, boolean state) {
        feedback.append("&7- ");

        if (state) feedback.append("&a");
        else feedback.append("&c");

        feedback.append("[").append(flag.name()).append("]");

        DNDUserAlertFlag alertFlag = null;

        try {
            alertFlag = DNDUserAlertFlag.valueOf(flag.name());
        } catch (IllegalArgumentException ignored) {}

        if (alertFlag == null) return;

        boolean alertFlagState = u.getAlertFlag(alertFlag);

        feedback.append(" &falert is ");

        if (alertFlagState) feedback.append("&a&lon");
        else feedback.append("&c&loff.");
    }
}
