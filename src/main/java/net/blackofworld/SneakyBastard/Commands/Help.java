package net.blackofworld.SneakyBastard.Commands;

import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Command.CommandManager;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@CommandInfo(command = "help", category = CommandCategory.Miscellaneous, syntax = "[command]")
@ExtensionMethod({Player.class, PlayerExt.class})
public final class Help extends CommandBase {
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        if (args.isEmpty()) {
            return;
        }
        String commandToSearch = args.get(0);
        if (commandToSearch.startsWith("-")) commandToSearch = commandToSearch.substring(1);
        CommandBase cmd = null;
        for (var c : CommandManager.Instance.commandList) {
            if (!c.Command.equalsIgnoreCase(commandToSearch)) continue;
            cmd = c;
        }
        if (cmd == null) {
            p.Reply(ChatColor.RED + "Command not found!");
            return;
        }
        p.Reply("Category: " + cmd.strCategory(),
                "Description: " + cmd.Description,
                "Syntax: " + cmd.Syntax);
    }
}
