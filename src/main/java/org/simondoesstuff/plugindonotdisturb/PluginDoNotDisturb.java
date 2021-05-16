package org.simondoesstuff.plugindonotdisturb;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.simondoesstuff.plugindonotdisturb.commandSystem.commandHandlers.DNDRootCommandExecutor;
import org.simondoesstuff.plugindonotdisturb.eventHandlers.*;
import org.simondoesstuff.plugindonotdisturb.eventHandlers.protocolLibEvents.RemoveRespawnScreenHandling;
import org.simondoesstuff.plugindonotdisturb.messagingSystem.DNDMessenger;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public final class PluginDoNotDisturb extends JavaPlugin {
    private static PluginDoNotDisturb instance;

    /**
     * A makeshift singleton only created with the constructor
     * only one time through the spigot API.
     *
     * @throws IllegalAccessException Get an instance using getInstance()
     */
    public PluginDoNotDisturb() throws IllegalAccessException {
        if (instance != null) throw new IllegalAccessException("Use getInstance() instead.");

        instance = this;
    }

    public static PluginDoNotDisturb getInstance() {
        if (instance == null) {
            try {
                instance = new PluginDoNotDisturb();
            } catch (IllegalAccessException e) {
                // this will never happen.
                e.printStackTrace();
            }
        }

        return instance;
    }

    // -------------------------------------------
    // Singleton stuff above
    // -------------------------------------------

    private ProtocolManager protoLib;
    private DNDRootCommandExecutor rootCmdHandler = new DNDRootCommandExecutor();

    public ProtocolManager getProtoLib() {
        return protoLib;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        importUserStorage();

        protoLib = ProtocolLibrary.getProtocolManager();

        registerCommands();
        registerEvents();

        DNDMessenger.send(Bukkit.getConsoleSender(), "&aEnabled successfully!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        exportUserStorage();

        DNDMessenger.send(Bukkit.getConsoleSender(), "&aDisabled successfully!");
    }

    private void registerEvents() {
        Listener[] listeners = new Listener[] {
                new PreventDamageEventsHandler(),
                new PreventDeathEventsHandler(),
                new PreventDisconnectEventsHandler(),
                new PreventMobTargetingEventsHandler(),
                new PreventTeleportEventsHandler(),
                new RestrictGamemodeEventsHandler(),
                new RetainEffectsEventsHandler()
        };

        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }

        // -------------------------------------------------
        //  now registering protocol lib events
        // -------------------------------------------------

        // removes the respawn screen when preventDeath flag is enabled
        new RemoveRespawnScreenHandling().registerListeners();
    }

    @SuppressWarnings("ConstantConditions")
    private void registerCommands() {
        PluginCommand rootCmdSpigot = this.getCommand("dnd");   // must and will never be null
        rootCmdSpigot.setExecutor(rootCmdHandler);
        rootCmdSpigot.setTabCompleter(rootCmdHandler);
    }

    private void importUserStorage() {
        File file = new File(getDataFolder(), "userdata.txt");
        HashMap<UUID, DNDUser> storageIn;

        try (FileInputStream fileInStream = new FileInputStream(file);
                ObjectInputStream objectInStream = new ObjectInputStream(fileInStream)) {

            storageIn = (HashMap<UUID, DNDUser>) objectInStream.readObject();
        } catch (Exception e) {

            // in the case that there is some issue -> create a fresh storage
            // eg: no storage existed yet and the file is empty

            storageIn = new HashMap<>();
        }

        DNDUser.setStorage(storageIn);
    }

    private void exportUserStorage() {
        File dataFolder = getDataFolder();
        File file = new File(dataFolder, "userdata.txt");

        HashMap<UUID, DNDUser> storageOut = DNDUser.getStorage();

        try {
            dataFolder.mkdir();
            file.createNewFile();

            try (FileOutputStream fileOutStream = new FileOutputStream(file);
                 ObjectOutputStream objectOutStream = new ObjectOutputStream(fileOutStream)) {

                objectOutStream.writeObject(storageOut);
                objectOutStream.flush();
            }
        } catch (IOException ignored) {}
    }
}
