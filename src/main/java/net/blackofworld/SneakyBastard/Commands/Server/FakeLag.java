package net.blackofworld.SneakyBastard.Commands.Server;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Queues;
import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import net.blackofworld.SneakyBastard.Start;
import net.blackofworld.SneakyBastard.Utils.BukkitReflection;
import net.blackofworld.SneakyBastard.Utils.Events.TickEvent;
import net.blackofworld.SneakyBastard.Utils.Packets.IPacket;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketEvent;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketInjector;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import oshi.util.tuples.Triplet;

import java.util.ArrayList;
import java.util.Queue;

@CommandInfo(command = "fakelag", description = "Fakes server lag.", Syntax = "", category = CommandCategory.Server)
@ExtensionMethod({Player.class, PlayerExt.class})
public class FakeLag extends CommandBase implements PacketInjector.PacketListener {

    EvictingQueue<Triplet<Player, Packet<?>, PacketType>> sync = EvictingQueue.create(50_000); // seems reasonable?
    Queue<Triplet<Player, Packet<?>, PacketType>> packets = Queues.synchronizedQueue(sync); // very ugly, i know
    public FakeLag() {PacketInjector.registerListener(this);}
    boolean isOn = false;
    int doCleanup = 0;
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        p.Reply("FakeLag is now " + (!isOn ? "on!" : "off!"));

        // Make sure the chat packet arrives... lol
        if(isOn) {
            isOn = false;
            doCleanup = 1;
        } else {
            Bukkit.getScheduler().runTaskLater(Start.Instance, () -> {
                isOn = true;
            }, 5L);
        }
    }


    public void onTick(TickEvent event) {
        // every 2 ticks, bit AND for performance reason,
        if((event.tick & 0x8) == 8 && sync.remainingCapacity() < 200) doCleanup = 2;
        if (doCleanup == 0 || ((event.tick & 0x2) != 2)) return;

        int packetCount = 0;

        Triplet<Player, Packet<?>, PacketType> pe;
        while ((pe = sync.poll()) != null) {
            if (packetCount++ > 5000) continue;
            switch (pe.getC()) {
                case Serverbound -> PacketInjector.Instance.receivePacket(pe.getA(), pe.getB());
                case Clientbound -> PacketInjector.Instance.sendPacket(pe.getA(), pe.getB());
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            BukkitReflection.refreshPlayer(p);
        }
        doCleanup = 0;

    }
    @IPacket(direction = PacketType.Serverbound)
    public void inboundPacket(PacketEvent event) {
        if (!isOn || event.packet.getClass() == ServerboundKeepAlivePacket.class || event.packet.getClass() == ServerboundChatPacket.class || event.packet.getClass() == ServerboundChatAckPacket.class) {
            return;
        }

        event.setCancelled(true);
        packets.add(new Triplet<>(event.player, event.packet, event.Direction));
    }
    @IPacket(direction = PacketType.Clientbound)
    public void outboundPacket(PacketEvent event) {
        if (!isOn || event.packet.getClass() == ClientboundKeepAlivePacket.class || event.packet.getClass() == ClientboundPlayerChatPacket.class || event.packet.getClass() == ClientboundSystemChatPacket.class) {
            return;
        }

        event.setCancelled(true);
        packets.add(new Triplet<>(event.player, event.packet, event.Direction));
    }
}
