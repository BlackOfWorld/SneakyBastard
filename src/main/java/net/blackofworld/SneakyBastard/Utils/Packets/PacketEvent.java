package net.blackofworld.SneakyBastard.Utils.Packets;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;

public class PacketEvent {
    public final Packet<?> packet;
    public final Player player;
    public final PacketType Direction;
    @Getter
    @Setter
    private boolean cancelled = false;

    public PacketEvent(Packet<?> packet, Player p, PacketType direction) {
        this.packet = packet;
        this.player = p;
        this.Direction = direction;
    }
}