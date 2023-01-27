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
import java.util.Arrays;

@Extension
public class PlayerExt {
    private static WeakReference<CommandBase> HelpCmd = null;
    public static void sendHelp(@This @NotNull Player p, @NotNull CommandBase cmd) {
        if(HelpCmd == null) {
            for (var help : CommandManager.Instance.commandList) {
                if(help.equals(Help.class)) {
                    HelpCmd = new WeakReference<>(help);
                }
            }
        }
        HelpCmd.get().Execute(p, new ArrayList<>(Arrays.asList(cmd.Command)));
    }
    public static void Reply(@This @NotNull Player player, String... message) {
        for(var msg : message) {
            player.sendMessage(CommandManager.COMMAND_PREFIX + msg.replaceAll("\n", "\n" + CommandManager.COMMAND_PREFIX));
        }
    }
    public static void SendPacket(@This @NotNull Player player, @NotNull Packet packet) {
        BukkitReflection.getServerPlayer(player).connection.send(packet);
    }
}
