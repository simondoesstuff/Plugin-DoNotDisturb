package org.simondoesstuff.plugindonotdisturb.commandSystem;

import org.bukkit.command.CommandSender;
import org.simondoesstuff.plugindonotdisturb.messagingSystem.DNDMessenger;

import java.util.*;

public class CommandSplit implements DNDCommand {
    private AutoHelpCmd helpCmd = new AutoHelpCmd(10);
    private String handle, helpLine;
    private String[] aliases;
    private HashSet<String> primaryHandles = new HashSet<>();       // only the command handles (no aliases)
    private List<String> sortedPrimaryHandles = new ArrayList<>();
    private HashMap<String, DNDCommand> commands = new HashMap<>(); // all commands, indexed by handle and aliases

    public CommandSplit(String handle, String[] aliases, String helpLine) {
        this.handle = handle;
        this.aliases = aliases;
        this.helpLine = helpLine;

        addCommand(helpCmd);
    }

    @Override
    public String getHandle() {
        return handle;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getHelpLine() {
        return helpLine;
    }

    private void registerCommand(DNDCommand command) {
        String handle = command.getHandle().toLowerCase();
        commands.put(handle, command);

        if (primaryHandles.contains(handle)) {
            throw new IllegalArgumentException("Command already exists: " + handle);
        }

        primaryHandles.add(command.getHandle());

        if (command.getAliases() != null)
            for (String alias : command.getAliases()) {
                alias = alias.toLowerCase();

                if (commands.containsKey(alias)) {
                    throw new IllegalArgumentException("Command already exists: " + handle);
                }

                commands.put(alias, command);
            } // for loop
    }

    private void sortAndRefresh() {
        sortedPrimaryHandles = new ArrayList<>(primaryHandles);
        Collections.sort(sortedPrimaryHandles);

        // we want the 'help' command to be at the bottom
        if (sortedPrimaryHandles.remove("help"))
            sortedPrimaryHandles.add("help");

        ArrayList<String> newHelpIndex = new ArrayList<>();

        for (String s : sortedPrimaryHandles) {
            DNDCommand sCmd = commands.get(s.toLowerCase());
            String helpLine = sCmd.getHelpLine();

            if (helpLine == null) helpLine = sCmd.getHandle();

            newHelpIndex.add(helpLine);
        }

        helpCmd.refresh(newHelpIndex);
    }

    public void addCommands(DNDCommand[] commands) {
        for (DNDCommand cmd : commands) {
            registerCommand(cmd);
        }

        sortAndRefresh();
    }

    public void addCommand(DNDCommand command) {
        registerCommand(command);
        sortAndRefresh();
    }

    @Override
    public void run(String label, List<String> args, CommandSender sender) {
        // takes in arguments while somewhere along the command tree.
        // argument 0 is striped from args as the cmd name to search for
        //  the label is not important.

        if (args.size() == 0) {
            // if they tried to run the split with no sub command whatsoever
            //  we will just pretend they typed the help command.

            helpCmd.run("help", new ArrayList<>(), sender);
            return;
        }

        String cmdName = args.get(0).toLowerCase();
        args.remove(0);

        DNDCommand command = commands.get(cmdName);

        if (command == null) {
            DNDMessenger.send(sender, "&4Command (&c&n%s&4) not found.", cmdName);
            return;
        }

        command.run(cmdName, args, sender);
    }

    @Override
    public List<String> tabComplete(List<String> args, CommandSender sender) {
        // returns the found command's tab completions

        // takes in arguments while somewhere along the command tree.
        // argument 0 is striped from args as the cmd name to search for
        //  the label is not important.

        if (args.size() == 0) {
            // they did not provide a sub command yet

            // self tab completions
            return sortedPrimaryHandles;
        }

        String cmdName = args.get(0).toLowerCase();
        args.remove(0);

        DNDCommand command = commands.get(cmdName);

        if (command != null) {
            // they typed a working command -> pass tab completions

            return command.tabComplete(args, sender);
        }

        // this command is invalid. It is possible that they are still typing one
        //  so we will return the possibilities
        return sortedPrimaryHandles;
    }
} // class
