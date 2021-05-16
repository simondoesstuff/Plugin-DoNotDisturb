package org.simondoesstuff.plugindonotdisturb.eventHandlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.simondoesstuff.plugindonotdisturb.DNDUserAlertFlag;
import org.simondoesstuff.plugindonotdisturb.DNDUserFlag;
import org.simondoesstuff.plugindonotdisturb.DNDUser;

public class PreventTeleportEventsHandler implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        DNDUser user = DNDUser.retrieveUser(e.getPlayer());

        if (!user.canUseFlag(DNDUserFlag.preventTeleport)) return;

        e.setCancelled(true);

        if (user.getAlertFlag(DNDUserAlertFlag.preventTeleport)) {
            user.sendDNDMsg("Teleportation blocked! &7(&nCause:&f %s&7)", e.getCause().toString());
        }
    }
}
