package org.simondoesstuff.plugindonotdisturb.commandSystem.commandHandlers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.simondoesstuff.plugindonotdisturb.commandSystem.CommandSplit;
import org.simondoesstuff.plugindonotdisturb.commandSystem.DNDCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DNDRootCommandExecutor implements CommandExecutor, TabExecutor {
    // the handle, aliases, and help line are only important when registering the splitter
    //  in a parent command manager system (eg another splitter) so we can just null them out.
    private final CommandSplit splitter = new CommandSplit(null, null, null);

    public DNDRootCommandExecutor() {
        addCommands();
    }

    private void addCommands() {
        DNDCommand[] cmds = new DNDCommand[]{
                new ListCmd(),
                new Toggle(),
                new ToggleAlert()
        };

        splitter.addCommands(cmds);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> argsAsList = new ArrayList<>(Arrays.asList(args));
        splitter.run(null, argsAsList, sender);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> argsAsList = new ArrayList<>(Arrays.asList(args));
        List<String> tabCompletions = splitter.tabComplete(argsAsList, sender);

        // sometimes the other DNDCommands can return a null, but bukkit turns
        //  those into the player name tab completions so we will have to filter
        //  them

        if (tabCompletions == null) return new ArrayList<>();

        return tabCompletions;
    }
}
