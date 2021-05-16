package org.simondoesstuff.plugindonotdisturb.eventHandlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.simondoesstuff.plugindonotdisturb.DNDUserAlertFlag;
import org.simondoesstuff.plugindonotdisturb.DNDUserFlag;
import org.simondoesstuff.plugindonotdisturb.DNDUser;

public class RestrictGamemodeEventsHandler implements Listener {

    @EventHandler
    public void changeGamemode(PlayerGameModeChangeEvent e) {
        DNDUser user = DNDUser.retrieveUser(e.getPlayer());

        if (!user.canUseFlag(DNDUserFlag.restrictGamemode)) return;

        e.setCancelled(true);

        if (user.getAlertFlag(DNDUserAlertFlag.restrictGamemode)) {
            user.sendDNDMsg("Gamemode change blocked! &7(&nTried to change to:&f %s&7)", e.getNewGameMode().toString());
        }
    }
}
