package org.simondoesstuff.plugindonotdisturb.commandSystem;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface DNDCommand {
    /**
     * Used to register this command into a parent command manager like CommandSplit.
     * The handle and aliases are lower-cased by the CommandSplit.
     */
    String getHandle();

    /**
     * Used to register this command into a parent command manager like CommandSplit.
     * The handle and aliases are lower-cased by the CommandSplit.
     */
    default String[] getAliases() {
        return null;
    }

    /**
     * Used for the help command and in generating help command pages in command trees.
     */
    default String getHelpLine() {
        return getHandle();
    }

    void run(String label, List<String> args, CommandSender sender);

    /**
     * Tab completions of a DNDCommand are carried backwards to the original command executor.
     */
    List<String> tabComplete(List<String> args, CommandSender sender);
}
