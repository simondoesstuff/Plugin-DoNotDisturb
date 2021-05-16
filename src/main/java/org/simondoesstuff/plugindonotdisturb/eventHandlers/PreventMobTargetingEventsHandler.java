package org.simondoesstuff.plugindonotdisturb.eventHandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.simondoesstuff.plugindonotdisturb.DNDUser;
import org.simondoesstuff.plugindonotdisturb.DNDUserFlag;

public class PreventMobTargetingEventsHandler implements Listener {

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (!(e.getTarget() instanceof Player)) return;

        DNDUser user = DNDUser.retrieveUser((Player) e.getTarget());

        if (!user.canUseFlag(DNDUserFlag.preventMobTargeting)) return;

        e.setCancelled(true);
    }
}
