package net.blackofworld.SneakyBastard.Command;

import lombok.SneakyThrows;
import net.blackofworld.SneakyBastard.Start;
import net.blackofworld.SneakyBastard.Start.Config;
import net.blackofworld.SneakyBastard.Utils.Events.TickEvent;
import net.blackofworld.SneakyBastard.Utils.Packets.IPacket;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketEvent;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketInjector.PacketListener;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketType;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.util.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CommandListener implements Listener, PacketListener, Runnable {
    private static final TickEvent tickEvent = new TickEvent();
    final HashMap<String, ArrayList<Tuple<Object, Method>>> events = new HashMap<>();

    @SuppressWarnings("unchecked")
    CommandListener() {
        for (CommandBase cmd : CommandManager.Instance.commandList) {
            for (Method m : cmd.getClass().getDeclaredMethods()) {
                if (m.getParameterCount() == 1) {
                    var param = m.getParameters()[0];
                    if (param.getType() == PacketEvent.class) continue;
                    if (param.getType() != TickEvent.class) {
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
                        EventHandler handler = m.getAnnotation(EventHandler.class);
                        boolean ignoreCanceled = false;
                        EventPriority priority = EventPriority.NORMAL;
                        if(handler != null) {
                            ignoreCanceled = handler.ignoreCancelled();
                            priority = handler.priority();
                        }
                        Bukkit.getPluginManager().registerEvent((Class<? extends Event>) param.getType(), this, priority, this::execute, Start.Instance, ignoreCanceled);
                    }
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
        Bukkit.getScheduler().runTaskTimer(Start.Instance, this, 1L, 1L);
    }

    @IPacket(direction = PacketType.Serverbound)
    public void logIncomingPacket(PacketEvent event) {
        if (!Config.LogPackets) return;
        var packet = event.packet;
        if (packet.toString().contains("BlockChange") || packet.toString().contains("KeepAlive")) {
            return;
        }
        Bukkit.getScheduler().runTask(Start.Instance, () -> Bukkit.broadcastMessage(packet.toString()));
    }

    @IPacket(direction = PacketType.Clientbound)
    public void logOutboundPacket(PacketEvent event) {
        if (!Config.LogPackets) return;
        var packet = event.packet;
        if (packet.toString().contains("BlockChange") || packet.toString().contains("KeepAlive")) return;
        if (packet.getClass().equals(ClientboundSystemChatPacket.class)) return;
        Bukkit.getScheduler().runTaskAsynchronously(Start.Instance, () -> Bukkit.broadcastMessage(packet.toString()));
    }

    public void Destroy() {
        events.clear();
    }

    private short tickCount = 0;
    @SneakyThrows
    @Override
    public void run() {
        tickEvent.tick = tickCount++;
        execute(null, tickEvent);
    }

    @SneakyThrows
    private void execute(Listener _listener, @NotNull Event event) {
        Class<?> clazz = event.getClass();
        do {
            ArrayList<Tuple<Object, Method>> methods;
            if ((methods = events.get(clazz.getSimpleName())) == null) {
                clazz = clazz.getSuperclass();
                continue;
            }
            methods.forEach(method -> {
                try {method.getB().invoke(method.getA(), event);} catch (IllegalAccessException | InvocationTargetException ignored) {}
            });
            return;
        } while (clazz != Object.class);
    }
}
