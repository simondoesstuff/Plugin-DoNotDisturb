package org.simondoesstuff.plugindonotdisturb.eventHandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.simondoesstuff.plugindonotdisturb.DNDUserAlertFlag;
import org.simondoesstuff.plugindonotdisturb.DNDUserFlag;
import org.simondoesstuff.plugindonotdisturb.DNDUser;

public class PreventDeathEventsHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDie(PlayerDeathEvent e) {
        DNDUser p = DNDUser.retrieveUser(e.getEntity());

        if (!p.canUseFlag(DNDUserFlag.preventDeath)) return;

        if (p.getAlertFlag(DNDUserAlertFlag.preventDeath)) {
            e.setDeathMessage(e.getEntity().getName() + " survived death");
            p.sendDNDMsg("Resurrected!");
        } else {
            e.setDeathMessage("");
        }

        e.setDroppedExp(0);
        e.getDrops().clear();
        e.setKeepInventory(true);

        p.getBase().setHealth(.1);    // resurrect player
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        DNDUser p = DNDUser.retrieveUser((Player) e.getEntity());

        if (!p.canUseFlag(DNDUserFlag.preventDeath)) return;

        double pHealth = p.getBase().getHealth();

        if (pHealth > e.getFinalDamage()) return;

        e.setDamage(pHealth - .1);

        if (p.getAlertFlag(DNDUserAlertFlag.preventDeath)) {
            p.sendDNDMsg("Fatal damage blocked! &7(&nSource of damage:&f %s&7)", e.getCause());
        }
    }
}
