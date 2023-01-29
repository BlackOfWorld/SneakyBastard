package net.blackofworld.sneakybastard.Command;

import net.blackofworld.sneakybastard.Start;
import net.blackofworld.sneakybastard.Utils.Packets.PacketInject.PacketEvent;
import net.blackofworld.sneakybastard.Utils.Packets.IPacket;
import net.blackofworld.sneakybastard.Utils.Packets.PacketInject.PacketListener;
import net.blackofworld.sneakybastard.Utils.Packets.PacketType;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.util.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static net.blackofworld.sneakybastard.Start.DEBUG_PACKETS;

public class CommandListener implements PacketListener, Listener {
    final HashMap<String, ArrayList<Tuple<Object, Method>>> events = new HashMap<>();
    final EventExecutor executor = (listener, event) -> {
        try {
            Class<?> clazz = event.getClass();
            do {
                ArrayList<Tuple<Object, Method>> methods;
                if ((methods = events.get(clazz.getSimpleName())) != null) {
                    methods.forEach((method -> {
                        try {
                            method.getB().invoke(method.getA(), event);
                        } catch (IllegalAccessException | InvocationTargetException ignored) {
                        }
                    }));
                    return;
                } else {
                    clazz = clazz.getSuperclass();
                }
            } while (clazz != Object.class);
        } catch (Throwable var5) {
            throw new EventException(var5);
        }
    };

    @SuppressWarnings("unchecked")
    CommandListener() {
        for (CommandBase cmd : CommandManager.Instance.commandList) {
            for (Method m : cmd.getClass().getDeclaredMethods()) {
                if (m.getParameterCount() == 1) {
                    var param = m.getParameters()[0];
                    boolean isValidEvent = true;
                    Class<?> isEventType = param.getType();
                    do {
                        isEventType = isEventType.getSuperclass();
                        if (isEventType == null || isEventType.equals(Object.class)) {
                            isValidEvent = false;
                            break;
                        }
                    } while (!isEventType.equals(Event.class));
                    if (!isValidEvent) break;
                    Bukkit.getPluginManager().registerEvent((Class<? extends Event>) param.getType(), this, EventPriority.LOW, executor, Start.Instance);
                    ArrayList<Tuple<Object, Method>> methods;
                    Tuple<Object, Method> tuple = new Tuple<>(cmd, m);
                    if ((methods = events.get(param.getType().getSimpleName())) != null) {
                        methods.add(tuple);
                        events.replace(param.getType().getSimpleName(), methods);
                    } else {
                        events.put(param.getType().getSimpleName(), new ArrayList<>(Collections.singleton(tuple)));
                    }
                }
            }
        }
    }

    @IPacket(direction = PacketType.INCOMING)
    public void onIncomingPacket(PacketEvent event) {
        if (DEBUG_PACKETS) return;
        var packet = event.packet;
        Bukkit.getScheduler().runTask(Start.Instance, () -> Bukkit.broadcastMessage(packet.toString()));
    }

    @IPacket(direction = PacketType.OUTGOING)
    public void onOutboundPacket(PacketEvent event) {
        if (DEBUG_PACKETS) return;
        var packet = event.packet;
        if (packet.getClass().equals(ClientboundSystemChatPacket.class)) return;
        Bukkit.getScheduler().runTask(Start.Instance, () -> Bukkit.broadcastMessage(packet.toString()));
    }

    public void Destroy() {
        events.clear();
    }
}
