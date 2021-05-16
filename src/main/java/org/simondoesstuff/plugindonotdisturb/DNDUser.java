package org.simondoesstuff.plugindonotdisturb;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.simondoesstuff.plugindonotdisturb.flagChangeHandling.FlagChangeHandler;
import org.simondoesstuff.plugindonotdisturb.messagingSystem.DNDMessenger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DNDUser implements Serializable {

    // so that we can convert a player to a DNDUser

    private static HashMap<UUID, DNDUser> store;

    /**
     * Directly replaces the DNDUser storage.
     * Only meant to be used once at startup
     * to replace the storage with one that
     * was previously serialized.
     *
     * @param newStore Deserialized hashmap
     */
    static void setStorage(HashMap<UUID, DNDUser> newStore) {
        store = newStore;
    }

    /**
     * Directly retrieves the DNDUser storage.
     * Only meant to be used once at shutdown
     * to serialize the storage.
     */
    static HashMap<UUID, DNDUser> getStorage() {
        return store;
    }

    public static DNDUser retrieveUser(Player p) {
        return store.computeIfAbsent(p.getUniqueId(), k -> new DNDUser(p));
    }


    // instance content

    private int flags = 0;          // serialized
    private int alertFlags = 0;     // serialized
    private UUID uuid;              // serialized

    transient private Player base;
    transient private FlagChangeHandler flagChangeHandler = new FlagChangeHandler(this);

    public DNDUser(Player base) {
        this.base = base;
        this.uuid = base.getUniqueId();
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();

        // we will attempt to set the transient base field to something here
        //  but there is a chance the method will return null anyway.
        // So we also have handling in getBase() for the null
        base = Bukkit.getPlayer(uuid);

        flagChangeHandler = new FlagChangeHandler(this);
    }

    public Player getBase() {
        if (base == null || !base.isValid()) base = Bukkit.getPlayer(getUUID());

        return base;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setFlag(DNDUserFlag flag, boolean newValue) {
        if (newValue) {
            int mask = 1 << flag.ordinal();
            flags |= mask;
        } else {
            if (canUseFlag(flag)) {
                int offset = 1 << flag.ordinal();
                flags -= offset;
            }
        }

        flagChangeHandler.onChange(flag, newValue);
    }

    public void setAlertFlag(DNDUserAlertFlag flag, boolean newValue) {
        if (newValue) {
            int mask = 1 << flag.ordinal();
            alertFlags |= mask;
        } else {
            if (getAlertFlag(flag)) {
                int offset = 1 << flag.ordinal();
                alertFlags -= offset;
            }
        }
    }

    /**
     * Strictly gets the raw value of a DNDUserFlag.
     * Ignores the permissions of the user.
     */
    public boolean getFlag(DNDUserFlag flag) {
        int value = flags >> flag.ordinal();
        value &= 1;

        return value != 0;
    }

    /**
     * Gets the value of a DNDUserFlag if the user has
     * permissions for the flag.
     *
     * @return if the user has access to the flag
     */
    public boolean canUseFlag(DNDUserFlag flag) {
        String permission = "dnd.flag." + flag.name();

        if (!getBase().hasPermission(permission)) return false;

        return getFlag(flag);
    }

    /**
     * @return a distinct list of flags a user can
     * access given their permission nodes.
     */
    public List<DNDUserFlag> getAccessibleFlags() {
        String basePermNode = "dnd.flag.";

        Player base = getBase();
        List<DNDUserFlag> flags = new ArrayList<>();

        for (DNDUserFlag flag : DNDUserFlag.values()) {
            if (base.hasPermission(basePermNode + flag.name())) {
                flags.add(flag);
            }
        }

        return flags;
    }

    /**
     * @return a distinct list of alert flags a user can
     * access given their permission nodes.
     */
    public List<DNDUserAlertFlag> getAccessibleAlertFlags() {
        String basePermNode = "dnd.flag.";

        Player base = getBase();
        List<DNDUserAlertFlag> flags = new ArrayList<>();

        for (DNDUserAlertFlag flag : DNDUserAlertFlag.values()) {
            if (base.hasPermission(basePermNode + flag.name())) {
                flags.add(flag);
            }
        }

        return flags;
    }

    public boolean getAlertFlag(DNDUserAlertFlag flag) {
        int value = alertFlags >> flag.ordinal();
        value &= 1;

        return value != 0;
    }

    /**
     * Wraps DNDMessenger.send(...)
     */
    public void sendDNDMsg(String message, Object... args) {
        DNDMessenger.send(getBase(), message, args);
    }
}
