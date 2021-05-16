package org.simondoesstuff.plugindonotdisturb.commandSystem;

import org.bukkit.command.CommandSender;
import org.simondoesstuff.plugindonotdisturb.messagingSystem.DNDMessenger;

import java.util.ArrayList;
import java.util.List;

public class AutoHelpCmd implements DNDCommand {
    private final int ENTRIESPERPAGE;
    private String[] helpPages;                     // generated lazily
    private List<String> entryIndex;

    public AutoHelpCmd(int entriesPerPage) {
        this.ENTRIESPERPAGE = entriesPerPage;
    }

    @Override
    public String getHandle() {
        return "help";
    }

    @Override
    public String[] getAliases() {
        return null;
    }

    @Override
    public String getHelpLine() {
        return getHandle() + " [Page Number]\nDisplay all available sub-commands.";
    }

    @Override
    public void run(String label, List<String> args, CommandSender sender) {
        if (args.size() > 1) {
            DNDMessenger.send(sender, "&4Only needed one argument.");
            return;
        }

        int pageNum = 0;

        if (args.size() == 1) {
            try {
                pageNum = Integer.parseInt(args.get(0)) - 1;

                // if they entered something that wasn't a number
                //  then just go ahead and ignore it
            } catch (NumberFormatException ignored) {
            }
        }

        if (pageNum < 0) {
            pageNum = 0;
            DNDMessenger.send(sender, "&4The page number can not be lower than 1.");
        }

        // if there is no help page for pageNum -> create it

        if (helpPages[pageNum] == null) {

            // here we are creating a help page

            StringBuilder fullPageText = new StringBuilder();

            int entriesAmount = 0;

            if (entryIndex != null)
                entriesAmount = entryIndex.size();

            fullPageText.append("&f&l&nIndex:&8 [&a").append(pageNum + 1).append("&8/&a")
                    .append(entriesAmount / ENTRIESPERPAGE + 1)  // using integer math, automatically flats the decimal
                    .append("&8]&7");

            if (entryIndex != null) {
                // add the entries from the entryIndex if its not null

                int index = pageNum * ENTRIESPERPAGE;

                while (index < ENTRIESPERPAGE && index < entriesAmount) {
                    fullPageText
                            .append("\n")
                            .append(entryIndex.get(index));

                    index++;
                }

            }

            helpPages[pageNum] = fullPageText.toString();
        } // finished making the help page

        // the page is guaranteed to exist: send it

        DNDMessenger.send(sender, helpPages[pageNum]);
    }

    @Override
    public List<String> tabComplete(List<String> args, CommandSender sender) {
        return null;
    }

    public void refresh(List<String> newIndex) {
        // resetting helpPages will trigger the recreation of all
        //  help pages because they are created lazily

        entryIndex = newIndex;

        reformatEntries();

        helpPages = new String[entryIndex.size()];
    }


    /**
     * Automatically applies coloring to the help line entries.
     * Change this method to change the coloring syntax.
     */
    private void reformatEntries() {
        ArrayList<String> newIndex = new ArrayList<>();

        for (String entry : entryIndex) {
            if (entry == null) continue;

            entry = "&7/" + entry;
            entry = entry.replace("[", "[&a");
            entry = entry.replace("]", "&7]");
            entry = entry.replace("\n", "\n&f  ");

            newIndex.add(entry);
        }

        entryIndex = newIndex;
    }
}