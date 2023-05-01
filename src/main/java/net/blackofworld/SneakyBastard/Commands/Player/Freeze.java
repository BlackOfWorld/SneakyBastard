package net.blackofworld.SneakyBastard.Commands.Player;

import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import net.blackofworld.SneakyBastard.Utils.BukkitReflection;
import net.blackofworld.SneakyBastard.Utils.Packets.IPacket;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketEvent;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketType;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.level.biome.BiomeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static net.minecraft.world.level.GameType.SURVIVAL;

@CommandInfo(command = "freeze", description = "Freezes the player", syntax = "<player>", category = CommandCategory.Player, requiredArgs = 1)
@ExtensionMethod({Player.class, PlayerExt.class})
public final class Freeze extends CommandBase {
    private static final List<Class> whitelistPackets = List.of(new Class[]{
            ClientboundKeepAlivePacket.class,
            ClientboundSystemChatPacket.class,
            ClientboundRespawnPacket.class,
            ClientboundContainerClosePacket.class,
            ClientboundForgetLevelChunkPacket.class,
            ClientboundPlayerInfoUpdatePacket.class,
            ClientboundPlayerInfoRemovePacket.class
    });
    boolean isOn = false;
    public void Execute(Player p, ArrayList<String> args) {
        Collection<? extends Player> pp = args.get(0).equals("*") ?
                Bukkit.getOnlinePlayers() :
                Collections.singleton(Bukkit.getPlayerExact(args.get(0)));
        if (pp.isEmpty()) {
            p.sendHelp(this, ChatColor.RED + "No player with such name!");
            return;
        }
        pp.parallelStream()/*.filter(pl -> !CommandManager.Instance.isTrusted(pl))*/.forEach(this::FreezePlayer);
        var name = args.get(0).equals("*") ? "Everyone" : args.get(0);
        p.Reply(name + " has been freezed");
        isOn = true;
    }

    @IPacket(direction = PacketType.Clientbound)
    public void clientboundFilter(PacketEvent e) {

        if(isOn && !whitelistPackets.contains(e.packet)) e.setCancelled(true);
    }

    public void FreezePlayer(Player p) {
        var sp = BukkitReflection.getServerPlayer(p);
        var level = BukkitReflection.getWorldLevel(p.getWorld());
        var data = level.getLevelData();
        int viewDistance = Math.min(Bukkit.getViewDistance(), p.getClientViewDistance()) + 3;
        for (int x = -viewDistance; x <= viewDistance; x++)
            for (int z = -viewDistance; z <= viewDistance; z++)
                p.sendPacket(new ClientboundForgetLevelChunkPacket(x, z));
        p.sendPacket(new ClientboundRespawnPacket(level.dimensionTypeId(), level.dimension(), BiomeManager.obfuscateSeed(level.getSeed()), SURVIVAL, SURVIVAL, level.isDebug(), level.isFlat(), (byte) 1, sp.getLastDeathLocation()));
    }

}
