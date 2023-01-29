package net.blackofworld.sneakybastard.Utils.Packets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import net.blackofworld.sneakybastard.Start;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A simple packet injector, to modify the packets sent and received
 */
public class PacketInjector extends ChannelDuplexHandler {

    // There are a lot more reads than writes, so performance should be okay
    private static final List<PacketListener> packetListeners = new CopyOnWriteArrayList<>();
    public static TinyProtocol tinyProtocol = null;

    public PacketInjector() {
        tinyProtocol = new TinyProtocol(Start.Instance) {
            @Override
            public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
                PacketEvent event = new PacketEvent(
                        packet,
                        PacketEvent.ConnectionDirection.TO_CLIENT,
                        receiver
                );

                for (PacketListener packetListener : packetListeners) {
                    try {
                        packetListener.onPacketSend(event);
                    } catch (Exception ignored) {
                    }
                }

                if (!event.isCancelled()) {
                    return packet;
                }
                return null;
            }

            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
                PacketEvent event = new PacketEvent(
                        packet,
                        PacketEvent.ConnectionDirection.TO_SERVER,
                        sender
                );
                for (PacketListener packetListener : packetListeners) {
                    try {
                        packetListener.onPacketReceived(event);
                    } catch (Exception ignored) {
                    }
                }

                if (!event.isCancelled()) {
                    return packet;
                }
                return null;
            }
        };
    }

    public static void addPacketListener(PacketListener packetListener) {
        Objects.requireNonNull(packetListener, "packetListener can not be null");
        packetListeners.add(packetListener);
    }

    /**
     * Removes a {@link PacketListener}
     *
     * @param packetListener The {@link PacketListener} to remove
     */
    public static void removePacketListener(PacketListener packetListener) {
        packetListeners.remove(packetListener);
    }

    /**
     * Returns the amount of listeners
     *
     * @return The amount of listeners
     */
    int getListenerAmount() {
        return packetListeners.size();
    }
}