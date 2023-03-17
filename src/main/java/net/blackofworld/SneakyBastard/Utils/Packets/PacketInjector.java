package net.blackofworld.SneakyBastard.Utils.Packets;

import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class PacketInjector extends LightInjector {

    public static PacketInjector Instance;
    public interface PacketListener {

    }
    private static final List<PacketMap> PACKET_MAP  = Collections.synchronizedList(Lists.newArrayList());

    public PacketInjector(Plugin plugin) {
        super(plugin);
        Instance = this;
    }
    public static List<PacketListener> getPacketListeners() {
        return PACKET_MAP.stream().map(PacketMap::listener).collect(Collectors.toList());
    }
    public static void registerListener(PacketListener listener) {
        Class<?> clazz = listener.getClass();
        while (clazz != null) {
            for (Method method : clazz.getMethods()) {
                IPacket handler = method.getAnnotation(IPacket.class);
                if (handler != null) {
                    method.setAccessible(true);
                    PACKET_MAP.add(new PacketMap(method, listener, handler.direction()));
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    public static void unregisterListener(PacketListener listener) {
        Iterator<PacketMap> it = PacketInjector.PACKET_MAP.iterator();
        while (it.hasNext()) {
            PacketMap map = it.next();

            if (map.listener().getClass().equals(listener.getClass())) {
                it.remove();
                break;
            }
        }
    }

    @Nullable
    @Override
    protected Object onPacketReceiveAsync(@Nullable Player sender, Channel channel, Object packet_) {
        AtomicReference<Packet<?>> packet = new AtomicReference<>((Packet<?>) packet_);
        PacketEvent event = new PacketEvent(packet.get(), sender, PacketType.INCOMING);
        for (PacketMap map : PacketInjector.PACKET_MAP) {
            if ((map.packetType().equals(PacketType.INCOMING))) {
                try {
                    packet.set((Packet<?>) map.m().invoke(map.listener(), event));
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return event.isCancelled() ? null : event.packet;
    }

    @Nullable
    @Override
    protected Object onPacketSendAsync(@Nullable Player receiver, Channel channel, Object packet) {
        PacketEvent event = new PacketEvent((Packet<?>) packet, receiver, PacketType.OUTGOING);
        PacketInjector.PACKET_MAP.stream().filter((map) -> (map.packetType().equals(PacketType.OUTGOING))).forEachOrdered((map) -> {
            try {
                map.m().invoke(map.listener(), event);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        return event.isCancelled() ? null : event.packet;
    }
}
