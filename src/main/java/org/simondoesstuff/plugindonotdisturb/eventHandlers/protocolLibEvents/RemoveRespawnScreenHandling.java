package org.simondoesstuff.plugindonotdisturb.eventHandlers.protocolLibEvents;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.simondoesstuff.plugindonotdisturb.DNDUser;
import org.simondoesstuff.plugindonotdisturb.DNDUserFlag;
import org.simondoesstuff.plugindonotdisturb.PluginDoNotDisturb;
import org.simondoesstuff.plugindonotdisturb.eventHandlers.protocolLibEvents.packetWrappers.WrapperPlayServerCombatEvent;

/**
 * Removes the "YOU DIED" screen when you die if the player has the
 *  DNDUserFlag.preventDeath flag enabled.
 */
public class RemoveRespawnScreenHandling {
    private final ProtocolManager protoLib;

    public RemoveRespawnScreenHandling() {
        this.protoLib = PluginDoNotDisturb.getInstance().getProtoLib();
    }

    public void registerListeners() {
        protoLib.addPacketListener(new PacketAdapter(PluginDoNotDisturb.getInstance(), PacketType.Play.Server.COMBAT_EVENT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerCombatEvent packet = new WrapperPlayServerCombatEvent(event.getPacket());

                DNDUser user = DNDUser.retrieveUser(event.getPlayer());

                if (!user.canUseFlag(DNDUserFlag.preventDeath)) return;

                if (packet.getEvent() != EnumWrappers.CombatEventType.ENTITY_DIED) return;

                event.setCancelled(true);
            }
        });
    }
}
