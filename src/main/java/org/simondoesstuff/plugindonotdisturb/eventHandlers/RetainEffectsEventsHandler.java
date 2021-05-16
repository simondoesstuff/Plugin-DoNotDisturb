package org.simondoesstuff.plugindonotdisturb.eventHandlers;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.simondoesstuff.plugindonotdisturb.DNDUserAlertFlag;
import org.simondoesstuff.plugindonotdisturb.DNDUserFlag;
import org.simondoesstuff.plugindonotdisturb.DNDUser;

public class RetainEffectsEventsHandler implements Listener {

    @EventHandler
    public void onEffects(EntityPotionEffectEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) return;

        DNDUser user = DNDUser.retrieveUser((Player) e.getEntity());

        if (!user.canUseFlag(DNDUserFlag.retainEffects)) return;

        e.setCancelled(true);

        if (user.getAlertFlag(DNDUserAlertFlag.retainEffects)) {
            if (e.getCause() == EntityPotionEffectEvent.Cause.EXPIRATION) return;

            user.sendDNDMsg("Potion effects retained! &7(&nReason for change:&f %s&7)", e.getCause().toString());
        }
    }
}
