package SneakyBastard.extensions.org.bukkit.entity.Player;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.blackofworld.sneakybastard.Command.CommandBase;
import net.blackofworld.sneakybastard.Command.CommandManager;
import net.blackofworld.sneakybastard.Commands.Help;
import net.blackofworld.sneakybastard.Utils.BukkitReflection;
import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

@Extension
public class PlayerExt {
    private static WeakReference<CommandBase> HelpCmd = null;

    public static void sendHelp(@This @NotNull Player p, @NotNull CommandBase cmd, String... optional) {
        if (HelpCmd == null) {
            for (var help : CommandManager.Instance.commandList) {
                if (help.getClass().equals(Help.class)) {
                    HelpCmd = new WeakReference<>(help);
                    break;
                }
            }
        }
        p.Reply(optional);
        HelpCmd.get().Execute(p, new ArrayList<>(Collections.singletonList(cmd.Command)));
    }
    public static void sendHelp(@This @NotNull Player p, @NotNull CommandBase cmd) {
        sendHelp(p, cmd, null);
    }

    public static void Reply(@This @NotNull Player player, @NotNull String... message) {
        for (var msg : message) {
            player.sendMessage(CommandManager.COMMAND_PREFIX + msg.replaceAll("\n", "\n" + CommandManager.COMMAND_PREFIX));
        }
    }

    public static void SendPacket(@This @NotNull Player player, @NotNull Packet packet) {
        BukkitReflection.getServerPlayer(player).connection.send(packet);
    }
}
