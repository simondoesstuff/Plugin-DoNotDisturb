package org.simondoesstuff.plugindonotdisturb.eventHandlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.simondoesstuff.plugindonotdisturb.DNDUserAlertFlag;
import org.simondoesstuff.plugindonotdisturb.DNDUserFlag;
import org.simondoesstuff.plugindonotdisturb.DNDUser;

public class PreventDisconnectEventsHandler implements Listener {

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        DNDUser user = DNDUser.retrieveUser(e.getPlayer());

        if (!user.canUseFlag(DNDUserFlag.preventDisconnect)) return;

        e.setCancelled(true);

        if (user.getAlertFlag(DNDUserAlertFlag.preventDisconnect)) {
            sendFeedback(user, e.getReason());
        }
    }

    private void sendFeedback(DNDUser user, String reason) {
        if (reason == null || reason.isEmpty()) reason = "Unknown";

        String reasonStr = "&7(&nReason:&f " + reason + "&7)";

        user.sendDNDMsg("Disconnection Blocked! %s", reasonStr);
    }
}
