package net.blackofworld.sneakybastard.Commands;

import net.blackofworld.sneakybastard.Command.CommandBase;
import net.blackofworld.sneakybastard.Command.CommandCategory;
import net.blackofworld.sneakybastard.Command.CommandInfo;
import net.blackofworld.sneakybastard.Command.CommandManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@CommandInfo(command = "help", category = CommandCategory.Miscellaneous, Syntax = "[command]")
public class Help extends CommandBase {
    @Override
    public void Execute(Player p, ArrayList<String> args) {
        if(args.isEmpty()) {return;}
        String commandToSearch = args[0];
        if(commandToSearch.startsWith("-")) commandToSearch = commandToSearch.substring(1);
        CommandBase cmd = null;
        for(var c : CommandManager.Instance.commandList) {
            if(!c.Command.equalsIgnoreCase(commandToSearch)) continue;
            cmd = c;
        }
        if(cmd == null) {
            p.Reply("Command not found!");
            return;
        }
        p.Reply("Category: "+cmd.Category.name(),
                "Description: "+cmd.Description,
                "Syntax: " + cmd.Syntax);
    }
}
