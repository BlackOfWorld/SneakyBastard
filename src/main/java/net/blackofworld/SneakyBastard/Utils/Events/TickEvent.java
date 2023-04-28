package net.blackofworld.SneakyBastard.Utils.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TickEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    public short tick;
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
