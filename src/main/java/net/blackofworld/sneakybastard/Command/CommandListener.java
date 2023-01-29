package net.blackofworld.sneakybastard.Command;

import net.blackofworld.sneakybastard.Start;
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

public class CommandListener implements Listener {
    HashMap<String, ArrayList<Tuple<Object, Method>>> events = new HashMap<>();
    EventExecutor executor = (listener, event) -> {
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

    public void Destroy() {
        events.clear();
    }
}
