package org.simondoesstuff.plugindonotdisturb.eventHandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.simondoesstuff.plugindonotdisturb.DNDUser;
import org.simondoesstuff.plugindonotdisturb.DNDUserAlertFlag;
import org.simondoesstuff.plugindonotdisturb.DNDUserFlag;

public class PreventDamageEventsHandler implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        DNDUser p = DNDUser.retrieveUser((Player) e.getEntity());

        if (!p.canUseFlag(DNDUserFlag.preventDamage)) return;

        e.setCancelled(true);

        if (p.getAlertFlag(DNDUserAlertFlag.preventDamage)) {
            p.sendDNDMsg("Damage blocked! &7(&nSource of damage:&f %s&7)", e.getCause());
        }
    }
}
