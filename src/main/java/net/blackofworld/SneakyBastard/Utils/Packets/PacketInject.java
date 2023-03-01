package net.blackofworld.SneakyBastard.Utils.Packets;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class PacketInject {

    private static final List<PacketMap> PACKET_MAP = Collections.synchronizedList(Lists.newArrayList());
    private static final Map<UUID, PacketPlayer> PLAYER_HANDLERS = Maps.newHashMap();
    private static Plugin plugin;

    public static PacketPlayer getPlayer(Player player) {
        if (PacketInject.PLAYER_HANDLERS.containsKey(player.getUniqueId())) {
            return PacketInject.PLAYER_HANDLERS.get(player.getUniqueId());
        }

        PacketPlayer packet = new PacketPlayer(player);
        PLAYER_HANDLERS.put(player.getUniqueId(), packet);
        return packet;
    }
    protected static boolean handleIncoming(PacketPlayer player, Packet<?> packet) {
        long start = System.nanoTime();

        PacketEvent event = new PacketEvent(packet, player, PacketType.INCOMING);
        PacketInject.PACKET_MAP.stream().filter((map) -> (map.packetType.equals(PacketType.INCOMING))).forEachOrdered((map) -> {
            try {
                map.method.invoke(map.listener, event);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });

        return !event.isCancelled();
    }

    protected static boolean handleOutgoing(PacketPlayer player, Packet<?> packet) {
        long start = System.nanoTime();

        PacketEvent event = new PacketEvent(packet, player, PacketType.OUTGOING);
        PacketInject.PACKET_MAP.stream().filter((map) -> (map.packetType.equals(PacketType.OUTGOING))).forEachOrdered((map) -> {
            try {
                map.method.invoke(map.listener, event);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });

        return !event.isCancelled();
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
        Iterator<PacketMap> it = PacketInject.PACKET_MAP.iterator();
        while (it.hasNext()) {
            PacketMap map = it.next();

            if (map.listener.getClass().equals(listener.getClass())) {
                it.remove();
                break;
            }
        }
    }

    public static PacketListener getInstance(Class<? extends PacketListener> clazz) {
        for (PacketMap map : PacketInject.PACKET_MAP) {
            if (map.listener.getClass().equals(clazz)) {
                return map.listener;
            }
        }

        return null;
    }

    public static List<PacketListener> getPacketListeners() {
        return PACKET_MAP.stream().map(packetMap -> packetMap.listener).collect(Collectors.toList());
    }

    public static void register(Plugin plugin) {
        if (PacketInject.plugin != null) {
            throw new UnsupportedOperationException("PacketInject is already registered!");
        }
        var listener = new Listener() {
            @EventHandler
            public void onPluginDisable(PluginDisableEvent e) {
                if (!e.getPlugin().equals(plugin)) {
                    return;
                }
                for(var a : PLAYER_HANDLERS.values()) {
                    a.unhook();
                }
            }
        };
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        PacketInject.plugin = plugin;
    }
    public interface PacketListener {
    }


    @ChannelHandler.Sharable
    public static class ChannelListener extends ChannelDuplexHandler {
        private final PacketPlayer player;

        ChannelListener(PacketPlayer player) {
            this.player = player;
        }

        @Override
        public void write(ChannelHandlerContext a, Object b, ChannelPromise c) throws Exception {
            if (PacketInject.handleOutgoing(player, (Packet<? extends net.minecraft.network.PacketListener>) b)) {
                super.write(a, b, c);
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext a, Object b) throws Exception {
            if (PacketInject.handleIncoming(player, (Packet<? extends net.minecraft.network.PacketListener>) b)) {
                super.channelRead(a, b);
            }
        }
    }
    public static class PacketEvent {
        public final Packet<?> packet;
        public final PacketPlayer player;
        public final PacketType Direction;
        @Getter
        @Setter
        private boolean cancelled = false;

        public PacketEvent(Packet<?> packet, PacketPlayer p, PacketType direction) {
            this.packet = packet;
            this.player = p;
            this.Direction = direction;
        }
    }

    record PacketMap(Method method, PacketListener listener, PacketType packetType) {

    }
}

