package net.blackofworld.SneakyBastard.Extensions;

import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandManager;
import net.blackofworld.SneakyBastard.Commands.Help;
import net.blackofworld.SneakyBastard.Utils.BukkitReflection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static lombok.Validate.NotNull;
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
        if(value) MinecraftServer.getServer().getPlayerList().op(BukkitReflection.getServerPlayer(p).getGameProfile());
        else MinecraftServer.getServer().getPlayerList().deop(BukkitReflection.getServerPlayer(p).getGameProfile());
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
        BukkitReflection.getServerPlayer(player).connection.send(packet);
    }
}
