package org.simondoesstuff.plugindonotdisturb.flagChangeHandling;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.simondoesstuff.plugindonotdisturb.DNDUser;
import org.simondoesstuff.plugindonotdisturb.DNDUserFlag;

public class FlagChangeHandler {
    private DNDUser user;

    public FlagChangeHandler(DNDUser user) {
        this.user = user;
    }

    public void onChange(DNDUserFlag flag, boolean newState) {
        Player basePlayer = user.getBase();

        switch (flag) {
            case preventMobTargeting:
                //-------------------------------------------------------------------------

                if (newState) {
                    // if preventMobTargeting is enabled, we must remove all
                    //  active mob targets on them

                    for (LivingEntity livingEntity : basePlayer.getWorld().getLivingEntities()) {
                        if (!(livingEntity instanceof Mob)) continue;

                        Mob mob = (Mob) livingEntity;
                        LivingEntity target = mob.getTarget();

                        if (target == null) continue;

                        if (target.equals(basePlayer)) mob.setTarget(null);
                    }
                }

                break;
        }
    }
}
