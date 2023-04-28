package net.blackofworld.SneakyBastard.Commands.Server;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Queues;
import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import net.blackofworld.SneakyBastard.Start;
import net.blackofworld.SneakyBastard.Utils.Events.TickEvent;
import net.blackofworld.SneakyBastard.Utils.Packets.IPacket;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketEvent;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketInjector;
import net.minecraft.network.protocol.game.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Queue;

import static net.blackofworld.SneakyBastard.Utils.Packets.PacketType.INCOMING;
import static net.blackofworld.SneakyBastard.Utils.Packets.PacketType.OUTGOING;

@CommandInfo(command = "fakelag", description = "Fakes server lag.", Syntax = "", category = CommandCategory.Server)
@ExtensionMethod({Player.class, PlayerExt.class})
public class FakeLag extends CommandBase implements PacketInjector.PacketListener {

    EvictingQueue<PacketEvent> sync = EvictingQueue.create(50000); // seems reasonable?
    Queue<PacketEvent> packets = Queues.synchronizedQueue(sync);
    public FakeLag() {PacketInjector.registerListener(this);}
    boolean isOn = false;
    boolean doCleanup = false;
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        p.Reply("FakeLag is now " + (!isOn ? "on!" : "off!"));

        // Make sure the chat packet arrives... lol
        if(isOn) {
            isOn = false;
            doCleanup = true;
        } else {
            Bukkit.getScheduler().runTaskLater(Start.Instance, () -> {
                isOn = true;
            }, 5L);
        }
    }


    public void onTick(TickEvent event) {
        // every 3 ticks, bit AND for performance reason
        if (!doCleanup || (event.tick & 0x2) == 2) return;

        int packetCount = 0;
        PacketEvent pe;
        while((pe = sync.poll()) != null) {
            if (packetCount++ > 5000) continue;
            if (pe.Direction == INCOMING) PacketInjector.Instance.receivePacket(pe.player, pe.packet);
            if (pe.Direction == OUTGOING) PacketInjector.Instance.sendPacket(pe.player, pe.packet);
        }
        doCleanup = false;
    }
    @IPacket(direction = INCOMING)
    public void inboundPacket(PacketEvent event) {
        if (!isOn || event.packet.getClass() == ServerboundKeepAlivePacket.class || event.packet.getClass() == ServerboundChatPacket.class || event.packet.getClass() == ServerboundChatAckPacket.class) {
            return;
        }
        event.setCancelled(true);
        packets.add(event);
    }
    @IPacket(direction = OUTGOING)
    public void outboundPacket(PacketEvent event) {
        if (!isOn || event.packet.getClass() == ClientboundKeepAlivePacket.class || event.packet.getClass() == ClientboundPlayerChatPacket.class || event.packet.getClass() == ClientboundSystemChatPacket.class) {
            return;
        }
        event.setCancelled(true);
        packets.add(event);
    }
}
