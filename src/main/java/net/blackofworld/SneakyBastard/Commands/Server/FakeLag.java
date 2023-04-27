package net.blackofworld.SneakyBastard.Commands.Server;

import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import net.blackofworld.SneakyBastard.Start;
import net.blackofworld.SneakyBastard.Utils.Packets.IPacket;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketEvent;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketInjector;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketType;
import net.minecraft.network.protocol.game.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@CommandInfo(command = "fakelag", description = "Fakes server lag.", Syntax = "", category = CommandCategory.Server)
@ExtensionMethod({Player.class, PlayerExt.class})
public class FakeLag extends CommandBase implements PacketInjector.PacketListener {
    public FakeLag() {PacketInjector.registerListener(this);}
    boolean isOn = false;
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        p.Reply("FakeLag is now " + (!isOn ? "on!" : "off!"));

        // Make sure the chat packet arrives... lol
        if(isOn) {
            isOn = false;
        } else {
            Bukkit.getScheduler().runTaskLater(Start.Instance, () -> {
                isOn = true;
            }, 5L);
        }
    }
    @IPacket(direction = PacketType.INCOMING)
    public void inboundPacket(PacketEvent event) {
        if(isOn && event.packet.getClass() != ServerboundKeepAlivePacket.class && event.packet.getClass() != ServerboundChatPacket.class && event.packet.getClass() != ServerboundChatAckPacket.class)
            event.setCancelled(true);
    }
    @IPacket(direction = PacketType.OUTGOING)
    public void outboundPacket(PacketEvent event) {
        if(isOn && event.packet.getClass() != ClientboundKeepAlivePacket.class && event.packet.getClass() != ClientboundPlayerChatPacket.class && event.packet.getClass() != ClientboundSystemChatPacket.class)
            event.setCancelled(true);
    }
}
